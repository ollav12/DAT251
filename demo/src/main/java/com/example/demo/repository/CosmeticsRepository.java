package com.example.demo.repository;

import com.example.demo.model.Cosmetics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CosmeticsRepository extends JpaRepository<Cosmetics, Long> {
    Cosmetics findByName(String name);

    @Query(
        value = "SELECT * FROM cosmetics",
        nativeQuery = true
    )
    List<Cosmetics> getAll();

    @Query("SELECT c FROM Cosmetics c JOIN c.owners u WHERE u.id = :userId")
    List<Cosmetics> findInventoryByUserId(@Param("userId") Long userId);
}
