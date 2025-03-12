package com.example.demo.repository;

import com.example.demo.model.Challenge;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.UserChallenge;

import java.util.List;

@Repository
public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
    List<UserChallenge> findByUser(User user);

    List<UserChallenge> findByUserAndStatus(User user, UserChallenge.Status status);

}
