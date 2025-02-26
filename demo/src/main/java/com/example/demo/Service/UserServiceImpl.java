package com.example.demo.Service;

import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(
        UserRepository userRepository,
        BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalStateException("User already exists");
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NoSuchElementException("User not found");
        }
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new SecurityException("Invalid password");
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(long id) {
        return userRepository
            .findById(id)
            .orElseThrow(() ->
                new NoSuchElementException(
                    "User with id: " + id + " was not found"
                )
            );
    }

    @Override
    public String deleteUser(long id) {
        User user = userRepository
            .findById(id)
            .orElseThrow(() ->
                new NoSuchElementException(
                    "User with id: " + id + " was not found"
                )
            );
        userRepository.delete(user);
        return "User with id: " + id + " was successfully deleted";
    }

    @Override
    public String updateUser(User updatedUser, long id) {
        User exsistingUser = userRepository
            .findById(id)
            .orElseThrow(() ->
                new NoSuchElementException(
                    "User with id: " + id + " was not found"
                )
            );

        exsistingUser.setEmail(updatedUser.getEmail());
        exsistingUser.setFirstName(updatedUser.getFirstName());
        exsistingUser.setLastName(updatedUser.getLastName());
        exsistingUser.setPoints(updatedUser.getPoints());
        exsistingUser.setUsername(updatedUser.getUsername());

        if (
            !bCryptPasswordEncoder.matches(
                updatedUser.getPassword(),
                exsistingUser.getPassword()
            )
        ) {
            exsistingUser.setPassword(
                bCryptPasswordEncoder.encode(updatedUser.getPassword())
            );
        }

        userRepository.save(exsistingUser);
        return "User with id: " + id + "has been successfully updated";
    }
}
