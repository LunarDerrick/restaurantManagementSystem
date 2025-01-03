/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.wif3006.restaurant.component.repositories;

import com.wif3006.restaurant.component.entities.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author derri
 */
@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {

    // Custom query to find items with low stock
    List<InventoryEntity> findByQuantityLessThan(Integer threshold);

    // for Order CRUD to fetch Menu
    Optional<InventoryEntity> findByName(String name);
}
