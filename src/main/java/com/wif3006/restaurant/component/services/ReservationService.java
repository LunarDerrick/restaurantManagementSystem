/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.wif3006.restaurant.component.services;

import com.wif3006.restaurant.component.dtos.ReservationModel;
import java.util.List;

/**
 *
 * @author derri
 */
public interface ReservationService {

    // CRUD: manageReservation()
    
    // bookTable(), Table.assignTable()
    Boolean addReservation(ReservationModel reservationModel);

    // Table.assignTable()
    Boolean updateReservation(Long id, ReservationModel reservationModel);

    Boolean deleteReservation(Long id);

    // viewAvailableTables(), Table.checkAvailability()
    List<ReservationModel> getReservationListByFilter(String filter, String sortedBy, String order);
}
