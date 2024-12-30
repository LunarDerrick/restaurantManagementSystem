/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wif3006.restaurant.component.dtos;

import lombok.Data;
import java.time.LocalDateTime;

/**
 *
 * @author derri
 */
@Data
public class ReservationModel {

    private Long id;
    private String customerName;
    private LocalDateTime dateTime;
    private Long tableId;
    private int partySize;
}
