// AlertRepository.java
package com.example.sims.repository;

import com.example.sims.model.Alert;
import com.example.sims.model.AlertType;
import com.example.sims.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByResolvedFalseOrderByCreatedAtDesc();
    
    boolean existsByItemAndTypeAndResolvedFalse(InventoryItem item, AlertType type);
    
    List<Alert> findByItem(InventoryItem item);

    List<Alert> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}