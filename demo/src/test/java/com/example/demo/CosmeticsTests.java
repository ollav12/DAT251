package com.example.demo;

import com.example.demo.model.Cosmetics;
import com.example.demo.model.CosmeticsType;
import com.example.demo.model.User;
import com.example.demo.repository.CosmeticsRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=password",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
})

public class CosmeticsTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CosmeticsRepository cosmeticsRepo;

    @BeforeEach
    void setUp() {
        userRepo.deleteAll();
        cosmeticsRepo.deleteAll();

        // Add the default cosmetics to the H2 database
        Cosmetics defaultPicture = new Cosmetics("Default Profile Picture", 0, "A Profile picture of a spire", "default_picture.png", CosmeticsType.PROFILE_PICTURE);
        Cosmetics defaultFireBorder = new Cosmetics("Default Fire border", 0, "First Fire border", "fireborder_1.png", CosmeticsType.BORDER);
        Cosmetics defaultGreenBorder = new Cosmetics("Default Plant border", 0, "First Plant border", "greenborder_1.png", CosmeticsType.BORDER);

        cosmeticsRepo.saveAll(Arrays.asList(defaultPicture, defaultFireBorder, defaultGreenBorder));

        // Additional cosmetics available in the shop
        Cosmetics elephantPicture = new Cosmetics("Elephant Profile Picture", 100, "A profile picture of an elephant", "elephant.png", CosmeticsType.PROFILE_PICTURE);
        Cosmetics foxPicture = new Cosmetics("Fox Profile Picture", 100, "A profile picture of a fox", "fox.png", CosmeticsType.PROFILE_PICTURE);
        Cosmetics giraffePicture = new Cosmetics("Giraffe Profile Picture", 150, "A profile picture of a giraffe", "giraffe.png", CosmeticsType.PROFILE_PICTURE);
        Cosmetics lionPicture = new Cosmetics("Lion Profile Picture", 200, "A profile picture of a lion", "lion.png", CosmeticsType.PROFILE_PICTURE);
        Cosmetics fireBorder2 = new Cosmetics("Advanced Fire Border", 150, "Second tier fire border", "fireborder_2.png", CosmeticsType.BORDER);
        Cosmetics fireBorder3 = new Cosmetics("Elite Fire Border", 250, "Third tier fire border", "fireborder_3.png", CosmeticsType.BORDER);
        Cosmetics greenBorder2 = new Cosmetics("Advanced Plant Border", 150, "Second tier plant border", "greenborder_2.png", CosmeticsType.BORDER);
        Cosmetics greenBorder3 = new Cosmetics("Elite Plant Border", 250, "Third tier plant border", "greenborder_3.png", CosmeticsType.BORDER);

        cosmeticsRepo.saveAll(Arrays.asList(
                elephantPicture, foxPicture, giraffePicture, lionPicture,
                fireBorder2, fireBorder3, greenBorder2, greenBorder3
        ));

        // Register a new user
        var registerRequest = new HashMap<String, String>();
        registerRequest.put("firstName", "NewTest");
        registerRequest.put("lastName", "User");
        registerRequest.put("username", "newuser123");
        registerRequest.put("email", "newuser@test.com");
        registerRequest.put("password", "password123");

        var registerResponse = restTemplate.postForEntity("/auth/register", registerRequest, Object.class);
        assertEquals(200, registerResponse.getStatusCodeValue());

        userRepo.flush();
        cosmeticsRepo.flush();
    }

    /**
     * An inventory is successfully instantiated and returned with the default cosmetics of a new user
     */
    @Test
    void testGetInventory() {
        // Check that the user has been created
        User newUser = userRepo.findByUsername("newuser123");
        assertNotNull(newUser);

        // Retrieve the inventory items for the new user
        var inventoryResponse = restTemplate.getForEntity(
                "/cosmetics/inventory?userId=" + newUser.getId(),
                Cosmetics[].class
        );
        var cosmetics = inventoryResponse.getBody();

        assertNotNull(cosmetics);
        assertEquals(3, cosmetics.length);
    }

    /**
     * The shop is successfully instantiated and contains all available cosmetics
     */
    @Test
    void testGetShop() {
        // Check that the user has been created
        User newUser = userRepo.findByUsername("newuser123");
        assertNotNull(newUser);

        // Retrieve the shop items for the new user
        var shopResponse = restTemplate.getForEntity(
                "/cosmetics/shop?userId=" + newUser.getId(),
                Cosmetics[].class
        );
        var cosmetics = shopResponse.getBody();

        assertNotNull(cosmetics);
        assertEquals(11, cosmetics.length);
    }

    /**
     * User can purchase cosmetics given enough money, and they appear in their inventory
     */
    @Test
    void testPurchaseCosmeticsWithMoney() {
        User newUser = userRepo.findByUsername("newuser123");
        assertNotNull(newUser);

        // Add enough points to the user's account
        newUser.setPoints(300); // Lion Profile Picture costs 200 points
        userRepo.save(newUser);

        // Check the number of items in inventory before purchase
        var inventoryResponse = restTemplate.getForEntity(
                "/cosmetics/inventory?userId=" + newUser.getId(),
                Cosmetics[].class
        );
        var cosmetics = inventoryResponse.getBody();
        assertNotNull(cosmetics);
        assertEquals(3, cosmetics.length);

        // Purchase a new cosmetic
        var purchaseRequest = new HashMap<String, String>();
        purchaseRequest.put("name", "Lion Profile Picture");
        var purchaseResponse = restTemplate.postForEntity(
                "/cosmetics/purchaseCosmetics?userId=" + newUser.getId(),
                purchaseRequest,
                Cosmetics.class
        );

        // Check that the purchase was successful
        assertEquals(200, purchaseResponse.getStatusCodeValue());

        // Check the number of items in inventory after purchase
        inventoryResponse = restTemplate.getForEntity(
                "/cosmetics/inventory?userId=" + newUser.getId(),
                Cosmetics[].class
        );
        cosmetics = inventoryResponse.getBody();
        assertNotNull(cosmetics);
        assertEquals(4, cosmetics.length);
    }

    /**
     * User can not purchase cosmetics because of insufficient funds, and the item does not appear in their inventory
     */
    @Test
    void testPurchaseCosmeticsWithoutMoney() {
        User newUser = userRepo.findByUsername("newuser123");
        assertNotNull(newUser);

        // Check the number of items in inventory before purchase
        var inventoryResponse = restTemplate.getForEntity(
                "/cosmetics/inventory?userId=" + newUser.getId(),
                Cosmetics[].class
        );
        var cosmetics = inventoryResponse.getBody();
        assertNotNull(cosmetics);
        assertEquals(3, cosmetics.length);

        // Attempt to purchase a new cosmetic
        var purchaseRequest = new HashMap<String, String>();
        purchaseRequest.put("name", "Lion Profile Picture");
        var purchaseResponse = restTemplate.postForEntity(
                "/cosmetics/purchaseCosmetics?userId=" + newUser.getId(),
                purchaseRequest,
                Cosmetics.class
        );

        // Check that the purchase was unsuccessful
        assertEquals(500, purchaseResponse.getStatusCodeValue());

        // Check that the number of items in inventory remains the same as before
        inventoryResponse = restTemplate.getForEntity(
                "/cosmetics/inventory?userId=" + newUser.getId(),
                Cosmetics[].class
        );
        cosmetics = inventoryResponse.getBody();
        assertNotNull(cosmetics);
        assertEquals(3, cosmetics.length);
    }

    /**
     * User can equip cosmetics and their profile is updated
     */
    @Test
    void testEquipCosmetics() {
        User newUser = userRepo.findByUsername("newuser123");
        assertNotNull(newUser);

        // Find default cosmetics by name instead of assuming IDs
        Cosmetics defaultFireBorder = cosmeticsRepo.findByName("Default Fire border");
        Cosmetics defaultProfilePic = cosmeticsRepo.findByName("Default Profile Picture");

        // Check equipped border and profile icon
        assertEquals(defaultFireBorder.getId(), newUser.getEquippedBorder().getId());
        assertEquals(defaultProfilePic.getId(), newUser.getEquippedProfilePicture().getId());

        // Find cosmetics to equip by name
        Cosmetics elitePlantBorder = cosmeticsRepo.findByName("Elite Plant Border");
        Cosmetics giraffePicture = cosmeticsRepo.findByName("Giraffe Profile Picture");

        // Equip new cosmetics
        restTemplate.put(
                "/cosmetics/equip/" + elitePlantBorder.getId() + "?userId=" + newUser.getId(),
                null
        );

        restTemplate.put(
                "/cosmetics/equip/" + giraffePicture.getId() + "?userId=" + newUser.getId(),
                null
        );

        // Check that the user's profile has been updated
        newUser = userRepo.findByUsername("newuser123");

        // Check equipped border and profile icon
        assertEquals(elitePlantBorder.getId(), newUser.getEquippedBorder().getId());
        assertEquals(giraffePicture.getId(), newUser.getEquippedProfilePicture().getId());
    }
}
