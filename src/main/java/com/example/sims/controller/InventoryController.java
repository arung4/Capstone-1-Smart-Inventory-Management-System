package com.example.sims.controller;

import com.example.sims.model.InventoryItem;
import com.example.sims.model.Response;
import com.example.sims.model.User;
import com.example.sims.service.ActivityLogService;
import com.example.sims.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Response<List<InventoryItem>> getAllItems(@RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        List<InventoryItem> items = inventoryService.getAllInventoryItems();
        activityLogService.logActivity(token, "INVENTORY_VIEW", "Viewed all inventory items", null);
        return new Response<>(HttpStatus.OK.value(), "Items retrieved successfully", items);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Response<InventoryItem> getItemById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        InventoryItem item = inventoryService.getItemById(id);
        activityLogService.logActivity(token, "INVENTORY_VIEW",
                "Viewed inventory item: " + item.getName(), null);
        return new Response<>(HttpStatus.OK.value(), "Item retrieved successfully", item);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public Response<InventoryItem> addItem(@RequestBody InventoryItem item, @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        InventoryItem addedItem = inventoryService.addItem(item);
        activityLogService.logStockMovement(addedItem, token, "ADD",
                item.getQuantity(), "Initial stock addition");
        return new Response<>(HttpStatus.CREATED.value(), "Item added successfully", addedItem);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Response<InventoryItem> updateItem(
            @PathVariable Long id,
            @RequestBody InventoryItem item,
            @RequestHeader("Authorization") String authHeader) {

         String token = authHeader.substring(7);

        // Get current item to compare quantities
        InventoryItem currentItem = inventoryService.getItemById(id);
        int oldQuantity = currentItem.getQuantity();

        InventoryItem updatedItem = inventoryService.updateItem(id, item);

        // Log quantity changes if they occurred
        int quantityChange = updatedItem.getQuantity() - oldQuantity;
        if (quantityChange != 0) {
            String movementType = quantityChange > 0 ? "ADD" : "REMOVE";
            activityLogService.logStockMovement(
                    updatedItem,
                    token,
                    movementType,
                    Math.abs(quantityChange),
                    "Manual adjustment"
            );
        }

        // Log the update activity
        activityLogService.logActivity(token, "INVENTORY_UPDATE",
                "Updated item: " + updatedItem.getName(), null);

        return new Response<>(HttpStatus.OK.value(), "Item updated successfully", updatedItem);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Response<Void> deleteItem(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        InventoryItem item = inventoryService.getItemById(id);


        // Log the deletion with the quantity being removed
        activityLogService.logStockMovement(
                item,
                token,
                "REMOVE",
                item.getQuantity(),
                "Item deletion"
        );

        activityLogService.logActivity(token, "INVENTORY_DELETE",
                "Deleted item: " + item.getName(), null);

        inventoryService.deleteItem(id);

        return new Response<>(HttpStatus.OK.value(), "Item deleted successfully", null);
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Response<List<InventoryItem>> getItemsByCategory(
            @PathVariable String category,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        List<InventoryItem> items = inventoryService.getItemByCategory(category);
        activityLogService.logActivity(token, "INVENTORY_VIEW",
                "Viewed items by category: " + category, null);

        return new Response<>(HttpStatus.OK.value(),
                "Items retrieved by category successfully", items);
    }
}