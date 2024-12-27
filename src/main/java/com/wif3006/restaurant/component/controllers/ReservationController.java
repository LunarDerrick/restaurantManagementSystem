/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wif3006.restaurant.component.controllers;

import com.wif3006.restaurant.component.dtos.ReservationModel;
import com.wif3006.restaurant.component.dtos.TableModel;
import com.wif3006.restaurant.component.services.ReservationService;
import com.wif3006.restaurant.component.services.TableService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * @author derri
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private TableService tableService;
    
    // POST /api/reservations: Add a new reservation
    @PostMapping
    public ResponseEntity<Boolean> addReservation(@RequestBody ReservationModel reservationModel) {
        return ResponseEntity.ok(reservationService.addReservation(reservationModel));
    }

    // PUT /api/reservations/{id}: Update an existing reservation
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateReservation(@PathVariable Long id, @RequestBody ReservationModel reservationModel) {
        return ResponseEntity.ok(reservationService.updateReservation(id, reservationModel));
    }

    // DELETE /api/reservations/{id}: Delete a reservation
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.deleteReservation(id));
    }
    
    // GET /api/reservations: Get a list of reservations, optionally with filters
    @GetMapping
    public List<ReservationModel> getReservationListByFilter(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String order) {
        return reservationService.getReservationListByFilter(filter, sortedBy, order);
    }

    // GET /api/reservations/tables: View available tables
    @GetMapping("/tables")
    public List<TableModel> viewAvailableTables() {
        return reservationService.viewAvailableTables();
    }

    // POST /api/reservations/tables: Add a new table
    @PostMapping("/tables")
    public ResponseEntity<Boolean> addTable(@RequestBody TableModel tableModel) {
        return ResponseEntity.ok(tableService.addTable(tableModel));
    }

    // PUT /api/reservations/tables/{id}: Update an existing table
    @PutMapping("/tables/{id}")
    public ResponseEntity<Boolean> updateTable(@PathVariable Long id, @RequestBody TableModel tableModel) {
        return ResponseEntity.ok(tableService.updateTable(id, tableModel));
    }

    // DELETE /api/reservations/tables/{id}: Delete a table
    @DeleteMapping("/tables/{id}")
    public ResponseEntity<Boolean> deleteTable(@PathVariable Long id) {
        return ResponseEntity.ok(tableService.deleteTable(id));
    }

    // GET /api/reservations/tables: Get a list of tables, optionally with filters
    @GetMapping("/tables/filter")
    public List<TableModel> getTableListByFilter(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String order) {
        return tableService.getTableListByFilter(filter, sortedBy, order);
    }
    
    // PUT /api/reservations/tables/{id}/assign: Assign a table
    @PutMapping("/tables/{id}/assign")
    public Boolean assignTable(@PathVariable Long id) {
        return tableService.assignTable();
    }
    
}
