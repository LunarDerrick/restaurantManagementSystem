/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wif3006.restaurant.component.services;

import com.wif3006.restaurant.component.dtos.TableModel;
import com.wif3006.restaurant.component.entities.TableEntity;
import com.wif3006.restaurant.component.repositories.TableRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author derri
 */
@Service
public class TableServiceImpl implements TableService {

    @Autowired
    private TableRepository tableRepository;
        
    @Override
    public Boolean addTable(TableModel tableModel) {
        try {
            TableEntity tableEntity = new TableEntity();
            tableEntity.setSeatingCapacity(tableModel.getSeatingCapacity());
            System.out.println("isAvailable: " + tableModel.getIsAvailable());
            tableEntity.setIsAvailable(tableModel.getIsAvailable());
            tableRepository.save(tableEntity);
            return Boolean.TRUE;
        } catch (Exception e) {
            System.err.println("Error adding table: " + e.getMessage());
            throw new IllegalArgumentException("Fail to add table");
        }
    }

    @Override
    public Boolean updateTable(Long id, TableModel tableModel) {
        try {
            Optional<TableEntity> optionalTableEntity = tableRepository.findById(id);
            if (optionalTableEntity.isPresent()) {
                TableEntity tableEntity = optionalTableEntity.get();
                tableEntity.setSeatingCapacity(tableModel.getSeatingCapacity());
                tableEntity.setIsAvailable(tableModel.getIsAvailable());
                tableRepository.save(tableEntity);
                return Boolean.TRUE;
            } else {
                throw new RuntimeException("Table not found");
            }
        } catch (Exception e) {
            System.err.println("Error updating table: " + e.getMessage());
            throw new IllegalArgumentException("Failed to update table");
        }
    }

    @Override
    public Boolean deleteTable(Long id) {
        try {
            Optional<TableEntity> optionalTableEntity = tableRepository.findById(id);
            if (optionalTableEntity.isPresent()) {
                tableRepository.deleteById(id);
                return Boolean.TRUE;
            } else {
                throw new RuntimeException("Table not found");
            }
        } catch (Exception e) {
            System.err.println("Error deleting table: " + e.getMessage());
            throw new IllegalArgumentException("Failed to delete table");
        }
    }

    @Override
    public List<TableModel> getTableListByFilter(String filter, String sortedBy, String order) {
        try {
            List<TableEntity> tableEntities;

            // Filter logic (e.g., based on availability or seating capacity)
            if ("available".equalsIgnoreCase(filter)) {
                tableEntities = tableRepository.findAll().stream()
                        .filter(TableEntity::getIsAvailable)
                        .toList();
            } else if ("unavailable".equalsIgnoreCase(filter)) {
                tableEntities = tableRepository.findAll().stream()
                        .filter(table -> !table.getIsAvailable())
                        .toList();
            } else {
                tableEntities = tableRepository.findAll();
            }

            // Sorting logic
            if ("desc".equalsIgnoreCase(order)) {
                tableEntities.sort((t1, t2) -> Integer.compare(t2.getSeatingCapacity(), t1.getSeatingCapacity()));
            } else {
                tableEntities.sort((t1, t2) -> Integer.compare(t1.getSeatingCapacity(), t2.getSeatingCapacity()));
            }

            // Mapping to TableModel
            return tableEntities.stream().map(entity -> {
                TableModel model = new TableModel();
                model.setId(entity.getId());
                model.setSeatingCapacity(entity.getSeatingCapacity());
                model.setIsAvailable(entity.getIsAvailable());
                return model;
            }).toList();
        } catch (Exception e) {
            return List.of(); // Return an empty list in case of an exception
        }
    }

    @Override
    public Boolean assignTable() {
        try {
            // Find the first available table and mark it as unavailable
            Optional<TableEntity> optionalTableEntity = tableRepository.findAll().stream()
                    .filter(TableEntity::getIsAvailable)
                    .findFirst();

            if (optionalTableEntity.isPresent()) {
                TableEntity tableEntity = optionalTableEntity.get();
                tableEntity.setIsAvailable(false);
                tableRepository.save(tableEntity);
                return true;
            } else {
                return false; // No available tables
            }
        } catch (Exception e) {
            return false;
        }
    }
    
}
