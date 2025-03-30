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

    // Constructor, getters, and setters
//    public DashboardStats(int totalItems, int lowStockItems, int expiringSoonItems) {
//        this.totalItems = totalItems;
//        this.lowStockItems = lowStockItems;
//        this.expiringSoonItems = expiringSoonItems;
//    }

    // Getters and setters...
}