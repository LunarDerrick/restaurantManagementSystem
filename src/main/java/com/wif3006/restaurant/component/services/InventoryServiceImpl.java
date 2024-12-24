/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wif3006.restaurant.component.services;

import com.wif3006.restaurant.component.dtos.InventoryModel;
import com.wif3006.restaurant.component.dtos.MenuModel;
import com.wif3006.restaurant.component.entities.InventoryEntity;
import com.wif3006.restaurant.component.repositories.InventoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author derri
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;
        
    @Override
    public Boolean addInventory(InventoryModel inventoryModel) {
        try {
            InventoryEntity inventoryEntity = new InventoryEntity();
            inventoryEntity.setName(inventoryModel.getName());
            inventoryEntity.setQuantity(inventoryModel.getQuantity());
            inventoryEntity.setThreshold(inventoryModel.getThreshold());
            inventoryRepository.save(inventoryEntity);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean updateInventory(Long id, InventoryModel inventoryModel) {
        try {   
            Optional<InventoryEntity> optionalInventoryEntity = inventoryRepository.findById(id);
            if (optionalInventoryEntity.isPresent()) {
                InventoryEntity inventoryEntity = optionalInventoryEntity.get();
                inventoryEntity.setName(inventoryModel.getName());
                inventoryEntity.setQuantity(inventoryModel.getQuantity());
                inventoryEntity.setThreshold(inventoryModel.getThreshold());
                inventoryRepository.save(inventoryEntity); 
                return true;
            } else {
                throw new RuntimeException("Menu not found");
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean deleteInventory(Long id) {
        try {
            Optional<InventoryEntity> optionalInventoryEntity = inventoryRepository.findById(id);
            if (optionalInventoryEntity.isPresent()) {
                inventoryRepository.delete(optionalInventoryEntity.get());
                return true;
            } else {
                throw new RuntimeException("Inventory not found");
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<InventoryModel> getInventoryListByFilter(String filter, String sortedBy, String order) {
        try {
            List<InventoryEntity> inventoryEntities;

            // Filter logic (using findByQualityLessThan)
            if (filter != null && !filter.isEmpty()) {
                try {
                    int threshold = Integer.parseInt(filter);
                    inventoryEntities = inventoryRepository.findByQuantityLessThan(threshold);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Filter must be a numeric value representing quality.");
                }
            } else {
                inventoryEntities = inventoryRepository.findAll();
            }
            
            // Sorting logic
            if ("desc".equalsIgnoreCase(order)) {
                if ("name".equalsIgnoreCase(sortedBy)) {
                    inventoryEntities.sort((inv1, inv2) -> inv2.getName().compareTo(inv1.getName())); // Descending by name
                } else if ("quantity".equalsIgnoreCase(sortedBy)) {
                    inventoryEntities.sort((inv1, inv2) -> Integer.compare(inv2.getQuantity(), inv1.getQuantity())); // Descending by quantity
                } else if ("threshold".equalsIgnoreCase(sortedBy)) {
                    inventoryEntities.sort((inv1, inv2) -> Double.compare(inv2.getThreshold(), inv1.getThreshold())); // Descending by threshold
                }
            } else {
                if ("name".equalsIgnoreCase(sortedBy)) {
                    inventoryEntities.sort((inv1, inv2) -> inv1.getName().compareTo(inv2.getName())); // Ascending by name
                } else if ("quantity".equalsIgnoreCase(sortedBy)) {
                    inventoryEntities.sort((inv1, inv2) -> Integer.compare(inv1.getQuantity(), inv2.getQuantity())); // Ascending by quantity
                } else if ("threshold".equalsIgnoreCase(sortedBy)) {
                    inventoryEntities.sort((inv1, inv2) -> Double.compare(inv1.getThreshold(), inv2.getThreshold())); // Ascending by threshold
                }
            }
            
            // Map entities to models
            return inventoryEntities.stream().map(entity -> {
                InventoryModel model = new InventoryModel();
                model.setId(entity.getId());
                model.setName(entity.getName());
                model.setQuantity(entity.getQuantity());
                model.setThreshold(entity.getThreshold());
                return model;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String alertLowStock() {
        try {
            List<InventoryEntity> inventoryEntities = inventoryRepository.findAll(); // Get all inventory items

            // Generate an alert string for low-stock items
            StringBuilder alert = new StringBuilder("Low Stock Alert:\n");
            for (InventoryEntity item : inventoryEntities) {
                // Check if the inventory quantity is less than the threshold
                if (item.getQuantity() < item.getThreshold()) {
                    alert.append("- ").append(item.getName()).append(": ")
                         .append(item.getQuantity()).append(" remaining\n");
                }
            }

            if (alert.length() == 0) {
                return "All inventory levels are sufficient.";
            }

            return alert.toString();
        } catch (Exception e) {
            return "An error occurred while checking low stock.";
        }
    }

    @Override
    public void reduceStockOnOrder(List<MenuModel> orderedItems) {
        try {
            for (MenuModel menuModel : orderedItems) {
                // Find the corresponding inventory item based on the menu item ID
                Optional<InventoryEntity> inventoryItemOpt = inventoryRepository.findById(menuModel.getId());

                if (inventoryItemOpt.isPresent()) {
                    InventoryEntity inventoryItem = inventoryItemOpt.get();

                    // Check if there is enough stock
                    if (inventoryItem.getQuantity() >= 1) { // Assuming 1 is the quantity per order; adjust as needed
                        // Reduce the stock by 1 (or the ordered quantity)
                        inventoryItem.setQuantity(inventoryItem.getQuantity() - 1); // Decrease by 1, change if needed

                        // Save the updated inventory entity
                        inventoryRepository.save(inventoryItem);
                    } else {
                        // Handle case where stock is insufficient (e.g., throw an exception or alert)
                        throw new Exception("Insufficient stock for item: " + inventoryItem.getName());
                    }
                } else {
                    // Handle case where no matching inventory item was found
                    throw new Exception("Inventory item not found for menu item: " + menuModel.getName());
                }
            }
        } catch (Exception e) {
            // Handle any other errors
            e.printStackTrace();
            // Optionally, rethrow a custom exception or return a specific error message
        }
    }
    
}
