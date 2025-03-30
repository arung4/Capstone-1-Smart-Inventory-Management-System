package com.example.sims.repository;

import com.example.sims.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByCategory(String category); // Optional: Filter by category

     List<InventoryItem> findByQuantityLessThan(int quantity);
    
    List<InventoryItem> findByExpiryDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT i FROM InventoryItem i WHERE i.lastUpdated BETWEEN :start AND :end")
    List<InventoryItem> findByLastUpdatedBetween(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);
}