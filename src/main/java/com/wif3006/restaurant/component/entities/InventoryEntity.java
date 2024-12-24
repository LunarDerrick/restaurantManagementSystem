/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wif3006.restaurant.component.entities;

import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author derri
 */
@Entity
@Data
@Table(name="inventories")
public class InventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "quantity", nullable = false)
    private int quantity;
    
    @Column(name = "threshold", nullable = false)
    private int threshold;
}
