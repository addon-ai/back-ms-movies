package com.example.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * Main Spring Boot application class for the .
 * <p>
 * This class serves as the entry point for the Spring Boot application,
 * enabling auto-configuration and component scanning for the entire application.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@SpringBootApplication
@EnableR2dbcRepositories
public class UserServiceWebFluxApplication {

    /**
     * Main method to start the Spring Boot application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceWebFluxApplication.class, args);
    }
}