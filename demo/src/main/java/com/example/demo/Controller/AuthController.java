package com.example.demo.Controller;

import com.example.demo.Model.User;
import com.example.demo.Service.UserServiceImpl;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserServiceImpl userService;

    public AuthController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
        @RequestBody User user
    ) {
        userService.registerUser(user);
        return ResponseEntity.ok(
            Map.of("message", "User registered successfully")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            userService.loginUser(user.getUsername(), user.getPassword());
            return ResponseEntity.ok(
                Map.of("message", "User logged in successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                Map.of("error", e.getMessage())
            );
        }
    }
}
