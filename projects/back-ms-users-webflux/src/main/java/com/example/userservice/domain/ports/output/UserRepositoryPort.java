package com.example.userservice.domain.ports.output;


import com.example.userservice.domain.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Domain repository port for User operations.
 * <p>
 * This interface defines the contract for User persistence operations,
 * serving as an output port in the Clean Architecture. It abstracts
 * the persistence layer from the domain logic using reactive types.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public interface UserRepositoryPort {
    
    Mono<User> save(User user);
    
    Mono<User> findById(String id);
    
    Flux<User> findAll();
    
    Flux<User> findBySearchTerm(String search, Integer page, Integer size);
    
    Flux<User> findAllPaged(Integer page, Integer size);
    
    Mono<Void> deleteById(String id);
    
    Mono<Boolean> existsById(String id);
}
