package com.example.userservice.domain.ports.output;


import com.example.userservice.domain.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for User operations.
 * <p>
 * This interface defines the contract for User persistence operations,
 * serving as an output port in the Clean Architecture. It abstracts
 * the persistence layer from the domain logic.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public interface UserRepositoryPort {
    
    User save(User user);
    
    Optional<User> findById(String id);
    
    List<User> findAll();
    
    List<User> findBySearchTerm(String search, Integer page, Integer size);
    
    void deleteById(String id);
    
    boolean existsById(String id);
}
