package com.example.sims.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inventory_items")

public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(columnDefinition = "integer")
    private Integer quantity;
    private LocalDate expiryDate;
    private double price;
    private String category;
    private String supplier;



    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    // ... getters and setters

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // Add to your existing @PreUpdate and @PrePersist methods if you have them
    @PreUpdate
    @PrePersist
    public void updateTimestamps() {
        this.lastUpdated = LocalDateTime.now();
    }
}
