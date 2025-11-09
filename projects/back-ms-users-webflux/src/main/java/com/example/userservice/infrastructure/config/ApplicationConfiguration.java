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


}