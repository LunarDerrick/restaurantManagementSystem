/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.wif3006.restaurant.component.repositories;

import com.wif3006.restaurant.component.entities.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author derri
 */
@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Long> {

    // Custom query to find menu items by category
    List<MenuEntity> findByCategory(String category);

    // for Order CRUD to fetch Menu
    Optional<MenuEntity> findByName(String name);
}
