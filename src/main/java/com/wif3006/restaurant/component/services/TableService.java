/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.wif3006.restaurant.component.services;

import com.wif3006.restaurant.component.dtos.TableModel;
import java.util.List;

/**
 *
 * @author derri
 */
public interface TableService {
    Boolean addTable(TableModel tableModel);
    Boolean updateTable(Long id, TableModel tableModel);
    Boolean deleteTable(Long id);
    List<TableModel> getTableListByFilter(String filter, String sortedBy, String order);
    
    Boolean checkAvailability();
    Boolean assignTable();
}
