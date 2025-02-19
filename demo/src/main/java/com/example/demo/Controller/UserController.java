package com.example.demo.Controller;

//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
// @CrossOrigin(origins = "?")
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{username}")
    public String getUser(@PathVariable String username) {
        return "Hello " + username;
    }
}
