package com.example.demo.service;

import com.example.demo.model.User;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service interface for managing user authentication operations.
 * Provides methods for user registration and login functionality.
 */

public interface UserService {
    /**
     * Registers a new user in the system and adds default cosmetics.
     *
     * @param user the user entity to be registered
     * @return the registered user entity
     * @throws IllegalStateException if a user with the same username already exists
     */
    User registerUser(User user);

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return the authenticated user entity
     * @throws NoSuchElementException if the user is not found
     * @throws SecurityException      if the password is invalid
     */
    User loginUser(String username, String password);

    List<User> getAllUsers();

    User getUser(long id);

    String deleteUser(long id);

    String updateUser(User user, long id);
}
