/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.wif3006.restaurant.component.repositories;

import com.wif3006.restaurant.component.entities.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author derri
 */
public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {
    // Repository handles functions from Service that requires custom SQL queries.
}
