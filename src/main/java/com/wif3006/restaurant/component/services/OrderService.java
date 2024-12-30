/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.wif3006.restaurant.component.services;

import com.wif3006.restaurant.component.dtos.OrderModel;
import java.util.List;

/**
 *
 * @author derri
 */
public interface OrderService {

    // placeOrder()
    Boolean addOrder(OrderModel orderModel);

    Boolean updateOrder(Long id, OrderModel orderModel);

    Boolean deleteOrder(Long id);

    List<OrderModel> getOrderListByFilter(String filter, String sortedBy, String order);

    String trackOrder(Long id);

    Boolean cancelOrder(Long id);
}
