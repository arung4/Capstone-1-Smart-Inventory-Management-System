package com.example.sims.service;

import com.example.sims.dto.DashboardStats;
import com.example.sims.model.InventoryItem;
import com.example.sims.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


// DashboardService.java
@Service
public class DashboardService {

    @Autowired
    private InventoryRepository inventoryRepository;

    public DashboardStats getDashboardStats() {
        int totalItems = inventoryRepository.countAllItems();
        int lowStockItems = inventoryRepository.countLowStockItems(5); // Assuming 5 is low stock threshold
        int expiringSoonItems = inventoryRepository.countExpiringSoonItems(
                LocalDate.now(),
                LocalDate.now().plusDays(30) // Items expiring in next 30 days
        );

        return new DashboardStats(totalItems, lowStockItems, expiringSoonItems);
    }
}