package com.example.sims.service;

import com.example.sims.model.Inventory;
import java.util.List;

public interface InventoryService {

    // Add inventory item
    Inventory addInventory(Inventory inventory);

    // Update inventory item
    Inventory updateInventory(Long inventoryId, Inventory inventory);

    // Delete inventory item
    void deleteInventory(Long inventoryId);

    // Get inventory item by ID
    Inventory getInventoryById(Long inventoryId);

    // Get all inventory items
    List<Inventory> getAllInventory();

    // Get inventory by category
    List<Inventory> getInventoryByCategory(String category);

    // Get low stock inventory items
    List<Inventory> getLowStockInventory(int threshold);
}
