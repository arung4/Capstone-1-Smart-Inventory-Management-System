package com.example.sims.service;

import com.example.sims.model.InventoryItem; 
import com.example.sims.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

     // get all inventory items
    public List<InventoryItem> getAllInventoryItems() {
        return inventoryRepository.findAll();
    }
    // add an inventory item
    public InventoryItem addItem(InventoryItem item){
        return inventoryRepository.save(item);
    }

    // update an inventory item
    public InventoryItem updateItem(Long Id, InventoryItem item){
          item.setId(Id);
          return inventoryRepository.save(item);
    }
    
    // delete an inventory item
    public void deleteItem(Long Id){
        inventoryRepository.deleteById(Id);
    }

    // get an inventory item by id
    public InventoryItem getItemById(Long Id){
        return inventoryRepository.findById(Id).orElse(null);
    }
    // get inventory items by category
    public List<InventoryItem> getItemByCategory(String category){
        return inventoryRepository.findByCategory(category);
    }
   
}
