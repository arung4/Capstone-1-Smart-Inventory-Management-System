package com.example.sims.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private String category;

    @Column(name = "supplier_name", nullable = false)
    private String supplierName;
}
