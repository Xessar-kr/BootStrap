package com.example.BootStrap.controller;

import com.example.BootStrap.dao.RoleRepository;
import com.example.BootStrap.model.Role;
import com.example.BootStrap.model.User;
import com.example.BootStrap.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("newUser", new User());
        model.addAttribute("currentSection", "admin");

        return "admin";
    }

    @PostMapping("/admin/create")
    public String createUser(@ModelAttribute("newUser") User user, @RequestParam("role") String role, Model model) {
        try {
            Role userRole = roleRepository.findByName(role);
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);
            userService.save(user);
            return "redirect:/admin";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "This user already exists");
            model.addAttribute("user", user);
            model.addAttribute("newUser", user);
            model.addAttribute("currentSection", "admin");
            return "admin";
        }
    }

    @GetMapping("/admin/edit/{id}")
    public String editUser(@PathVariable("id") int id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            model.addAttribute("error", "User with ID " + id + " not found");
            model.addAttribute("users", userService.findAll());
            model.addAttribute("newUser", new User());
            model.addAttribute("currentSection", "admin");
            return "admin";
        }
        model.addAttribute("user", user);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("newUser", new User());
        model.addAttribute("currentSection", "admin");
        return "admin";
    }

    @PostMapping("/admin/update")
    public String updateUser(
            @RequestParam("id") int id,
            @RequestParam("firstName") String firstName,
            @RequestParam("secondName") String secondName,
            @RequestParam("email") String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam("role") String role,
            Model model) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                model.addAttribute("error", "User with ID " + id + " not found");
                model.addAttribute("users", userService.findAll());
                model.addAttribute("newUser", new User());
                model.addAttribute("currentSection", "admin");
                return "admin";
            }

            user.setFirstName(firstName);
            user.setSecondName(secondName);
            user.setEmail(email);
            if (password != null && !password.isEmpty()) {
                user.setPassword(password); // Пароль будет закодирован в UserServiceImpl
            }

            Role userRole = roleRepository.findByName(role);
            if (userRole == null) {
                throw new RuntimeException("Role not found: " + role);
            }
            Set<Role> roles = new HashSet<>(); // Создаем изменяемую коллекцию
            roles.add(userRole);
            user.setRoles(roles);

            userService.save(user);
            return "redirect:/admin";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "This user already exists");
            model.addAttribute("users", userService.findAll());
            model.addAttribute("newUser", new User());
            model.addAttribute("currentSection", "admin");
            return "admin";
        }
    }

    @GetMapping("/admin/delete/{id}")
    public String showDeleteUser(@PathVariable("id") int id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            model.addAttribute("error", "User not found");
            model.addAttribute("user", userService.findAll());
            model.addAttribute("currentSection", "admin");
            model.addAttribute("newUser", new User());
            return "admin";
        }
        model.addAttribute("userToDelete", user);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("newUser", new User());
        model.addAttribute("currentSection", "admin"); // Мы всё ещё в разделе admin
        return "redirect:/admin";
    }

    @PostMapping("/admin/delete")
    public String deleteUser(@RequestParam("id") int id, Model model) {
        userService.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping("/user")
    public String userPage(Model model) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("users", Collections.singletonList(currentUser));
        model.addAttribute("currentSection", "user");
        return "user";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

}
