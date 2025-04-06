package com.example.sims.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "stock_movements")
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inventory_item_id")
//    @JsonBackReference
    private InventoryItem inventoryItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_email", referencedColumnName = "email",
            foreignKey = @ForeignKey(name = "fk_stock_movement_user"))
    private User user;

    private String movementType;
    private int quantityChange;
    private int previousQuantity;
    private int newQuantity;

    @CreationTimestamp
    private LocalDateTime movementDate;

    private String notes;
    // Getters, setters, constructors
}

