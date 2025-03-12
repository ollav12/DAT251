package com.example.demo;

import com.example.demo.model.Challenge;
import com.example.demo.model.User;
import com.example.demo.repository.UserChallengeRepository;
import com.example.demo.repository.ChallengeRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
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

public class ChallangeTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ChallengeRepository challengeRepo;

    @Autowired
    private UserChallengeRepository completedChallengeRepo;

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

}
