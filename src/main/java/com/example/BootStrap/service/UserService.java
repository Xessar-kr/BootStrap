package com.example.BootStrap.service;

import com.example.BootStrap.model.User;

import java.util.List;

public interface UserService {
    User save(User user);
    User findById(int id);
    void deleteById(int id);
    List<User> findAll();

    Object findByEmail(String email);
}
