package com.example.demo.Controller;

import com.example.demo.Model.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        // TODO: Implement registration logic
        return "User registered successfully";
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        // TODO: Implement login logic
        return "User logged in successfully";
    }
}
