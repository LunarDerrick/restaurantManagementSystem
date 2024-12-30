/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.wif3006.restaurant.component.repositories;

import com.wif3006.restaurant.component.entities.OrderEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author derri
 */
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    // Custom query to find orders by status
    List<OrderEntity> findByStatus(String status);

    // native query bypass persistence context & cache
    @Query(value = "SELECT status FROM orders WHERE id = :id", nativeQuery = true)
    String getOrderStatusById(@Param("id") Long id);
}
