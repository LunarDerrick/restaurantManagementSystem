/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.wif3006.restaurant.component.services;

import com.wif3006.restaurant.component.dtos.InventoryModel;
import com.wif3006.restaurant.component.dtos.OrderModel;
import java.util.List;

/**
 *
 * @author derri
 */
public interface InventoryService {

    Boolean addInventory(InventoryModel inventoryModel);

    Boolean updateInventory(Long id, InventoryModel inventoryModel);

    Boolean deleteInventory(Long id);

    List<InventoryModel> getInventoryListByFilter(String filter, String sortedBy, String order);

    String alertLowStock();

    void reduceStockOnOrder(OrderModel orderModel, Long orderId);

    Boolean isStockSufficient(OrderModel orderModel);
}
