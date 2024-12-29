/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wif3006.restaurant.component.services;

import com.wif3006.restaurant.component.dtos.OrderModel;
import com.wif3006.restaurant.component.entities.MenuEntity;
import com.wif3006.restaurant.component.entities.OrderEntity;
import com.wif3006.restaurant.component.repositories.MenuRepository;
import com.wif3006.restaurant.component.repositories.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import javax.persistence.EntityManager;

/**
 *
 * @author derri
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private MenuRepository menuRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    @Override
    public Boolean addOrder(OrderModel orderModel) {
        try {
            // Fetch menu entities based on item names in the order model
            List<MenuEntity> menuEntities = orderModel.getItems().stream()
                .map(itemName -> menuRepository.findByName(itemName) // Find MenuEntity by name
                    .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + itemName))) 
                .collect(Collectors.toList());

            // Calculate the total price
            float totalPrice = menuEntities.stream()
                .map(MenuEntity::getPrice)
                .reduce(0f, Float::sum);
            
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setStatus("placed"); // fixed starting status
            orderEntity.setItems(orderModel.getItems());
            orderEntity.setTotalPrice(totalPrice);
            orderRepository.save(orderEntity);
            
            // Initiate the asynchronous status update after saving the order
            updateOrderStatusAfterDelay(orderEntity.getId());
        
            return Boolean.TRUE;
        } catch (Exception e) {
            System.err.println("Error adding order: " + e.getMessage());
            throw new IllegalArgumentException("Fail to add order");
        }
    }

    @Override
    public Boolean updateOrder(Long id, OrderModel orderModel) {
        try {
            Optional<OrderEntity> optionalOrder = orderRepository.findById(id);
            if (optionalOrder.isPresent()) {
                OrderEntity orderEntity = optionalOrder.get();
                
                // Fetch menu entities based on item names in the order model
                List<MenuEntity> menuEntities = orderModel.getItems().stream()
                    .map(itemName -> menuRepository.findByName(itemName) // Find MenuEntity by name
                        .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + itemName))) 
                    .collect(Collectors.toList());

                // Calculate the total price
                float totalPrice = menuEntities.stream()
                    .map(MenuEntity::getPrice)
                    .reduce(0f, Float::sum);
                
                // Update the order with the new status, items, and total price
                orderEntity.setItems(orderModel.getItems());  // Assuming items are a list of names
                orderEntity.setTotalPrice(totalPrice);
                orderRepository.save(orderEntity);
                return Boolean.TRUE;
            } else {
                throw new RuntimeException("Order not found");
            }
        } catch (Exception e) {
            System.err.println("Error updating order: " + e.getMessage());
            throw new IllegalArgumentException("Fail to update order");
        }
    }

    @Override
    public Boolean deleteOrder(Long id) {
        try {
            Optional<OrderEntity> optionalOrderEntity = orderRepository.findById(id);
            if (optionalOrderEntity.isPresent()) {
                orderRepository.delete(optionalOrderEntity.get());
                return Boolean.TRUE;
            } else {
                throw new RuntimeException("Order not found");
            }
        } catch (Exception e) {
            System.err.println("Error deleting order: " + e.getMessage());
            throw new IllegalArgumentException("Failed to delete order");
        }
    }

    @Override
    public List<OrderModel> getOrderListByFilter(String filter, String sortedBy, String order) {
        List<OrderEntity> orderEntities;
        
        if (filter != null && !filter.isEmpty()) {
            orderEntities = orderRepository.findByStatus(filter);
        } else {
            orderEntities = orderRepository.findAll();
        }

        // Example sort by status
        if ("desc".equalsIgnoreCase(order)) {
            orderEntities.sort((menu1, menu2) -> menu2.getStatus().compareTo(menu1.getStatus())); 
        } else {
            orderEntities.sort((menu1, menu2) -> menu1.getStatus().compareTo(menu2.getStatus())); 
        }
                    
        return orderEntities.stream().map(orderEntity -> {
            // Explicitly map MenuEntity to MenuModel
            OrderModel model = new OrderModel();
            model.setId(orderEntity.getId());
            model.setStatus(orderEntity.getStatus());
            model.setItems(orderEntity.getItems());
            model.setTotalPrice(orderEntity.getTotalPrice());
            return model;
        }).collect(Collectors.toList());
    }

    @Override
    public String trackOrder(Long id) {
        try {
            OrderEntity orderEntity = orderRepository.findById(id).orElseThrow();

            // Return the status of the entire order as a string
            return orderEntity.getStatus();
        } catch (Exception e) {
            // Handle exceptions (e.g., order not found)
            return "Order not found or error occurred";
        }
    }

    @Override
    public Boolean cancelOrder(Long id) {
        try {
            OrderEntity orderEntity = orderRepository.findById(id).orElseThrow();

            // Set the order status to "Cancelled"
            orderEntity.setStatus("cancelled");

            // Optionally, you can also cancel individual items if needed
            // orderEntity.getItems().forEach(item -> item.setStatus("Cancelled"));

            // Save the updated order entity
            orderRepository.save(orderEntity);
            return Boolean.TRUE;
        } catch (Exception e) {
            System.err.println("Error cancelling order: " + e.getMessage());
            throw new IllegalArgumentException("Fail to cancel order");
        }
    }
    
    @Transactional
    @Async
    private CompletableFuture<Void> updateOrderStatusAfterDelay(Long orderId) {
        try {
            // Define the statuses to progress through
            List<String> statuses = List.of("placed", "preparing", "delivering", "served");

            for (String nextStatus : statuses) {
                // Fetch the current order entity from the database
                OrderEntity orderEntity = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

                // Explicitly refresh the entity to ensure the latest state
                orderRepository.refreshOrder(orderId);
            
                // Check if the status is "cancelled"
                if ("cancelled".equalsIgnoreCase(orderEntity.getStatus())) {
                    break; // Exit the loop if the order is cancelled
                }

                // Update the status to the next progression
                orderEntity.setStatus(nextStatus);
                orderRepository.save(orderEntity);

                // Simulate delay before moving to the next status
                Thread.sleep(10000); // 10 seconds between each status update
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted state
            System.err.println("Async process interrupted for Order ID: " + orderId);
        } catch (RuntimeException e) {
            System.err.println("Error updating order status for Order ID: " + orderId + ", " + e.getMessage());
        }

        return CompletableFuture.completedFuture(null);
    }
    
    @Override
    @Transactional
    public void refreshOrder(Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        entityManager.refresh(orderEntity); // Refreshes the entity state from the database
    }
}
