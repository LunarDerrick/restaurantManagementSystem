/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wif3006.restaurant.component.dtos;

import java.util.List;

/**
 *
 * @author derri
 */
public class OrderModel {
    private Long id;
    private String status;
    private List<MenuModel> items;
    private float totalPrice;
}
