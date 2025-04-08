package com.example.sims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private int totalItems;
    private int lowStockItems;
    private int expiringSoonItems;


    // Getters and setters...
}