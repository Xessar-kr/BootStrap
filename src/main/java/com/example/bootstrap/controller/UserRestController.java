package com.example.bootstrap.controller;

import com.example.bootstrap.dao.RoleRepository;
import com.example.bootstrap.model.Role;
import com.example.bootstrap.model.User;
import com.example.bootstrap.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("admin-rest/users")
public class UserRestController {
    private final UserService userService;
    private final RoleRepository roleRepository;

    public UserRestController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                List<String> roleNames = user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList());
                User savedUser = userService.createUserWithRoles(user, roleNames);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User user) {
        User existingUser = userService.findById(id);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        try {
            existingUser.setFirstName(user.getFirstName());
            existingUser.setSecondName(user.getSecondName());
            existingUser.setEmail(user.getEmail());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(user.getPassword()); // Кодирование в save
            }
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                List<String> roleNames = user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList());
                userService.updateUserRoles(existingUser, roleNames);
            }
            userService.save(existingUser);
            return ResponseEntity.ok(existingUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteUser ( @PathVariable int id){
            User user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
    }
