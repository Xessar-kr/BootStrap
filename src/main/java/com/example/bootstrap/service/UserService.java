package com.example.bootstrap.service;

import com.example.bootstrap.model.User;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

public interface UserService {
    User save(User user);
    User findById(int id);
    void deleteById(int id);
    List<User> findAll();

    Object findByEmail(String email);

    User createUserWithRoles(User user, List<String> roleNames) throws DataIntegrityViolationException;
    void updateUserRoles(User user, List<String> roleNames);
}
