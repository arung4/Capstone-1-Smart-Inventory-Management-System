package com.example.sims.repository;

import com.example.sims.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByCategory(String category); // Optional: Filter by category
}