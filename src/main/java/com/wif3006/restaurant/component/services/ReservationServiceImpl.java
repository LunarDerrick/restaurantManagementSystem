/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wif3006.restaurant.component.services;

import com.wif3006.restaurant.component.dtos.ReservationModel;
import com.wif3006.restaurant.component.dtos.TableModel;
import com.wif3006.restaurant.component.entities.ReservationEntity;
import com.wif3006.restaurant.component.entities.TableEntity;
import com.wif3006.restaurant.component.repositories.ReservationRepository;
import com.wif3006.restaurant.component.repositories.TableRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author derri
 */
@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private TableRepository tableRepository;
        
    @Override
    public Boolean addReservation(ReservationModel reservationModel) {
        try {
            // Check if the table is available
            TableEntity table = tableRepository.findById(reservationModel.getTableId())
                    .orElseThrow(() -> new IllegalArgumentException("Table not found"));

            if (!table.getIsAvailable()) {
                throw new IllegalStateException("Table is already booked");
            }

            // Mark the table as booked
            table.setIsAvailable(false);
            tableRepository.save(table);
            
            // Create reservation
            ReservationEntity reservationEntity = new ReservationEntity();
            reservationEntity.setCustomerName(reservationModel.getCustomerName());
            reservationEntity.setDateTime(reservationModel.getDateTime());
            Optional<TableEntity> tableOptional = tableRepository.findById(reservationModel.getTableId());
            if (tableOptional.isPresent()) {
                reservationEntity.setTable(tableOptional.get());
            } else {
                throw new Exception("Table not found");
            }
            reservationEntity.setPartySize(reservationModel.getPartySize());
            reservationRepository.save(reservationEntity);
            return Boolean.TRUE;
        } catch (Exception e) {
            System.err.println("Error adding reservation: " + e.getMessage());
            throw new IllegalArgumentException("Fail to add reservation");
        }
    }

    @Override
    public Boolean updateReservation(Long id, ReservationModel reservationModel) {
        try {
            Optional<ReservationEntity> reservationOptional = reservationRepository.findById(id);
            if (reservationOptional.isPresent()) {
                ReservationEntity reservationEntity = reservationOptional.get();
                reservationEntity.setCustomerName(reservationModel.getCustomerName());
                reservationEntity.setDateTime(reservationModel.getDateTime());
                
                // Get the current table associated with the reservation
                TableEntity currentTable = reservationEntity.getTable();
                
                Optional<TableEntity> tableOptional = tableRepository.findById(reservationModel.getTableId());
                if (tableOptional.isPresent()) {
                    TableEntity newTable = tableOptional.get();
                    
                    // Revert the availability of the current table
                    currentTable.setIsAvailable(true);
                    tableRepository.save(currentTable);
                    
                    // Check if the new table is available
                    if (!newTable.getIsAvailable()) {
                        throw new IllegalStateException("New table is already booked");
                    }
                    
                    // Mark the new table as not available
                    newTable.setIsAvailable(false);
                    tableRepository.save(newTable);
                    
                    // Update the reservation to use the new table
                    reservationEntity.setTable(tableOptional.get());
                } else {
                    throw new Exception("Table not found");
                }
                
                reservationEntity.setPartySize(reservationModel.getPartySize());
                reservationRepository.save(reservationEntity);
                return Boolean.TRUE;
            } else {
                throw new RuntimeException("Reservation not found");
            }
        } catch (Exception e) {
            System.err.println("Error updating reservation: " + e.getMessage());
            throw new IllegalArgumentException("Failed to update reservation");
        }
    }

    @Override
    public Boolean deleteReservation(Long id) {
        try {
            Optional<ReservationEntity> optionalReservationEntity = reservationRepository.findById(id);
            if (optionalReservationEntity.isPresent()) {
                // Retrieve the table related to the reservation
                ReservationEntity reservationEntity = optionalReservationEntity.get();
                Optional<TableEntity> optionalTableEntity = Optional.ofNullable(reservationEntity.getTable());
                if (optionalTableEntity.isPresent()) {
                    TableEntity tableEntity = optionalTableEntity.get();

                    // Mark the table as available (true)
                    tableEntity.setIsAvailable(true);
                    tableRepository.save(tableEntity);  // Save the updated table status
                } else {
                    throw new IllegalArgumentException("Table associated with the reservation not found");
                }

                // Delete the reservation
                reservationRepository.delete(optionalReservationEntity.get());
                return Boolean.TRUE;
            } else {
                throw new RuntimeException("Reservation not found");
            }
        } catch (Exception e) {
            System.err.println("Error updating reservation: " + e.getMessage());
            throw new IllegalArgumentException("Failed to update reservation");
        }
    }

    @Override
    public List<ReservationModel> getReservationListByFilter(String filter, String sortedBy, String order) {
        List<ReservationEntity> reservationEntities;

        // Filter reservations based on the provided filter (e.g., by customer name)
        if (filter != null && !filter.isEmpty()) {
            Long tableId;
            try {
                tableId = Long.parseLong(filter); // Assuming the filter is a table ID
                reservationEntities = reservationRepository.findByTableId(tableId);
            } catch (NumberFormatException e) {
                reservationEntities = reservationRepository.findAll();
            }
        } else {
            reservationEntities = reservationRepository.findAll();
        }

        // Sort reservations based on the provided sort parameter
        if ("desc".equalsIgnoreCase(order)) {
            reservationEntities.sort((res1, res2) -> {
                if ("date".equalsIgnoreCase(sortedBy)) {
                    return res2.getDateTime().compareTo(res1.getDateTime());
                } else if ("customerName".equalsIgnoreCase(sortedBy)) {
                    return res2.getCustomerName().compareTo(res1.getCustomerName());
                }
                return 0; // Default no sorting
            });
        } else {
            reservationEntities.sort((res1, res2) -> {
                if ("date".equalsIgnoreCase(sortedBy)) {
                    return res1.getDateTime().compareTo(res2.getDateTime());
                } else if ("customerName".equalsIgnoreCase(sortedBy)) {
                    return res1.getCustomerName().compareTo(res2.getCustomerName());
                }
                return 0; // Default no sorting
            });
        }

        // Map ReservationEntity to ReservationModel
        return reservationEntities.stream().map(reservationEntity -> {
            ReservationModel model = new ReservationModel();
            model.setId(reservationEntity.getId());
            model.setCustomerName(reservationEntity.getCustomerName());
            model.setDateTime(reservationEntity.getDateTime());
            model.setTableId(reservationEntity.getTable().getId()); // Assuming TableEntity has a getId() method
            model.setPartySize(reservationEntity.getPartySize());
            return model;
        }).collect(Collectors.toList());
    }
    
}
