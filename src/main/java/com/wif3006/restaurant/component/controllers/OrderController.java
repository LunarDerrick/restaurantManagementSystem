/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wif3006.restaurant.component.controllers;

import com.wif3006.restaurant.component.dtos.InventoryModel;
import com.wif3006.restaurant.component.dtos.MenuModel;
import com.wif3006.restaurant.component.dtos.OrderModel;
import com.wif3006.restaurant.component.services.InventoryService;
import com.wif3006.restaurant.component.services.MenuService;
import com.wif3006.restaurant.component.services.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * @author derri
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private MenuService menuService;

    @Autowired
    private InventoryService inventoryService;
    
    //POST /api/orders: Add a new order.
    @PostMapping
    public ResponseEntity<Boolean> addOrder(@RequestBody OrderModel orderModel) {
        return ResponseEntity.ok(orderService.addOrder(orderModel));
    }
    
    //PUT /api/orders/{id}: Update an existing order.
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateOrder(@PathVariable Long id, @RequestBody OrderModel orderModel) {
        return ResponseEntity.ok(orderService.updateOrder(id, orderModel));
    }
    
    //DELETE /api/orders/{id}: Delete an order.
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.deleteOrder(id));
    }

    //GET /api/orders: Get a list of orders, optionally with filters.
    @GetMapping
    public List<OrderModel> getOrderListByFilter(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String order) {
        return orderService.getOrderListByFilter(filter, sortedBy, order);
    }
    
    // Endpoint to track the status of an order
    @GetMapping("/{id}/track")
    public String trackOrder(@PathVariable Long id) {
        return orderService.trackOrder(id);
    }

    // Endpoint to cancel an order
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Boolean> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    //POST /api/orders/menu: Add a menu item.
    @PostMapping("/menu")
    public ResponseEntity<Boolean> addMenu(@RequestBody MenuModel menuModel) {    
        return ResponseEntity.ok(menuService.addMenu(menuModel));
    }
    
    //PUT /api/orders/menu/{id}: Update a menu item.
    @PutMapping("/menu/{id}")
    public ResponseEntity<Boolean> updateMenu(@PathVariable Long id, @RequestBody MenuModel menuModel) {
        return ResponseEntity.ok(menuService.updateMenu(id, menuModel));
    }
    
    //DELETE /api/orders/menu/{id}: Delete a menu item.
    @DeleteMapping("/menu/{id}")
    public ResponseEntity<Boolean> deleteMenu(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.deleteMenu(id));
    }
    
    //GET /api/orders/menu: Get a list of menu items.
    // Endpoint to get the list of menu items filtered by category, with sorting
    @GetMapping("/menu")
    public List<MenuModel> getMenuListByFilter(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String order) {
        return menuService.getMenuListByFilter(category, sortedBy, order);
    }
    
    //POST /api/orders/inventory: Add inventory items.
    @PostMapping("/inventory")
    public ResponseEntity<Boolean> addInventory(@RequestBody InventoryModel inventoryModel) {
        return ResponseEntity.ok(inventoryService.addInventory(inventoryModel));
    }
    
    //PUT /api/orders/inventory/{id}: Update inventory items.
    @PutMapping("/inventory/{id}")
    public ResponseEntity<Boolean> updateInventory(@PathVariable Long id, @RequestBody InventoryModel inventoryModel) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, inventoryModel));
    }
    
    //DELETE /api/orders/inventory/{id}: Delete inventory items.
    @DeleteMapping("/inventory/{id}")
    public ResponseEntity<Boolean> deleteInventory(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.deleteInventory(id));
    }
    
    //GET /api/orders/inventory: Get a list of inventory items.
    @GetMapping("/inventory")
    public List<InventoryModel> getInventoryListByFilter(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String order) {
        return inventoryService.getInventoryListByFilter(filter, sortedBy, order);
    }
}
