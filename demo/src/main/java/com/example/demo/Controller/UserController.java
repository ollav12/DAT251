package com.example.demo.Controller;

//import org.springframework.web.bind.annotation.CrossOrigin;
import com.example.demo.Model.User;
import com.example.demo.Service.UserServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
// @CrossOrigin(origins = "?")
@RequestMapping("/users")
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public String getUser(@PathVariable String username) {
        return "Hello " + username;
    }

    @GetMapping()
    public List<User> getUsers() {
        return userService.getAllUsers();
    }
}
