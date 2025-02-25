package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.Trip;
import com.example.demo.Model.User;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    Trip findByUser(User user);

    Trip findById(long id);
}