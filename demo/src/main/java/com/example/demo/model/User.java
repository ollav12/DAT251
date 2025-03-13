package com.example.demo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String username;

    private String email;
    private String password;
    private int points;

    @OneToOne
    @JoinColumn(name = "default_vehicle_id")
    private Vehicle defaultVehicle;

    @OneToMany(
        mappedBy = "owner",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Vehicle> vehicles;

    public User(
        String firstName,
        String lastName,
        String username,
        String email,
        String password,
        int points
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.points = points;
    }

    public User() {}

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getPoints() {
        return points;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Vehicle getDefaultVehicle() {
        return defaultVehicle;
    }

    public void setDefaultVehicle(Vehicle defaultVehicle) {
        this.defaultVehicle = defaultVehicle;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public String toString() {
        return (
            "User [id=" +
            id +
            ", firstName=" +
            firstName +
            ", lastName=" +
            lastName +
            ", username=" +
            username +
            ", email=" +
            email +
            ", password=" +
            password +
            ", points=" +
            points +
            ", defaultVehicle=" +
            defaultVehicle
        );
    }
}
