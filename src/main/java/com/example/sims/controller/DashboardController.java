// AlertController.java
package com.example.sims.controller;


import com.example.sims.dto.DashboardStats;
import com.example.sims.model.InventoryItem;
import com.example.sims.model.Response;
import com.example.sims.repository.InventoryRepository;
import com.example.sims.service.DashboardService;
import com.example.sims.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


// DashboardController.java
@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // Only accessible by ADMIN
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;
    private final InventoryService inventoryService;

    @Autowired
    public DashboardController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<DashboardStats> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryItem>> LowStockItems() {

        int lowStockThreshold = 5;
        List<InventoryItem> lowStockItems = inventoryService.getLowStockItems(lowStockThreshold);
        return ResponseEntity.ok(lowStockItems);
    }

    @GetMapping("/expiring-soon")
   public ResponseEntity<List<InventoryItem>> ExpiringSoonItems(){
        int daysThreeshold = 30;
        return ResponseEntity.ok(inventoryService.getExpirySoonItems(daysThreeshold));
    }
}