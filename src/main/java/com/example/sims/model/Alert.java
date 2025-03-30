package com.example.sims.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String message;
    private AlertType type;
    private LocalDateTime createdAt;
    private boolean resolved;
    
    @ManyToOne
    private InventoryItem item;
    
    // No-args constructor (required by JPA)
    public Alert() {
    }
    
    // All-args constructor
    public Alert(String message, AlertType type, InventoryItem item) {
        this.message = message;
        this.type = type;
        this.item = item;
        this.createdAt = LocalDateTime.now();
        this.resolved = false;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AlertType getType() {
        return type;
    }

    public void setType(AlertType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public InventoryItem getItem() {
        return item;
    }

    public void setItem(InventoryItem item) {
        this.item = item;
    }
}