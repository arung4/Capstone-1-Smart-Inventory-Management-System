package com.example.sims.controller;

import com.example.sims.model.ActivityLog;
import com.example.sims.model.StockMovement;
import com.example.sims.model.User;
import com.example.sims.service.ActivityLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@PreAuthorize("hasRole('ADMIN')")
public class ActivityController {

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping("/recent")
    public ResponseEntity<List<ActivityLog>> getRecentActivities(@RequestHeader("Authorization")
                                                                     String authHeader,
                                                                 HttpServletRequest request) {

        String token = authHeader.substring(7);
        // Log the response
        activityLogService.logActivity(
                token,
                "ACTIVITY_VIEW",
                "Viewed acitivity logs",
                request.getRemoteAddr()
        );
        return ResponseEntity.ok(activityLogService.getRecentActivities());
    }

    @GetMapping("/stock-movements")
    public ResponseEntity<List<StockMovement>> getRecentStockMovements(
            @RequestHeader("Authorization") String authHeader, HttpServletRequest request
    ) {
         String token = authHeader.substring(7);
        // Log this access
        activityLogService.logActivity(
                token,
                "STOCK_MOVEMENT_VIEW",
                "Viewed stock movements",
                request.getRemoteAddr()
        );
        List<StockMovement> movements = activityLogService.getRecentStockMovements();
        return ResponseEntity.ok(movements);
    }
}