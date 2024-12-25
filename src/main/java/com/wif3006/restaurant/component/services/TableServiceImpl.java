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
            tableEntity.setAvailable(tableModel.isAvailable());
            tableRepository.save(tableEntity);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean updateTable(Long id, TableModel tableModel) {
        try {
            Optional<TableEntity> optionalTableEntity = tableRepository.findById(id);
            if (optionalTableEntity.isPresent()) {
                TableEntity tableEntity = optionalTableEntity.get();
                tableEntity.setSeatingCapacity(tableModel.getSeatingCapacity());
                tableEntity.setAvailable(tableModel.isAvailable());
                tableRepository.save(tableEntity);
                return true;
            } else {
                return false; // Table not found
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean deleteTable(Long id) {
        try {
            Optional<TableEntity> optionalTableEntity = tableRepository.findById(id);
            if (optionalTableEntity.isPresent()) {
                tableRepository.deleteById(id);
                return true;
            } else {
                return false; // Table not found
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<TableModel> getTableListByFilter(String filter, String sortedBy, String order) {
        try {
            List<TableEntity> tableEntities;

            // Filter logic (e.g., based on availability or seating capacity)
            if ("available".equalsIgnoreCase(filter)) {
                tableEntities = tableRepository.findAll().stream()
                        .filter(TableEntity::isAvailable)
                        .toList();
            } else if ("unavailable".equalsIgnoreCase(filter)) {
                tableEntities = tableRepository.findAll().stream()
                        .filter(table -> !table.isAvailable())
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
                model.setAvailable(entity.isAvailable());
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
                    .filter(TableEntity::isAvailable)
                    .findFirst();

            if (optionalTableEntity.isPresent()) {
                TableEntity tableEntity = optionalTableEntity.get();
                tableEntity.setAvailable(false);
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
