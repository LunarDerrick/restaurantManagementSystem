/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wif3006.restaurant.component.entities;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author derri
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    private static final String SEPARATOR = ","; // Separator for the menu items

    // Convert List<String> to a single String (e.g., "Fried Rice,Honey Toast,Honey Toast")
    @Override
    public String convertToDatabaseColumn(List<String> list) {
        if (list == null || list.isEmpty()) {
            return ""; // Return an empty string if the list is null or empty
        }
        return String.join(SEPARATOR, list); // Join the list items with a comma
    }

    // Convert a String (e.g., "Fried Rice,Honey Toast,Honey Toast") back to List<String>
    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return List.of(); // Return an empty list if the string is null or empty
        }
        return Arrays.stream(dbData.split(SEPARATOR)) // Split the string by commas
                     .collect(Collectors.toList()); // Collect the items into a list
    }
}
