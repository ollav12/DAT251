package com.example.demo.Repository;

import com.example.demo.Model.Trip;
import com.example.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    Trip findByUser(User user);

    Trip findById(long id);
}
