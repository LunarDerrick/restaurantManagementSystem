/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wif3006.restaurant.component.services;

import com.wif3006.restaurant.component.dtos.InventoryModel;
import com.wif3006.restaurant.component.dtos.OrderModel;
import com.wif3006.restaurant.component.entities.InventoryEntity;
import com.wif3006.restaurant.component.entities.MenuEntity;
import com.wif3006.restaurant.component.entities.OrderEntity;
import com.wif3006.restaurant.component.repositories.InventoryRepository;
import com.wif3006.restaurant.component.repositories.MenuRepository;
import com.wif3006.restaurant.component.repositories.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

/**
 *
 * @author derri
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Override
    public Boolean addInventory(InventoryModel inventoryModel) {
        try {
            InventoryEntity inventoryEntity = new InventoryEntity();
            inventoryEntity.setName(inventoryModel.getName());
            inventoryEntity.setQuantity(inventoryModel.getQuantity());
            inventoryEntity.setThreshold(inventoryModel.getThreshold());
            inventoryRepository.save(inventoryEntity);
            return Boolean.TRUE;
        } catch (Exception e) {
            System.err.println("Error adding inventory: " + e.getMessage());
            throw new IllegalArgumentException("Fail to add inventory");
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
                return Boolean.TRUE;
            } else {
                throw new RuntimeException("Inventory not found");
            }
        } catch (Exception e) {
            System.err.println("Error updating inventory: " + e.getMessage());
            throw new IllegalArgumentException("Failed to update inventory");
        }
    }

    @Override
    public Boolean deleteInventory(Long id) {
        try {
            Optional<InventoryEntity> optionalInventoryEntity = inventoryRepository.findById(id);
            if (optionalInventoryEntity.isPresent()) {
                inventoryRepository.delete(optionalInventoryEntity.get());
                return Boolean.TRUE;
            } else {
                throw new RuntimeException("Inventory not found");
            }
        } catch (Exception e) {
            System.err.println("Error deleting inventory: " + e.getMessage());
            throw new IllegalArgumentException("Failed to delete inventory");
        }
    }

    @Override
    public List<InventoryModel> getInventoryListByFilter(String filter, String sortedBy, String order) {
        try {
            List<InventoryEntity> inventoryEntities;

//            // Filter logic (using findByQualityLessThan)
//            if (filter != null && !filter.isEmpty()) {
//                try {
//                    int threshold = Integer.parseInt(filter);
//                    inventoryEntities = inventoryRepository.findByQuantityLessThan(threshold);
//                } catch (NumberFormatException e) {
//                    throw new IllegalArgumentException("Filter must be a numeric value representing quality.");
//                }
//            } else {
                inventoryEntities = inventoryRepository.findAll();
//            }
//
//            // Sorting logic
//            if ("desc".equalsIgnoreCase(order)) {
//                if ("name".equalsIgnoreCase(sortedBy)) {
//                    inventoryEntities.sort((inv1, inv2) -> inv2.getName().compareTo(inv1.getName())); // Descending by name
//                } else if ("quantity".equalsIgnoreCase(sortedBy)) {
//                    inventoryEntities.sort((inv1, inv2) -> Integer.compare(inv2.getQuantity(), inv1.getQuantity())); // Descending by quantity
//                } else if ("threshold".equalsIgnoreCase(sortedBy)) {
//                    inventoryEntities.sort((inv1, inv2) -> Double.compare(inv2.getThreshold(), inv1.getThreshold())); // Descending by threshold
//                }
//            } else {
//                if ("name".equalsIgnoreCase(sortedBy)) {
//                    inventoryEntities.sort((inv1, inv2) -> inv1.getName().compareTo(inv2.getName())); // Ascending by name
//                } else if ("quantity".equalsIgnoreCase(sortedBy)) {
//                    inventoryEntities.sort((inv1, inv2) -> Integer.compare(inv1.getQuantity(), inv2.getQuantity())); // Ascending by quantity
//                } else if ("threshold".equalsIgnoreCase(sortedBy)) {
//                    inventoryEntities.sort((inv1, inv2) -> Double.compare(inv1.getThreshold(), inv2.getThreshold())); // Ascending by threshold
//                }
//            }

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
            List<InventoryEntity> inventoryEntities = inventoryRepository.findAll();

            // Generate an alert string for low-stock items
            StringBuilder alert = new StringBuilder("\nLow Stock Alert:\n");
            for (InventoryEntity item : inventoryEntities) {
                // Check if the inventory quantity is less than the threshold
                if (item.getQuantity() < item.getThreshold()) {
                    alert.append("- ").append(item.getName()).append(": ")
                            .append(item.getQuantity()).append(" remaining\n");
                }
            }

            if (alert.length() == 0) {
                return "All inventory levels are sufficient.\n";
            }

            return alert.toString();
        } catch (Exception e) {
            return "An error occurred while checking low stock.";
        }
    }

    @Override
    @Transactional
    public void reduceStockOnOrder(OrderModel orderModel, Long orderId) {
        try {
            // Handle the case of a new order
            if (orderId == null) {
                for (String itemName : orderModel.getItems()) {
                    MenuEntity menu = menuRepository.findByName(itemName)
                            .orElseThrow(() -> new RuntimeException("Menu item not found: " + itemName));

                    String[] ingredients = menu.getDescription().split(",");
                    for (String ingredientName : ingredients) {
                        String ingredient = ingredientName.trim();
                        InventoryEntity inventoryEntity = inventoryRepository.findByName(ingredient)
                                .orElseThrow(() -> new RuntimeException("Ingredient not found: " + ingredient));

                        inventoryEntity.setQuantity(inventoryEntity.getQuantity() - 1);
                        inventoryRepository.save(inventoryEntity);
                        inventoryRepository.flush(); // Ensure changes are committed to DB
                    }
                }
            } else {
                // Handle the case of an updated order
                Optional<OrderEntity> optionalOrder = orderRepository.findById(orderId);
                if (!optionalOrder.isPresent()) {
                    throw new RuntimeException("Order not found");
                }

                OrderEntity orderEntity = optionalOrder.get();

                // Fetch upcoming "new" menu
                List<MenuEntity> newMenuEntities = orderModel.getItems().stream()
                        .map(itemName -> menuRepository.findByName(itemName)
                        .orElseThrow(() -> new RuntimeException("Menu item not found: " + itemName)))
                        .collect(Collectors.toList());
                
                // Fetch current "old/previous" menu
                List<String> previousItems = orderEntity.getItems();

                // Compare quantities in the previous and new order
                for (MenuEntity menu : newMenuEntities) {
                    String itemName = menu.getName();

                    if (previousItems.contains(itemName)) {
                        previousItems.remove(itemName);
                    } else {
                        String[] ingredients = menu.getDescription().split(",");
                        for (String ingredientName : ingredients) {
                            String ingredient = ingredientName.trim();
                            InventoryEntity inventoryEntity = inventoryRepository.findByName(ingredient)
                                    .orElseThrow(() -> new RuntimeException("Ingredient not found: " + ingredient));

                            inventoryEntity.setQuantity(inventoryEntity.getQuantity() - 1);
                            inventoryRepository.save(inventoryEntity);
                            inventoryRepository.flush(); // Ensure changes are committed to DB
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reducing stock on order: " + e.getMessage());
            throw new RuntimeException("Failed to reduce stock on order");
        }
    }

    @Override
    public Boolean isStockSufficient(OrderModel orderModel) {
        try {
            // Map to track the total quantity required for each ingredient
            Map<String, Integer> requiredQuantities = new HashMap<>();

            // Step 1: Calculate total required quantities for all ingredients
            for (String itemName : orderModel.getItems()) {
                MenuEntity menu = menuRepository.findByName(itemName)
                        .orElseThrow(() -> new RuntimeException("Menu item not found: " + itemName));

                String[] ingredients = menu.getDescription().split(",");
                for (String ingredientName : ingredients) {
                    String ingredient = ingredientName.trim();
                    requiredQuantities.put(
                            ingredient,
                            requiredQuantities.getOrDefault(ingredient, 0) + 1 // Increment required quantity for this ingredient
                    );
                }
            }

            // Step 2: Check stock sufficiency for all required ingredients
            for (Map.Entry<String, Integer> entry : requiredQuantities.entrySet()) {
                String ingredient = entry.getKey();
                int requiredQuantity = entry.getValue();

                InventoryEntity inventoryEntity = inventoryRepository.findByName(ingredient)
                        .orElseThrow(() -> new RuntimeException("Ingredient not found: " + ingredient));

                // Check if available stock is sufficient
                if (inventoryEntity.getQuantity() < requiredQuantity) {
                    return Boolean.FALSE; // Insufficient stock for this ingredient
                }
            }

            return Boolean.TRUE; // Stock is sufficient for all ingredients
        } catch (Exception e) {
            System.err.println("Error checking stock sufficiency: " + e.getMessage());
            return Boolean.FALSE; // Fail-safe: assume insufficient stock if an error occurs
        }
    }
}
