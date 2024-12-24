/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.wif3006.restaurant.component.repositories;

import com.wif3006.restaurant.component.entities.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 *
 * @author derri
 */
@Repository
public interface TableRepository extends JpaRepository<TableEntity, Long> {
    // Optional: Custom query to find tables by availability status
    List<TableEntity> findByIsAvailable(Boolean isAvailable);
}
