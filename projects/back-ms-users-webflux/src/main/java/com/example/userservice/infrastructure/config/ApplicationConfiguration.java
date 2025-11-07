package com.example.userservice.infrastructure.config;

import com.example.userservice.domain.ports.input.UserUseCase;
import com.example.userservice.domain.ports.output.UserRepositoryPort;
import com.example.userservice.application.service.UserService;
import com.example.userservice.application.mapper.UserMapper;
import com.example.userservice.infrastructure.adapters.output.persistence.adapter.UserRepositoryAdapter;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaUserRepository;
import com.example.userservice.domain.ports.input.LocationUseCase;
import com.example.userservice.domain.ports.output.LocationRepositoryPort;
import com.example.userservice.application.service.LocationService;
import com.example.userservice.application.mapper.LocationMapper;
import com.example.userservice.infrastructure.adapters.output.persistence.adapter.LocationRepositoryAdapter;
import com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaLocationRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring configuration class for application beans.
 * <p>
 * This configuration class defines beans required by the application,
 * including security components like password encoders and MapStruct mappers.
 * It serves as the infrastructure configuration in Clean Architecture.
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@Configuration
@Import({OpenApiConfiguration.class})
public class ApplicationConfiguration {

    /**
     * Configures the password encoder bean for security operations.
     * 
     * @return BCryptPasswordEncoder instance for password hashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the User use case implementation.
     * 
     * @param userRepositoryPort the repository port
     * @param userMapper the mapper
     * @return UserUseCase implementation
     */
    @Bean
    public UserUseCase userUseCase(UserRepositoryPort userRepositoryPort, UserMapper userMapper) {
        return new UserService(userRepositoryPort, userMapper);
    }

    /**
     * Configures the User repository port implementation.
     * 
     * @param jpaUserRepository the JPA repository
     * @param userMapper the mapper
     * @return UserRepositoryPort implementation
     */
    @Bean
    public UserRepositoryPort userRepositoryPort(JpaUserRepository jpaUserRepository, UserMapper userMapper) {
        return new UserRepositoryAdapter(jpaUserRepository, userMapper);
    }

    /**
     * Configures the Location use case implementation.
     * 
     * @param locationRepositoryPort the repository port
     * @param locationMapper the mapper
     * @return LocationUseCase implementation
     */
    @Bean
    public LocationUseCase locationUseCase(LocationRepositoryPort locationRepositoryPort, LocationMapper locationMapper) {
        return new LocationService(locationRepositoryPort, locationMapper);
    }

    /**
     * Configures the Location repository port implementation.
     * 
     * @param jpaLocationRepository the JPA repository
     * @param locationMapper the mapper
     * @return LocationRepositoryPort implementation
     */
    @Bean
    public LocationRepositoryPort locationRepositoryPort(JpaLocationRepository jpaLocationRepository, LocationMapper locationMapper) {
        return new LocationRepositoryAdapter(jpaLocationRepository, locationMapper);
    }

}