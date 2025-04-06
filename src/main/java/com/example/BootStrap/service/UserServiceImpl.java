package com.example.BootStrap.service;

import com.example.BootStrap.dao.RoleRepository;
import com.example.BootStrap.dao.UserDao;
import com.example.BootStrap.model.Role;
import com.example.BootStrap.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public User save(User user) {
        System.out.println("Saving user: " + user);
        if (user.getId() != 0) {
            System.out.println("Editing user with ID: " + user.getId());
            User existingUser = userDao.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            System.out.println("Existing user: " + existingUser);
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            System.out.println("User before saving: " + user);
            User savedUser = userDao.save(user); // Сохраняем переданный объект
            System.out.println("User saved successfully: " + savedUser);
            return savedUser;
        } else {
            System.out.println("Creating new user");
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Пароль не может быть пустым при создании пользователя");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                Role role = roleRepository.findByName("ROLE_USER");
                if (role == null) {
                    throw new RuntimeException("Роль ROLE_USER не найдена");
                }
                Set<Role> roles = new HashSet<>();
                roles.add(role);
                user.setRoles(roles);
            }
            User savedUser = userDao.save(user);
            System.out.println("New user saved successfully: " + savedUser);
            return savedUser;
        }
    }

    @Override
    public User findById(int id) {
        return userDao.findById(id).orElse(null);
    }

    @Override
    public void deleteById(int id) {
        userDao.deleteById(id);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }
}
