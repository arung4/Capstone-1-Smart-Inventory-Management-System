package com.example.sims.service;

import com.example.sims.model.ActivityLog;
import com.example.sims.model.InventoryItem;
import com.example.sims.model.StockMovement;
import com.example.sims.model.User;
import com.example.sims.repository.ActivityLogRepository;
import com.example.sims.repository.StockMovementRepository;
import com.example.sims.repository.UserRepository;
import com.example.sims.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public void logActivity(String token, String activityType, String description, String ipAddress) {

        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setActivityType(activityType);
        log.setDescription(description);
        log.setIpAddress(ipAddress);
        activityLogRepository.save(log);
    }

    public void logStockMovement(InventoryItem item, String token, String movementType,
                                 int quantityChange, String notes) {

        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


        StockMovement movement = new StockMovement();
        movement.setInventoryItem(item);
        movement.setUser(user);
        movement.setMovementType(movementType);
        movement.setQuantityChange(quantityChange);
        movement.setPreviousQuantity(item.getQuantity() - quantityChange);
        movement.setNewQuantity(item.getQuantity());
        movement.setNotes(notes);
        stockMovementRepository.save(movement);

        // Also log as activity
        logActivity(token, "STOCK_MOVEMENT",
                String.format("%s %d units of %s",
                        movementType, quantityChange, item.getName()),
                null);
    }

    public List<ActivityLog> getRecentActivities() {
        return activityLogRepository.findTop20ByOrderByCreatedAtDesc();
    }

    public List<StockMovement> getRecentStockMovements() {
        return stockMovementRepository.findAllByOrderByMovementDateDesc();
    }
}
