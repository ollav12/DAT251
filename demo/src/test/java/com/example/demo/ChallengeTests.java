package com.example.demo;

import com.example.demo.controller.ChallengeController;
import com.example.demo.model.Challenge;
import com.example.demo.model.ChallengeStatus;
import com.example.demo.model.User;
import com.example.demo.repository.ChallengeStatusRepository;
import com.example.demo.repository.ChallengeRepository;
import com.example.demo.repository.UserRepository;

import java.util.List;

import com.example.demo.service.ChallengeStatusService;
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
import org.springframework.web.bind.annotation.PostMapping;

import static org.junit.jupiter.api.Assertions.*;

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

    @Autowired
    private ChallengeStatusService challengeStatusService;

    private User testUser;

    private Challenge testChallenge;

    @BeforeEach
    void setUp() {
        completedChallengeRepo.deleteAll();
        challengeRepo.deleteAll();
        userRepo.deleteAll();

        // Create test user
        testUser = new User("Test", "User", "testuser", "test@gmail.com", "password", 100);
        userRepo.save(testUser);

        // Create a navigation challenge
        testChallenge = new Challenge(
            "Visit Sustainability Page",
            "Navigate to the sustainability dashboard",
            50,  // reward points
            7,   // duration in days
            Challenge.ChallengeType.NAVIGATION,
            0.0, // target value (not relevant for navigation)
            "", // metric unit (not relevant for navigation)
            "/sustainability", // target route
            0   // required actions (not relevant for navigation)
        );
        challengeRepo.save(testChallenge);

        // Create a metric challenge
        Challenge metricChallenge = new Challenge(
            "Reduce Carbon Footprint",
            "Save 10kg of CO2 this week",
            100, // reward points
            7,   // duration in days
            Challenge.ChallengeType.METRIC,
            10.0, // target value
            "kg CO2", // metric unit
            "",  // target route (not relevant for metric)
            0    // required actions (not relevant for metric)
        );
        challengeRepo.save(metricChallenge);

        // Set up challenge status for navigation challenge
        ChallengeStatus navStatus = new ChallengeStatus();
        navStatus.setUserID(testUser.getId());
        navStatus.setChallenge(testChallenge);
        navStatus.setStatus(ChallengeStatus.Status.IN_PROGRESS);
        completedChallengeRepo.save(navStatus);

        userRepo.flush();
        challengeRepo.flush();
        completedChallengeRepo.flush();
    }

    @Test
    void testCreateChallenge() {
        // Create a new challenge
        Challenge actionChallenge = new Challenge(
            "Take Public Transport",
            "Use public transit 5 times this week",
            75,  // reward points
            7,   // duration in days
            Challenge.ChallengeType.ACTION,
            5.0, // target value (5 times)
            "trips", // metric unit
            "",  // target route (not relevant for this type)
            5    // required actions
        );

        ResponseEntity<Challenge> response = restTemplate.postForEntity(
            "/challenges",
            actionChallenge,
            Challenge.class);

        // Verify response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Challenge createdChallenge = response.getBody();
        assertNotNull(createdChallenge);
        assertEquals("Take Public Transport", createdChallenge.getChallengeTitle());
        assertEquals(Challenge.ChallengeType.ACTION, createdChallenge.getChallengeType());
        assertEquals(5, createdChallenge.getRequiredActions());
    }

    @Test
    void testGetChallengeById() {
        // Fetch existing challenge
        ResponseEntity<Challenge> response = restTemplate.getForEntity(
            "/challenges/" + testChallenge.getChallengeID(),
            Challenge.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Challenge fetchedChallenge = response.getBody();
        assertNotNull(fetchedChallenge);
        assertEquals(testChallenge.getChallengeID(), fetchedChallenge.getChallengeID());
        assertEquals(testChallenge.getChallengeTitle(), fetchedChallenge.getChallengeTitle());
        assertEquals(testChallenge.getChallengeType(), fetchedChallenge.getChallengeType());
    }

    @Test
    void testGetAllChallenges() {
        // Get all challenges
        ResponseEntity<List<Challenge>> response = restTemplate.exchange(
            "/challenges",
            HttpMethod.GET,
            null,
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Challenge> challenges = response.getBody();
        assertNotNull(challenges);
        // Should find at least the two challenges created in setUp()
        assertTrue(challenges.size() >= 2);
    }

    @Test
    void testUpdateChallenge() {
        // Update challenge description
        Challenge challenge = testChallenge;
        challenge.setDescription("Updated description");

        restTemplate.put("/challenges/" + challenge.getChallengeID(), challenge);

        // Verify update was successful
        ResponseEntity<Challenge> response = restTemplate.getForEntity(
            "/challenges/" + challenge.getChallengeID(),
            Challenge.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Challenge updatedChallenge = response.getBody();
        assertNotNull(updatedChallenge);
        assertEquals("Updated description", updatedChallenge.getDescription());
    }

    @Test
    void testAssignChallengeToUser() {
        // Create a streak challenge
        Challenge streakChallenge = new Challenge(
            "Daily Login",
            "Login for 5 consecutive days",
            50,
            5,
            Challenge.ChallengeType.STREAK,
            5.0, // 5 days streak
            "days",
            "",
            0
        );
        challengeRepo.save(streakChallenge);

        // Assign challenge to user
        ResponseEntity<ChallengeStatus> response = restTemplate.postForEntity(
            "/users/" + testUser.getId() + "/challenges/" + streakChallenge.getChallengeID() + "/assign",
            null,
            ChallengeStatus.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ChallengeStatus status = response.getBody();
        assertNotNull(status);
        assertEquals(testUser.getId(), status.getUserID());
        assertEquals(streakChallenge.getChallengeID(), status.getChallenge().getChallengeID());
        assertEquals(ChallengeStatus.Status.NOT_STARTED, status.getStatus());
    }

    /*@Test
    void testNavigationChallengeCompletion() {
        // Create a navigation challenge
        Challenge navChallenge = new Challenge();
        navChallenge.setChallengeType(Challenge.ChallengeType.NAVIGATION);
        navChallenge.setTargetRoute("/sustainability");
        navChallenge.setChallengeTitle("Visit Sustainability Page");
        navChallenge.setRewardPoints(50);
        challengeRepo.save(navChallenge);

        // Record a navigation event
        restTemplate.postForEntity(
            "/challenges/users/" + testUser.getId() + "/navigation",
            new ChallengeController.NavigationRequest("/sustainability"),
            Void.class);

        // Verify challenge was completed
        ChallengeStatus status = completedChallengeRepo.findByUserIDAndChallenge_ChallengeID(
            testUser.getId(), navChallenge.getChallengeID());
        assertEquals(ChallengeStatus.Status.COMPLETED, status.getStatus());
    }*/

    @Test
    void testUpdateMetricProgress() {
        // Create a metric challenge
        Challenge metricChallenge = new Challenge();
        metricChallenge.setChallengeType(Challenge.ChallengeType.METRIC);
        metricChallenge.setTargetValue(10.0);
        metricChallenge.setMetricUnit("kg CO2");
        metricChallenge.setChallengeTitle("Save 10kg CO2");
        metricChallenge.setRewardPoints(100);
        challengeRepo.save(metricChallenge);

        // Create challenge status for user
        ChallengeStatus status = new ChallengeStatus();
        status.setUserID(testUser.getId());
        status.setChallenge(metricChallenge);
        status.setStatus(ChallengeStatus.Status.IN_PROGRESS);
        status.setCurrentValue(0.0);
        completedChallengeRepo.save(status);

        // Update metric progress
        restTemplate.postForEntity(
            "/challenges/users/" + testUser.getId() + "/challenges/" + metricChallenge.getChallengeID() + "/metrics",
            new ChallengeController.MetricUpdate("kg CO2", 5.0),
            Void.class);

        // Verify progress update
        ChallengeStatus updatedStatus = completedChallengeRepo.findByUserIDAndChallenge_ChallengeID(
            testUser.getId(), metricChallenge.getChallengeID());
        assertEquals(5.0, updatedStatus.getCurrentValue());
        assertEquals(ChallengeStatus.Status.IN_PROGRESS, updatedStatus.getStatus());

        // Complete the challenge
        restTemplate.postForEntity(
            "/challenges/users/" + testUser.getId() + "/challenges/" + metricChallenge.getChallengeID() + "/metrics",
            new ChallengeController.MetricUpdate("kg CO2", 6.0),
            Void.class);

        // Verify challenge completion
        updatedStatus = completedChallengeRepo.findByUserIDAndChallenge_ChallengeID(
            testUser.getId(), metricChallenge.getChallengeID());
        assertEquals(11.0, updatedStatus.getCurrentValue());
        assertEquals(ChallengeStatus.Status.COMPLETED, updatedStatus.getStatus());
    }

    @Test
    void testCompleteChallenge() {
        // Create a challenge
        Challenge actionChallenge = new Challenge(
            "Complete Survey",
            "Take a sustainability survey",
            75,  // reward points
            7,   // duration in days
            Challenge.ChallengeType.ACTION,
            1.0, // target value
            "action", // metric unit
            "",  // target route
            1    // required actions
        );
        challengeRepo.save(actionChallenge);

        // Assign challenge to user
        /*ChallengeStatus status = new ChallengeStatus();
        status.setUserID(testUser.getId());
        status.setChallenge(actionChallenge);
        status.setStatus(ChallengeStatus.Status.IN_PROGRESS);
        completedChallengeRepo.save(status);*/
        challengeStatusService.assignChallenge(testUser.getId(), actionChallenge);

        // Update the status to IN_PROGRESS
        ChallengeStatus status = completedChallengeRepo.findByUserIDAndChallenge_ChallengeID(
            testUser.getId(), actionChallenge.getChallengeID());
        status.setStatus(ChallengeStatus.Status.IN_PROGRESS);
        completedChallengeRepo.save(status);
        completedChallengeRepo.flush();

        // Complete the challenge
        ResponseEntity<User> response = restTemplate.postForEntity(
                "/users/" + testUser.getId() + "/challenges/" + actionChallenge.getChallengeID() + "/complete",
            null,
            User.class);

        // Verify challenge was completed
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ChallengeStatus updatedStatus = completedChallengeRepo.findByUserIDAndChallenge_ChallengeID(
            testUser.getId(), actionChallenge.getChallengeID());
        assertNotNull(updatedStatus);
        assertEquals(ChallengeStatus.Status.COMPLETED, updatedStatus.getStatus());
    }

    @Test
    void testUserReceivesPointsWhenCompletingChallenge() {
        // Create a challenge with specific reward points
        int rewardPoints = 75;
        Challenge actionChallenge = new Challenge(
            "Carpool to Work",
            "Carpool with a colleague this week",
            rewardPoints,  // reward points
            7,   // duration in days
            Challenge.ChallengeType.ACTION,
            1.0, // target value
            "action", // metric unit
            "",  // target route
            1    // required actions
        );
        challengeRepo.save(actionChallenge);

        // Assign challenge to user
        challengeStatusService.assignChallenge(testUser.getId(), actionChallenge);

        // Update the status to IN_PROGRESS
        ChallengeStatus status = completedChallengeRepo.findByUserIDAndChallenge_ChallengeID(
                testUser.getId(), actionChallenge.getChallengeID());
        status.setStatus(ChallengeStatus.Status.IN_PROGRESS);
        completedChallengeRepo.save(status);
        completedChallengeRepo.flush();

        // Record initial points
        int initialPoints = testUser.getPoints();

        // Complete the challenge
        ResponseEntity<User> response = restTemplate.postForEntity(
                "/users/" + testUser.getId() + "/challenges/" + actionChallenge.getChallengeID() + "/complete",
            null,
            User.class);

        // Verify response status
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Fetch updated user to check points
        ResponseEntity<User> userResponse = restTemplate.getForEntity(
            "/users/" + testUser.getId(),
            User.class);

        User updatedUser = userResponse.getBody();
        assertNotNull(updatedUser);
        assertEquals(initialPoints + rewardPoints, updatedUser.getPoints());
    }

    //@Test
    void testChallengeStatusChangesToInProgressWhenMetricUpdated() {
        // Create a metric challenge
        Challenge metricChallenge = new Challenge(
            "Track Water Usage",
            "Save 20 liters of water",
            50,  // reward points
            7,   // duration in days
            Challenge.ChallengeType.METRIC,
            20.0, // target value
            "liters", // metric unit
            "",  // target route (not relevant for metric)
            0    // required actions (not relevant for metric)
        );
        challengeRepo.save(metricChallenge);

        // Assign challenge to user with initial NOT_STARTED status
        ChallengeStatus status = new ChallengeStatus();
        status.setUserID(testUser.getId());
        status.setChallenge(metricChallenge);
        status.setStatus(ChallengeStatus.Status.NOT_STARTED);
        status.setCurrentValue(0.0);
        completedChallengeRepo.save(status);
        completedChallengeRepo.flush();

        // Verify initial status is NOT_STARTED
        ChallengeStatus initialStatus = completedChallengeRepo.findByUserIDAndChallenge_ChallengeID(
            testUser.getId(), metricChallenge.getChallengeID());
        assertEquals(ChallengeStatus.Status.NOT_STARTED, initialStatus.getStatus());

        // Update metric with progress (but not enough to complete)
        restTemplate.postForEntity(
            "/challenges/users/" + testUser.getId() + "/metrics",
            new ChallengeController.MetricUpdate("liters", 5.0),
            Void.class);

        // Verify status changed to IN_PROGRESS
        ChallengeStatus updatedStatus = completedChallengeRepo.findByUserIDAndChallenge_ChallengeID(
            testUser.getId(), metricChallenge.getChallengeID());
        assertEquals(ChallengeStatus.Status.IN_PROGRESS, updatedStatus.getStatus());
        assertEquals(5.0, updatedStatus.getCurrentValue());
    }

    @Test
    void testChallengeStatusChangesToCompletedWhenMetricReachesTarget() {
        // Create a metric challenge with specific target
        Challenge metricChallenge = new Challenge(
            "Reduce Water Consumption",
            "Save 30 liters of water",
            75,  // reward points
            7,   // duration in days
            Challenge.ChallengeType.METRIC,
            30.0, // target value
            "liters", // metric unit
            "",  // target route (not relevant for metric)
            0    // required actions (not relevant for metric)
        );
        challengeRepo.save(metricChallenge);

        // Assign challenge to user with initial IN_PROGRESS status and some progress
        ChallengeStatus status = new ChallengeStatus();
        status.setUserID(testUser.getId());
        status.setChallenge(metricChallenge);
        status.setStatus(ChallengeStatus.Status.IN_PROGRESS);
        status.setCurrentValue(15.0); // Already some progress
        completedChallengeRepo.save(status);
        completedChallengeRepo.flush();

        // Add more progress, but still below target

                restTemplate.postForEntity(
            "/challenges/users/" + testUser.getId() + "/challenges/" + metricChallenge.getChallengeID() + "/metrics",
            new ChallengeController.MetricUpdate("liters", 10.0),
            Void.class);

        // Verify still IN_PROGRESS and correct current value
        ChallengeStatus updatedStatus = completedChallengeRepo.findByUserIDAndChallenge_ChallengeID(
            testUser.getId(), metricChallenge.getChallengeID());
        assertEquals(ChallengeStatus.Status.IN_PROGRESS, updatedStatus.getStatus());
        assertEquals(25.0, updatedStatus.getCurrentValue());

        // Add final increment that reaches target
        restTemplate.postForEntity(
            "/challenges/users/" + testUser.getId() + "/challenges/" + metricChallenge.getChallengeID() + "/metrics",
            new ChallengeController.MetricUpdate("liters", 5.0),
            Void.class);

        // Verify automatically changed to COMPLETED
        ChallengeStatus completedStatus = completedChallengeRepo.findByUserIDAndChallenge_ChallengeID(
            testUser.getId(), metricChallenge.getChallengeID());
        assertEquals(ChallengeStatus.Status.COMPLETED, completedStatus.getStatus());
        assertEquals(30.0, completedStatus.getCurrentValue());

        // Verify user received the reward points
        User updatedUser = userRepo.findById(testUser.getId()).orElseThrow();
        assertEquals(100 + 75, updatedUser.getPoints()); // Initial 100 + 75 reward
    }

}
