/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wif3006.restaurant.component.entities;

import java.util.List;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author derri
 */
@Entity
@Data
@Table(name="orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    @Column(name = "items", nullable = true) // Store as a single column in the table
    @Convert(converter = StringListConverter.class) // Use the custom converter
    private List<String> items; // Store menu names as a single string
    
    @Column(name = "total_price", nullable = false)
    private float totalPrice;
}
