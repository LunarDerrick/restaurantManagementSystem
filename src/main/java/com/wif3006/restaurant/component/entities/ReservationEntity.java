/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wif3006.restaurant.component.entities;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author derri
 */
@Entity
@Data
@Table(name="reservations")
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "customer_name", nullable = false)
    private String customerName;
    
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;
    
    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false) // Foreign key in reservations table
    private TableEntity table; // References the booked table
    
    @Column(name = "party_size", nullable = false)
    private int partySize;
}
