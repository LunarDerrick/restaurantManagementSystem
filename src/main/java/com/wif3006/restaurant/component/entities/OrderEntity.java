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
    
    @OneToMany(cascade = CascadeType.ALL) 
    @JoinColumn(name = "order_id", referencedColumnName = "id") // Foreign key in the items table
    private List<MenuEntity> items; // Represents the list of menu items in the order
    
    @Column(name = "total_price", nullable = false)
    private float totalPrice;
}
