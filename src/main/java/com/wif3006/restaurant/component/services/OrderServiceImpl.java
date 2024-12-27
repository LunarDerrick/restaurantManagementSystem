/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wif3006.restaurant.component.services;

import com.wif3006.restaurant.component.dtos.MenuModel;
import com.wif3006.restaurant.component.dtos.OrderModel;
import com.wif3006.restaurant.component.entities.MenuEntity;
import com.wif3006.restaurant.component.entities.OrderEntity;
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
    private OrderRepository orderRepository;
    
    @Override
    public Boolean addOrder(OrderModel orderModel) {
        try {
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setStatus(orderModel.getStatus());
            orderEntity.setItems(orderModel.getItems().stream().map(menuModel -> {
                MenuEntity menuEntity = new MenuEntity();
                menuEntity.setId(menuModel.getId());
                menuEntity.setName(menuModel.getName());
                menuEntity.setPrice(menuModel.getPrice());
                return menuEntity;
            }).collect(Collectors.toList()));
            // TODO: compute total price
            orderEntity.setTotalPrice(orderModel.getTotalPrice());
            orderRepository.save(orderEntity);
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
                orderEntity.setStatus(orderModel.getStatus());
                orderEntity.setItems(orderModel.getItems().stream().map(menuModel -> {
                    MenuEntity menuEntity = new MenuEntity();
                    menuEntity.setId(menuModel.getId());
                    menuEntity.setName(menuModel.getName());
                    menuEntity.setPrice(menuModel.getPrice());
                    return menuEntity;
                }).collect(Collectors.toList()));
                orderEntity.setTotalPrice(orderModel.getTotalPrice());
                orderRepository.save(orderEntity);
                return true;
            } else {
                throw new RuntimeException("Order not found");
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean deleteOrder(Long id) {
        try {
            Optional<OrderEntity> optionalOrderEntity = orderRepository.findById(id);
            if (optionalOrderEntity.isPresent()) {
                orderRepository.delete(optionalOrderEntity.get());
                return true;
            } else {
                throw new RuntimeException("Order not found");
            }
        } catch (Exception e) {
            return false;
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
            List<MenuModel> menuModels = orderEntity.getItems().stream().map(menuEntity -> {
                MenuModel menuModel = new MenuModel();
                menuModel.setId(menuEntity.getId());
                menuModel.setName(menuEntity.getName());
                menuModel.setPrice(menuEntity.getPrice());
                return menuModel;
            }).collect(Collectors.toList());
            
            OrderModel model = new OrderModel();
            model.setId(orderEntity.getId());
            model.setStatus(orderEntity.getStatus());
            model.setItems(menuModels); // Set the mapped List<MenuModel>
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
            orderEntity.setStatus("Cancelled");

            // Optionally, you can also cancel individual items if needed
            // orderEntity.getItems().forEach(item -> item.setStatus("Cancelled"));

            // Save the updated order entity
            orderRepository.save(orderEntity);
            return true;
        } catch (Exception e) {
            // Handle exceptions (e.g., order not found)
            return false;
        }
    }
    
    @Async
    public CompletableFuture<Void> updateOrderStatusAfterDelay(Long orderId) {
        try {            
            Thread.sleep(10000); // Simulate delay in millis

            OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow();
            // Update status after the delay
            orderEntity.setStatus("In Progress");
            orderRepository.save(orderEntity);

        } catch (InterruptedException | RuntimeException e) {
            // Handle exceptions
        }
        return CompletableFuture.completedFuture(null);
    }
}
