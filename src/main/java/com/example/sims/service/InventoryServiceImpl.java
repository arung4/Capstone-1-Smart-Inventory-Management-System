package com.example.sims.service;

import com.example.sims.model.Inventory;
import com.example.sims.repository.InventoryRepository;
import com.example.sims.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{
    
    private final InventoryRepository inventoryRepository;

    @Override
    public Inventory addInventory(Inventory inventory){
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory updateInventory(Long inventoryId, Inventory inventory){
        Inventory existingInventory = inventoryRepository.findById(inventoryId)
        .orElseThrow(() -> new IllegalArgumentException("Inventory not found with ID: " + inventoryId));

existingInventory.setName(inventory.getName());
existingInventory.setQuantity(inventory.getQuantity());
existingInventory.setExpiryDate(inventory.getExpiryDate());
existingInventory.setCategory(inventory.getCategory());
existingInventory.setSupplierName(inventory.getSupplierName());

return inventoryRepository.save(existingInventory);
    }

    @Override
    public void deleteInventory(Long inventoryId){
        inventoryRepository.deleteById(inventoryId);
    }

    @Override
    public Inventory getInventoryById(Long inventoryId){
        return inventoryRepository.findById(inventoryId)
       .orElseThrow(() -> new IllegalArgumentException("Inventory not found with ID: " + inventoryId));
    }

    @Override
    public List<Inventory> getAllInventory(){
        return inventoryRepository.findAll();
    }

    @Override
    public List<Inventory> getInventoryByCategory(String category){
        return inventoryRepository.findByCategory(category);
    }

    @Override
    public List<Inventory> getLowStockInventory(int threshold){
        return inventoryRepository.findByQuantityLessThan(threshold);
    }
}
