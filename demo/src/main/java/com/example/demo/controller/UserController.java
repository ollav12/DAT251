package com.example.demo.controller;

import com.example.demo.model.Challenge;
import com.example.demo.model.User;
import com.example.demo.service.ChallengeService;
import com.example.demo.service.ChallengeStatusService;
import com.example.demo.service.UserServiceImpl;
import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
// @CrossOrigin(origins = "?")
@RequestMapping("/users")
public class UserController {

    private final UserServiceImpl userService;
    private final ChallengeService challengeService;
    private final ChallengeStatusService challengeStatusService;

    public UserController(UserServiceImpl userService, ChallengeService challengeService,
            ChallengeStatusService challengeStatusService) {
        this.userService = userService;
        this.challengeService = challengeService;
        this.challengeStatusService = challengeStatusService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable long id) {
        try {
            User user = userService.getUser(id);
            return ResponseEntity.ok().body(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id) {
        try {
            String result = userService.deleteUser(id);
            return ResponseEntity.ok(result);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    "An error occurred while deleting the user");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(
            @RequestBody User updatedUser,
            @PathVariable long id) {
        try {
            String result = userService.updateUser(updatedUser, id);
            return ResponseEntity.ok().body(result);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    "An error occurred while updating the user");
        }
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            List<Challenge> challenges = challengeService.getAllChallenges();
            for (Challenge challenge : challenges) {
                challengeStatusService.startChallenge(user.getId(), challenge);
            }

            return ResponseEntity.created(
                    new URI("/users/" + registeredUser.getId())).body(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
