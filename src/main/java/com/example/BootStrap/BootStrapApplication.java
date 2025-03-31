package com.example.BootStrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class BootStrapApplication {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("user: " + encoder.encode("user"));
        System.out.println("admin: " + encoder.encode("admin"));
        SpringApplication.run(BootStrapApplication.class, args);
    }
}