/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wif3006.restaurant.component.dtos;

import lombok.Data;

/**
 *
 * @author derri
 */
@Data
public class TableModel {
    private Long id;
    private int seatingCapacity;
    private Boolean isAvailable;
}
