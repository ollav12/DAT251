package com.example.demo.repository;

import com.example.demo.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    public record LeaderboardRow(
        String username,
        String firstName,
        String lastName,
        double totalEmissions
    ) {}

    @Query(
        "SELECT new com.example.demo.repository.UserRepository$LeaderboardRow(u.username, u.firstName, u.lastName, SUM(t.totalEmissionsCO2eKg) AS totalEmissions) " +
        "FROM User u " +
        "JOIN Trip t ON t.user = u " +
        "GROUP BY u.username, u.firstName, u.lastName " +
        "ORDER BY totalEmissions ASC"
    )
    List<LeaderboardRow> getLeaderboard();
}
