package com.example.demo;

import com.example.demo.model.Challenge;
import com.example.demo.model.User;
import com.example.demo.repository.ChallengeStatusRepository;
import com.example.demo.repository.ChallengeRepository;
import com.example.demo.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.charThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=password",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
})

public class ChallengeTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ChallengeRepository challengeRepo;

    @Autowired
    private ChallengeStatusRepository completedChallengeRepo;

    private User testUser;

    private Challenge testChallenge;

    @BeforeEach
    void setUp() {
        userRepo.deleteAll();
        challengeRepo.deleteAll();
        completedChallengeRepo.deleteAll();

        testUser = new User("Test", "User", "testuser", "test@gmail.com", "password", 100);
        userRepo.save(testUser);

        testChallenge = new Challenge("Test Challenge", "Test Description", 100, 10);
        challengeRepo.save(testChallenge);

        userRepo.flush();
        challengeRepo.flush();
    }

    @Test
    void testCreateChallenge() {
        ResponseEntity<Challenge> response = restTemplate.postForEntity(
                "/challenges",
                testChallenge,
                Challenge.class);

        challengeRepo.flush();
        System.out.println("Challenge: " + response.getBody());
        System.out.println("Status: " + response.getStatusCode());

        Challenge testChallenge = response.getBody();
        assertNotNull(testChallenge);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Test Challenge", testChallenge.getChallengeTitle());
        assertEquals("Test Description", testChallenge.getDescription());
        assertEquals(10, testChallenge.getDuration());
        assertEquals(100, testChallenge.getRewardPoints());
    }

    @Test
    void testChallengeSaved() {
        ResponseEntity<Challenge> response = restTemplate.postForEntity(
                "/challenges",
                testChallenge,
                Challenge.class);

        challengeRepo.flush();
        Challenge testChallenge = response.getBody();
        assertNotNull(testChallenge);
        Challenge foundChallenge = challengeRepo.findById(response.getBody().getChallengeID()).orElseThrow();

        assertEquals(testChallenge.getChallengeID(), foundChallenge.getChallengeID());
    }

    @Test
    void testGetAllChallenges() {
        challengeRepo.deleteAll();
        ResponseEntity<List<Challenge>> response = restTemplate.exchange(
                "/challenges",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Challenge>>() {
                });

        List<Challenge> challenges = response.getBody();

        // add 1 challenge
        assertEquals(0, challenges.size());
        // challengeRepo.save(testChallenge);
        restTemplate.postForEntity(
                "/challenges",
                testChallenge,
                Challenge.class);

        // Add 1 challenge
        restTemplate.postForEntity(
                "/challenges",
                testChallenge,
                Challenge.class);

        challengeRepo.flush();

        ResponseEntity<List<Challenge>> response1 = restTemplate.exchange(
                "/challenges",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Challenge>>() {
                });
        System.out.println("size: " + response1.getBody().size());
        // assertEquals(2, response1.getBody().size());
    }

    @Test
    void testUserReceivesPointsWhenCompletingChallenge() {
        // Record initial points
        int initialPoints = testUser.getPoints();
        int challengeReward = testChallenge.getRewardPoints();

        // Complete the challenge - assuming there's an endpoint like:
        // /users/{userId}/challenges/{challengeId}/complete
        ResponseEntity<User> response = restTemplate.postForEntity(
                "/users/" + testUser.getId() + "/challenges/" + testChallenge.getChallengeID() + "/complete",
                null,
                User.class);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        User updatedUser = response.getBody();
        assertNotNull(updatedUser);

        // Verify the user received the points
        assertEquals(initialPoints + challengeReward, updatedUser.getPoints());

        // Verify the points were persisted in the database
        User userFromDb = userRepo.findById(testUser.getId()).orElseThrow();
        assertEquals(initialPoints + challengeReward, userFromDb.getPoints());
    }
}
