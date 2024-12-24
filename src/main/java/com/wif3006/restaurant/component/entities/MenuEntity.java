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
@Table(name="menus")
public class MenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", nullable = false)
    private String description;
    
    @Column(name = "price", nullable = false)
    private float price;
    
    @Column(name = "category", nullable = false)
    private String category;
}
