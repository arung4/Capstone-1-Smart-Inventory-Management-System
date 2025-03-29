package com.example.sims.controller;

import com.example.sims.model.InventoryItem;
import com.example.sims.model.Response;
import com.example.sims.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // Only ADMIN and STAFF can view all items
    public Response<List<InventoryItem>> getAllItems() {
        List<InventoryItem> items = inventoryService.getAllInventoryItems();
        return new Response<>(HttpStatus.OK.value(), "Items retrieved successfully", items);
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // Only ADMIN and STAFF can view this item
    public Response<InventoryItem> getItemById(@PathVariable Long id) {
        InventoryItem item = inventoryService.getItemById(id);
        return new Response<>(HttpStatus.OK.value(), "Item retrieved successfully", item);
    }
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can add the item
    public Response<InventoryItem> addItem(@RequestBody InventoryItem item) {
        InventoryItem addedItem = inventoryService.addItem(item);
        return new Response<>(HttpStatus.CREATED.value(), "Item added successfully", addedItem);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // only admin can edit this item
    public Response<InventoryItem> updateItem(@PathVariable Long id, @RequestBody InventoryItem item) {
        InventoryItem updatedItem = inventoryService.updateItem(id, item);
        return new Response<>(HttpStatus.OK.value(), "Item updated successfully", updatedItem);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // only admin can delete this item
    public Response<Void> deleteItem(@PathVariable Long id) {
        inventoryService.deleteItem(id);
        return new Response<>(HttpStatus.OK.value(), "Item deleted successfully", null);
    }

    @GetMapping("/category/{category}")
    public Response<List<InventoryItem>> getItemsByCategory(@PathVariable String category) {
        List<InventoryItem> items = inventoryService.getItemByCategory(category);
        return new Response<>(HttpStatus.OK.value(), "Items retrieved by category successfully", items);
    }
}