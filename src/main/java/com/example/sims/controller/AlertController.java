// AlertController.java
package com.example.sims.controller;

import com.example.sims.dto.ApiResponse;
import com.example.sims.service.AlertService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final AlertService alertService;
    
    @Autowired
    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }
     
    // Manual check for alerts
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Map<String, Object>>> manuallyCheckAlerts() {
        ApiResponse<Map<String, Object>> response = alertService.checkInventoryAlertsWithResponse();
        return ResponseEntity.status(response.isSuccess() ? 200 : 500).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getActiveAlerts() {
        ApiResponse<?> response = alertService.getActiveAlerts();
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<?>> resolveAlert(@PathVariable Long id) {
        ApiResponse<?> response = alertService.resolveAlert(id);
        return ResponseEntity.status(response.isSuccess() ? 200 : 404).body(response);
    }
}