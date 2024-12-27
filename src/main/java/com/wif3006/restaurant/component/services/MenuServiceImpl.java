/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wif3006.restaurant.component.services;

import com.wif3006.restaurant.component.dtos.MenuModel;
import com.wif3006.restaurant.component.entities.MenuEntity;
import com.wif3006.restaurant.component.repositories.MenuRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author derri
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuRepository menuRepository;
    
    @Override
    public Boolean addMenu(MenuModel menuModel) {
        try {
            MenuEntity menuEntity = new MenuEntity();
            menuEntity.setName(menuModel.getName());
            menuEntity.setDescription(menuModel.getDescription());
            menuEntity.setPrice(menuModel.getPrice());
            menuEntity.setCategory(menuModel.getCategory());
            menuRepository.save(menuEntity); 
            return Boolean.TRUE;
        } catch (Exception e) {
            System.err.println("Error adding menu: " + e.getMessage());
            throw new IllegalArgumentException("Fail to add menu");
        }
    }

    @Override
    public Boolean updateMenu(Long id, MenuModel menuModel) {
        try {
            Optional<MenuEntity> optionalMenuEntity = menuRepository.findById(id);
            if (optionalMenuEntity.isPresent()) {
                MenuEntity menuEntity = optionalMenuEntity.get();
                menuEntity.setName(menuModel.getName());
                menuEntity.setDescription(menuModel.getDescription());
                menuEntity.setPrice(menuModel.getPrice());
                menuEntity.setCategory(menuModel.getCategory());
                menuRepository.save(menuEntity);
                return Boolean.TRUE;
            } else {
                throw new RuntimeException("Menu not found");
            }
        } catch (Exception e) {
            System.err.println("Error updating menu: " + e.getMessage());
            throw new IllegalArgumentException("Failed to update menu");
        }
    }

    @Override
    public Boolean deleteMenu(Long id) {
        try {
            Optional<MenuEntity> optionalMenuEntity = menuRepository.findById(id);
            if (optionalMenuEntity.isPresent()) {
                menuRepository.delete(optionalMenuEntity.get());
                return Boolean.TRUE;
            } else {
                throw new RuntimeException("Menu not found");
            }
        } catch (Exception e) {
            System.err.println("Error deleting menu: " + e.getMessage());
            throw new IllegalArgumentException("Failed to delete menu");
        }
    }

    @Override
    public List<MenuModel> getMenuListByFilter(String filter, String sortedBy, String order) {
        try {
            List<MenuEntity> menuEntities;
            
            // Example filtering by category
            if (filter != null && !filter.isEmpty()) {
                menuEntities = menuRepository.findByCategory(filter);
            } else {
                menuEntities = menuRepository.findAll();
            }

            // Sorting logic (you can add dynamic sorting based on `sortedBy` and `order`)
            if ("desc".equalsIgnoreCase(order)) {
                menuEntities.sort((menu1, menu2) -> menu2.getName().compareTo(menu1.getName())); // Example sort by name
            } else {
                menuEntities.sort((menu1, menu2) -> menu1.getName().compareTo(menu2.getName())); // Example sort by name
            }

            // Convert List<MenuEntity> to List<MenuModel>
            return menuEntities.stream().map(menuEntity -> {
                MenuModel menuModel = new MenuModel();
                menuModel.setId(menuEntity.getId());
                menuModel.setName(menuEntity.getName());
                menuModel.setDescription(menuEntity.getDescription());
                menuModel.setPrice(menuEntity.getPrice());
                menuModel.setCategory(menuEntity.getCategory());
                return menuModel;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            return null; // Return null or an empty list as appropriate for error handling
        }
    }
    
}
