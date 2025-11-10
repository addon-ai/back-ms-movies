package com.example.userservice.domain.ports.output;


import com.example.userservice.domain.model.City;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for City operations.
 * <p>
 * This interface defines the contract for City persistence operations,
 * serving as an output port in the Clean Architecture. It abstracts
 * the persistence layer from the domain logic.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public interface CityRepositoryPort {
    
    City save(City city);
    
    Optional<City> findById(String id);
    
    List<City> findAll();
    
    List<City> findBySearchTerm(String search, Integer page, Integer size);
    
    List<City> findByFilters(String search, String status, String dateFrom, String dateTo, Integer page, Integer size);
    
    List<City> findAllPaged(Integer page, Integer size);
    
    void deleteById(String id);
    
    boolean existsById(String id);
}
