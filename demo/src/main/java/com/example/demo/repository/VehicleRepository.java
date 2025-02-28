package com.example.demo.repository;

import com.example.demo.model.User;
import com.example.demo.model.Vehicle;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    @Query("SELECT v FROM Vehicle v WHERE v.owner = :user")
    List<Vehicle> findByOwner(User user);
}
