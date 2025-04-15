package com.example.bootstrap.service;

import com.example.bootstrap.dao.RoleRepository;
import com.example.bootstrap.dao.UserDao;
import com.example.bootstrap.model.Role;
import com.example.bootstrap.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Override
    public User save(User user) {
        logger.info("Saving user: " + user);
        if (user.getId() != 0) {
            logger.info("Editing user with ID: " + user.getId());
            User existingUser = userDao.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            logger.info("Existing user: " + existingUser);
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            logger.info("User before saving: " + user);
            User savedUser = userDao.save(user); // Сохраняем переданный объект
            logger.info("User saved successfully: " + savedUser);
            return savedUser;
        } else {
            logger.info("Creating new user");
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
            logger.info("New user saved successfully: " + savedUser);
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

    @Override
    public User createUserWithRoles(User user, List<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                throw new IllegalArgumentException("Role not found: " + roleName);
            }
            roles.add(role);
        }
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Предполагаем, что пароль кодируется
        return userDao.save(user);
    }

    @Override
    public void updateUserRoles(User user, List<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                throw new IllegalArgumentException("Role not found: " + roleName);
            }
            roles.add(role);
        }
        user.setRoles(roles);
    }
}
