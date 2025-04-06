package com.example.BootStrap.controller;

import com.example.BootStrap.dao.RoleRepository;
import com.example.BootStrap.model.Role;
import com.example.BootStrap.model.User;
import com.example.BootStrap.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                String roleName = user.getRoles().iterator().next().getName();
                Role role = roleRepository.findByName(roleName);
                if (role == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
                // Используем изменяемую коллекцию
                Set<Role> roles = new HashSet<>();
                roles.add(role);
                user.setRoles(roles);
            }
            userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User user) {
        System.out.println("Updating user with ID: " + id);
        System.out.println("Received user data: " + user);
        User existingUser = userService.findById(id);
        if (existingUser == null) {
            System.out.println("User not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        try {
            // Обновляем поля existingUser на основе user
            existingUser.setFirstName(user.getFirstName());
            existingUser.setSecondName(user.getSecondName());
            existingUser.setEmail(user.getEmail());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(user.getPassword()); // Пароль будет зашифрован в UserServiceImpl
            }
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                String roleName = user.getRoles().iterator().next().getName();
                System.out.println("Role name: " + roleName);
                Role role = roleRepository.findByName(roleName);
                if (role == null) {
                    System.out.println("Role not found: " + roleName);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
                System.out.println("Found role: " + role);
                Set<Role> roles = new HashSet<>();
                roles.add(role);
                existingUser.setRoles(roles);
            } else {
                System.out.println("No roles provided, keeping existing roles: " + existingUser.getRoles());
            }
            System.out.println("User before saving: " + existingUser);
            userService.save(existingUser); // Сохраняем existingUser
            User updatedUser = userService.findById(id);
            System.out.println("Updated user: " + updatedUser);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            System.out.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
