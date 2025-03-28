package com.example.demo.controller;

import com.example.demo.model.Challenge;
import com.example.demo.service.ChallengeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    /**
     * Get all challenges
     * 
     * @return List of challenges
     */
    @GetMapping
    public ResponseEntity<List<Challenge>> getAllChallenges() {
        return ResponseEntity.ok(challengeService.getAllChallenges());
    }

    /**
     * Get a challenge by ID
     * 
     * @param id
     * @return challenge
     */
    @GetMapping("/{id}")
    public ResponseEntity<Challenge> getChallengeById(@PathVariable long id) {
        return ResponseEntity.ok(challengeService.getChallenge(id));
    }

    /**
     * Store challenge in database
     * 
     * @param challenge
     * @return challenge id
     */
    @PostMapping
    public ResponseEntity<Long> createChallenge(@RequestBody Challenge challenge) {
        challengeService.addChallenge(challenge);
        return new ResponseEntity<>(challenge.getChallengeID(), HttpStatus.CREATED);
    }

    /**
     * Update a challenge by ID and Challenge
     * 
     * @param id
     * @param challenge
     * @return Updated Challenge
     */
    @PutMapping("/{id}")
    public ResponseEntity<Challenge> updateChallenge(@PathVariable long id, @RequestBody Challenge challenge) {
        Challenge updatedChallenge = challengeService.updateChallenge(id, challenge);
        return ResponseEntity.ok(updatedChallenge);
    }

    /**
     * Delete a challenge by ID
     * 
     * @param id
     * @return Response Message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteChallenge(@PathVariable long id) {
        boolean deleted = challengeService.deleteChallenge(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No challenge with id " + id + " exists"));
        }
        return ResponseEntity.ok(Map.of("message", "Challenge deleted successfully"));
    }
}