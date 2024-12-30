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

/**
 *
 * @author derri
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OrderRepository orderRepository;

    private final InventoryServiceImpl inventoryService;

    // Constructor injection of InventoryServiceImpl
    @Autowired
    public OrderServiceImpl(InventoryServiceImpl inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public Boolean addOrder(OrderModel orderModel) {
        try {
            // Check if stock is sufficient
            if (!inventoryService.isStockSufficient(orderModel)) {
                System.out.println("\nInsufficient stock to place order.\n");
                return Boolean.FALSE;
            }

            // Fetch menu entities based on item names in the order model
            List<MenuEntity> menuEntities = orderModel.getItems().stream()
                    .map(itemName -> menuRepository.findByName(itemName)
                    .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + itemName)))
                    .collect(Collectors.toList());

            // Calculate the total price
            float totalPrice = menuEntities.stream()
                    .map(MenuEntity::getPrice)
                    .reduce(0f, Float::sum);

            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setStatus("placed"); // fixed initial status
            orderEntity.setItems(orderModel.getItems());
            orderEntity.setTotalPrice(totalPrice);
            orderRepository.save(orderEntity);

            // Reduce stock after the order is saved
            // pass null because no need to compare a brand new order
            inventoryService.reduceStockOnOrder(orderModel, null);
            System.out.println(inventoryService.alertLowStock());

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

                // Check if stock is sufficient
                if (!inventoryService.isStockSufficient(orderModel)) {
                    System.out.println("\nInsufficient stock to place order.\n");
                    return Boolean.FALSE;
                }

                // Fetch menu entities based on item names in the order model
                List<MenuEntity> menuEntities = orderModel.getItems().stream()
                        .map(itemName -> menuRepository.findByName(itemName)
                        .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + itemName)))
                        .collect(Collectors.toList());

                // Calculate the total price
                float totalPrice = menuEntities.stream()
                        .map(MenuEntity::getPrice)
                        .reduce(0f, Float::sum);

                // compare & reduce stock before saving
                inventoryService.reduceStockOnOrder(orderModel, orderEntity.getId());
                System.out.println(inventoryService.alertLowStock());

                orderEntity.setItems(orderModel.getItems());
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

//        if (filter != null && !filter.isEmpty()) {
//            orderEntities = orderRepository.findByStatus(filter);
//        } else {
        orderEntities = orderRepository.findAll();
//        }
//
//        // Example sort by status
//        if ("desc".equalsIgnoreCase(order)) {
//            orderEntities.sort((menu1, menu2) -> menu2.getStatus().compareTo(menu1.getStatus()));
//        } else {
//            orderEntities.sort((menu1, menu2) -> menu1.getStatus().compareTo(menu2.getStatus()));
//        }

        return orderEntities.stream().map(orderEntity -> {
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

            return orderEntity.getStatus();
        } catch (Exception e) {
            System.err.println("Error tracking order: " + e.getMessage());
            throw new IllegalArgumentException("Fail to track order");
        }
    }

    @Override
    public Boolean cancelOrder(Long id) {
        try {
            OrderEntity orderEntity = orderRepository.findById(id).orElseThrow();

            orderEntity.setStatus("cancelled");
            orderRepository.save(orderEntity);
            return Boolean.TRUE;
        } catch (Exception e) {
            System.err.println("Error cancelling order: " + e.getMessage());
            throw new IllegalArgumentException("Fail to cancel order");
        }
    }

    @Async
    private CompletableFuture<Void> updateOrderStatusAfterDelay(Long orderId) {
        try {
            // Define the statuses to progress through
            List<String> statuses = List.of("placed", "preparing", "delivering", "served");

            for (String nextStatus : statuses) {
                String orderStatus = orderRepository.getOrderStatusById(orderId);

                // Check if the status is "cancelled"
                if ("cancelled".equalsIgnoreCase(orderStatus)) {
                    break; // Exit the loop if the order is cancelled
                }

                OrderEntity orderEntity = orderRepository.findById(orderId)
                        .orElseThrow(() -> new RuntimeException("Order not found"));

                // Update the status to the next progression
                orderEntity.setStatus(nextStatus);
                orderRepository.save(orderEntity);

                // Simulate delay before moving to the next status
                Thread.sleep(10000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted state
            System.err.println("Async process interrupted for Order ID: " + orderId);
        } catch (RuntimeException e) {
            System.err.println("Error updating order status for Order ID: " + orderId + ", " + e.getMessage());
        }

        return CompletableFuture.completedFuture(null);
    }
    
}
