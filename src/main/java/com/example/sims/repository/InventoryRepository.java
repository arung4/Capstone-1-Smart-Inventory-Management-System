package com.example.sims.repository;

import com.example.sims.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Find inventory items by category
    List<Inventory> findByCategory(String category);

    // Find items that are low in stock (below a threshold)
    List<Inventory> findByQuantityLessThan(int threshold);
}