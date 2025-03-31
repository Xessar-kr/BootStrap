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
    public void save(User user) {
        if (user.getId() != 0) {
            User existingUser = userDao.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            existingUser.setFirstName(user.getFirstName());
            existingUser.setSecondName(user.getSecondName());
            existingUser.setEmail(user.getEmail());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            if (user.getRoles() != null) {
                existingUser.setRoles(user.getRoles());
            }
            userDao.save(existingUser);
        } else {
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Пароль не может быть пустым при создании пользователя");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                Role role = roleRepository.findByName("ROLE_USER");
                if (role == null) {
                    throw new RuntimeException("Роль ROLE_USER не найдена");
                }
                Set<Role> roles = new HashSet<>(); // Изменяемая коллекция
                roles.add(role);
                user.setRoles(roles);
            }
            userDao.save(user);
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
