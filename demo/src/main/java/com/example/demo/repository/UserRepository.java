package com.example.demo.repository;

import com.example.demo.model.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    // User findById(long id);

    public record LeaderboardRow(

        String username,
        String firstName,
        String lastName,
        double value
    ) {}

    @Query(
        "SELECT new com.example.demo.repository.UserRepository$LeaderboardRow(u.username, u.firstName, u.lastName, SUM(t.totalEmissionsCO2eKg) AS value) " +
        "FROM User u " +
        "JOIN Trip t ON t.user = u " +
        "WHERE t.createdAt >= :since " +
        "GROUP BY u.username, u.firstName, u.lastName " +
        "ORDER BY value ASC"
    )
    List<LeaderboardRow> getLeaderboardTotalEmissions(LocalDateTime since);

    @Query(
        "SELECT new com.example.demo.repository.UserRepository$LeaderboardRow(u.username, u.firstName, u.lastName, SUM(t.savedEmissionsCO2eKg) AS value) " +
        "FROM User u " +
        "JOIN Trip t ON t.user = u " +
        "WHERE t.createdAt >= :since " +
        "GROUP BY u.username, u.firstName, u.lastName " +
        "ORDER BY value DESC"
    )
    List<LeaderboardRow> getLeaderboardTotalSavedEmissions(LocalDateTime since);

    @Query(
        "SELECT new com.example.demo.repository.UserRepository$LeaderboardRow(u.username, u.firstName, u.lastName, AVG(t.totalEmissionsCO2eKg / t.totalDistanceKm) AS value) " +
        "FROM User u " +
        "JOIN Trip t ON t.user = u " +
        "WHERE t.createdAt >= :since " +
        "GROUP BY u.username, u.firstName, u.lastName " +
        "ORDER BY value ASC"
    )
    List<LeaderboardRow> getLeaderboardAverageEmissionsPerKm(
        LocalDateTime since
    );

    @Query(
        "SELECT new com.example.demo.repository.UserRepository$LeaderboardRow(u.username, u.firstName, u.lastName, SUM(t.totalDistanceKm) AS value) " +
        "FROM User u " +
        "JOIN Trip t ON t.user = u " +
        "WHERE t.createdAt >= :since " +
        "GROUP BY u.username, u.firstName, u.lastName " +
        "ORDER BY value DESC"
    )
    List<LeaderboardRow> getLeaderboardTotalDistance(LocalDateTime since);

    @Query(
        "SELECT new com.example.demo.repository.UserRepository$LeaderboardRow(u.username, u.firstName, u.lastName, SUM(t.totalDurationSeconds) AS value) " +
        "FROM User u " +
        "JOIN Trip t ON t.user = u " +
        "WHERE t.createdAt >= :since " +
        "GROUP BY u.username, u.firstName, u.lastName " +
        "ORDER BY value DESC"
    )
    List<LeaderboardRow> getLeaderboardTotalDuration(LocalDateTime since);
}
