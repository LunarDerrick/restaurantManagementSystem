/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.wif3006.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 *
 * @author derri
 */
@SpringBootApplication
@EnableAsync
public class SpringbootApplication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringbootApplication.class, args);
        System.out.println("\nWELCOME TO RESTAURANT MANAGEMENT SYSTEM");
    }

}
