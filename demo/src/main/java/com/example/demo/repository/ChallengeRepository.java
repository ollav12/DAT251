package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Challenge;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

}
