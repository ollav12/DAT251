package com.example.demo.service;

import com.example.demo.model.Cosmetics;
import com.example.demo.model.User;
import com.example.demo.repository.CosmeticsRepository;
import com.example.demo.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CosmeticsRepository cosmeticsRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(
        UserRepository userRepository,
        CosmeticsRepository cosmeticsRepository,
        BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.userRepository = userRepository;
        this.cosmeticsRepository = cosmeticsRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalStateException("User already exists");
        }
        Cosmetics defaultBorder = cosmeticsRepository.findByName("Default Fire border");
        Cosmetics defaultPlantBorder = cosmeticsRepository.findByName("Default Plant border");
        Cosmetics defaultProfilePicture = cosmeticsRepository.findByName("Default Profile Picture");

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        if(user.getOwnedCosmetics() == null) {
            user.setOwnedCosmetics(new HashSet<>());
        }

        user.getOwnedCosmetics().add(defaultBorder);
        user.getOwnedCosmetics().add(defaultPlantBorder);
        user.getOwnedCosmetics().add(defaultProfilePicture);

        user.setEquippedBorder(defaultBorder);
        user.setEquippedProfilePicture(defaultProfilePicture);
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
