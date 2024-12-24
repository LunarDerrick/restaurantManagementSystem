/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.wif3006.restaurant.component.services;

import com.wif3006.restaurant.component.dtos.MenuModel;
import java.util.List;

/**
 *
 * @author derri
 */
public interface MenuService {
    Boolean addMenu(MenuModel menuModel);
    Boolean updateMenu(Long id, MenuModel menuModel);
    Boolean deleteMenu(Long id);
    List<MenuModel> getMenuListByFilter(String filter, String sortedBy, String order);
    // getCategory()
}
