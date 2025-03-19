package com.example.demo.service;

import com.example.demo.model.Cosmetics;
import com.example.demo.repository.CosmeticsRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CosmeticsService {

    private final CosmeticsRepository cosmeticsRepository;
    private final UserRepository userRepository;

    public CosmeticsService(CosmeticsRepository cosmeticsRepository, UserRepository userRepository) {
        this.cosmeticsRepository = cosmeticsRepository;
        this.userRepository = userRepository;
    }

    public List<Cosmetics> getInventory(Long userId) {
        //return cosmeticsRepository.findInventoryByUserId(userId);
        return new ArrayList<>(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getOwnedCosmetics());
    }

    public List<Cosmetics> getShop() { return cosmeticsRepository.getAll(); }

    public Cosmetics purchaseCosmetics(Long userId, String name) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        var cosmetics = cosmeticsRepository.findByName(name);
        if (cosmetics == null) {
            throw new RuntimeException("Cosmetics not found");
        }
        user.getOwnedCosmetics().add(cosmetics);
        userRepository.save(user);
        return cosmetics;
    }

    public Cosmetics equipCosmetics(Long userId, Long cosmeticsId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        var cosmetics = cosmeticsRepository.findById(cosmeticsId)
                .orElseThrow(() -> new RuntimeException("Cosmetics not found"));

        // First unequip any existing cosmetic of the same type
        switch (cosmetics.getCategory()) {
            case BORDER -> {
                // Clear existing border relationship if exists
                if (user.getEquippedBorder() != null) {
                    user.setEquippedBorder(null);
                    userRepository.save(user); // Save changes to avoid constraint issues
                }
                user.setEquippedBorder(cosmetics);
            }
            case PROFILE_PICTURE -> {
                // Clear existing profile picture relationship if exists
                if (user.getEquippedProfilePicture() != null) {
                    user.setEquippedProfilePicture(null);
                    userRepository.save(user); // Save changes to avoid constraint issues
                }
                user.setEquippedProfilePicture(cosmetics);
            }
        }

        userRepository.save(user);
        return cosmetics;
    }
}
