package com.example.sims.repository;

import com.example.sims.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByInventoryItemIdOrderByMovementDateDesc(Long itemId);

    // Remove the limit to get all records
    @Query("SELECT sm FROM StockMovement sm ORDER BY sm.movementDate DESC")
    List<StockMovement> findAllByOrderByMovementDateDesc();

    @Query("SELECT sm FROM StockMovement sm JOIN FETCH sm.user JOIN FETCH sm.inventoryItem")
    List<StockMovement> findAllWithUserAndItem();


    @Query("SELECT sm FROM StockMovement sm WHERE sm.movementDate BETWEEN :startDate AND :endDate ORDER BY sm.movementDate DESC")
    List<StockMovement> findByMovementDateBetween(
            @Param("startDate")LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
            );
}

