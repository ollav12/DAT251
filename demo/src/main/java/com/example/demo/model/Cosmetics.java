package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cosmetics")
public class Cosmetics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String price;

    private String description;
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CosmeticsType category;

    @ManyToMany(mappedBy = "ownedCosmetics")
    @JsonIgnore
    private Set<User> owners = new HashSet<>();

    public Cosmetics(String name, String price, String description, String image, CosmeticsType category) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
        this.category = category;
    }

    public Cosmetics() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public CosmeticsType getCategory() { return category; }
    public void setCategory(CosmeticsType category) { this.category = category; }
}
