package com.example.demo.Model;

import org.springframework.boot.autoconfigure.domain.EntityScan;

//@Entity
//@Table(name = "users")
public class User {

    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstName;
    private String lastName;
    // @Column(unique = true)
    private String username;
    private String email;
    private String password;
    private int points;

    public User(String firstName, String lastName, String username, String email, String password, int points) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.points = points;
    }

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
}
