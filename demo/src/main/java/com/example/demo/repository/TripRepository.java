package com.example.demo.repository;

import com.example.demo.model.Trip;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    Trip findByUser(User user);

    Trip findById(long id);

    int countByUser(User user);

    @Query(
        "SELECT COALESCE(SUM(t.totalDistanceKm), 0) FROM Trip t WHERE t.user = :user"
    )
    double sumTotalDistanceKmByUser(User user);

    @Query(
        "SELECT COALESCE(SUM(t.totalDurationSeconds), 0) FROM Trip t WHERE t.user = :user"
    )
    double sumTotalDurationSecondsByUser(User user);

    @Query(
        "SELECT COALESCE(SUM(t.totalEmissionsCO2eKg), 0) FROM Trip t WHERE t.user = :user"
    )
    double sumTotalEmissionsByUser(User user);

    @Query(
        "SELECT COALESCE(SUM(t.savedEmissionsCO2eKg), 0) FROM Trip t WHERE t.user = :user"
    )
    double sumSavedEmissionsByUser(User user);
}
