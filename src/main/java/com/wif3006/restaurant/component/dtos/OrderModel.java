/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wif3006.restaurant.component.dtos;

import lombok.Data;
import java.util.List;

/**
 *
 * @author derri
 */
@Data
public class OrderModel {
    private Long id;
    private String status;
    private List<String> items;
    private float totalPrice;
}
