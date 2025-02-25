package com.example.demo.Repository;

import com.example.demo.Model.TransportationMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportationModeRepository extends JpaRepository <TransportationMode, Long> {
    //TransportationMode findByTransportationMode(String transportationMode);
}
