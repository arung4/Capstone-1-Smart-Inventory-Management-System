// AlertService.java
package com.example.sims.service;

import com.example.sims.dto.ApiResponse;
import com.example.sims.model.Alert;
import com.example.sims.model.AlertType;
import com.example.sims.model.InventoryItem;
import com.example.sims.repository.AlertRepository;
import com.example.sims.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AlertService {
    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);

    private final AlertRepository alertRepository;
    private final InventoryRepository itemRepository;
    
    @Autowired
    public AlertService(AlertRepository alertRepository, 
                      InventoryRepository itemRepository) {
        this.alertRepository = alertRepository;
        this.itemRepository = itemRepository;
    }
    
    @Scheduled(fixedRate = 86400000) // Runs daily
    public void checkInventoryAlerts() {
        checkLowStockItems();
        checkExpiringItems();
    }
    
     
    // Manual check for alerts 
    public ApiResponse<Map<String, Object>> checkInventoryAlertsWithResponse() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Check low stock items
            List<InventoryItem> lowStockItems = itemRepository.findByQuantityLessThan(5);
            lowStockItems.forEach(this::createLowStockAlert);
            result.put("lowStockItemsFound", lowStockItems.size());
            
            // Check expiring items
            List<InventoryItem> expiringItems = itemRepository.findByExpiryDateBetween(
                LocalDate.now(), 
                LocalDate.now().plusDays(7)
            );
            expiringItems.forEach(this::createExpiryAlert);
            result.put("expiringItemsFound", expiringItems.size());
            
            // Get current active alerts
            List<Alert> activeAlerts = alertRepository.findByResolvedFalseOrderByCreatedAtDesc();
            result.put("activeAlerts", activeAlerts);
            
            logger.info("Alert check completed: {} low stock items, {} expiring items", 
                    lowStockItems.size(), expiringItems.size());
                    
            return ApiResponse.success("Alert check completed successfully", result);
            
        } catch (Exception e) {
            logger.error("Error during alert check: {}", e.getMessage());
            return ApiResponse.error("Error during alert check: " + e.getMessage());
        }
    }
   
    private void createLowStockAlert(InventoryItem item) {
        if (!alertRepository.existsByItemAndTypeAndResolvedFalse(item, AlertType.LOW_STOCK)) {
            Alert alert = new Alert();
            alert.setMessage("Low stock alert for " + item.getName() + " (Current: " + item.getQuantity() + ")");
            alert.setType(AlertType.LOW_STOCK);
            alert.setItem(item);
            alert.setCreatedAt(LocalDateTime.now());
            alertRepository.save(alert);
        }
    }


    private void createExpiryAlert(InventoryItem item) {
        if (!alertRepository.existsByItemAndTypeAndResolvedFalse(item, AlertType.EXPIRY)) {
            Alert alert = new Alert();
            alert.setMessage(item.getName() + " expires on " + item.getExpiryDate());
            alert.setType(AlertType.EXPIRY);
            alert.setItem(item);
            alert.setCreatedAt(LocalDateTime.now());
            alertRepository.save(alert);
        }
    }


    private List<InventoryItem> checkLowStockItems() {
        int lowStockThreshold = 5;
        List<InventoryItem> lowStockItems = itemRepository.findByQuantityLessThan(lowStockThreshold);
        
        lowStockItems.forEach(item -> {
            if (!alertRepository.existsByItemAndTypeAndResolvedFalse(item, AlertType.LOW_STOCK)) {
                Alert alert = new Alert();
                alert.setMessage("Low stock alert for " + item.getName() + " (Current: " + item.getQuantity() + ")");
                alert.setType(AlertType.LOW_STOCK);
                alert.setItem(item);
                alert.setCreatedAt(LocalDateTime.now());
                alert.setResolved(false);
                alertRepository.save(alert);
            }
        });
        return lowStockItems;
    }
    
    private List<InventoryItem> checkExpiringItems() {
        LocalDate thresholdDate = LocalDate.now().plusDays(7);
        List<InventoryItem> expiringItems = itemRepository.findByExpiryDateBetween(
            LocalDate.now(),
            thresholdDate
        );
        
        expiringItems.forEach(item -> {
            if (!alertRepository.existsByItemAndTypeAndResolvedFalse(item, AlertType.EXPIRY)) {
                Alert alert = new Alert();
                alert.setMessage(item.getName() + " expires on " + item.getExpiryDate());
                alert.setType(AlertType.EXPIRY);
                alert.setItem(item);
                alert.setCreatedAt(LocalDateTime.now());
                alert.setResolved(false);
                alertRepository.save(alert);
            }
        });
        return expiringItems;
    }
    
    public ApiResponse<List<Alert>> getActiveAlerts() {
        List<Alert> alerts = alertRepository.findByResolvedFalseOrderByCreatedAtDesc();
        return ApiResponse.success("Active alerts retrieved successfully", alerts);
    }
    
    public ApiResponse<Alert> resolveAlert(Long alertId) {
        try {
            return alertRepository.findById(alertId)
                .map(alert -> {
                    alert.setResolved(true);
                    Alert savedAlert = alertRepository.save(alert);
                    return ApiResponse.success("Alert resolved successfully", savedAlert);
                })
                .orElseGet(() -> ApiResponse.error("Alert not found")); // This now properly returns ApiResponse<Alert>
        } catch (Exception e) {
            return ApiResponse.error("Failed to resolve alert: " + e.getMessage());
        }
    }
}