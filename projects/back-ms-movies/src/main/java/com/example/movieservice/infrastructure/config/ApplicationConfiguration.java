package com.example.movieservice.infrastructure.config;

import com.example.movieservice.domain.ports.input.RentalUseCase;
import com.example.movieservice.domain.ports.output.RentalRepositoryPort;
import com.example.movieservice.application.service.RentalService;
import com.example.movieservice.application.mapper.RentalMapper;
import com.example.movieservice.infrastructure.adapters.output.persistence.adapter.RentalRepositoryAdapter;
import com.example.movieservice.infrastructure.adapters.output.persistence.repository.JpaRentalRepository;
import com.example.movieservice.domain.ports.input.MovieUseCase;
import com.example.movieservice.domain.ports.output.MovieRepositoryPort;
import com.example.movieservice.application.service.MovieService;
import com.example.movieservice.application.mapper.MovieMapper;
import com.example.movieservice.infrastructure.adapters.output.persistence.adapter.MovieRepositoryAdapter;
import com.example.movieservice.infrastructure.adapters.output.persistence.repository.JpaMovieRepository;

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
     * Configures the Rental use case implementation.
     * 
     * @param rentalRepositoryPort the repository port
     * @param rentalMapper the mapper
     * @return RentalUseCase implementation
     */
    @Bean
    public RentalUseCase rentalUseCase(RentalRepositoryPort rentalRepositoryPort, RentalMapper rentalMapper) {
        return new RentalService(rentalRepositoryPort, rentalMapper);
    }

    /**
     * Configures the Rental repository port implementation.
     * 
     * @param jpaRentalRepository the JPA repository
     * @param rentalMapper the mapper
     * @return RentalRepositoryPort implementation
     */
    @Bean
    public RentalRepositoryPort rentalRepositoryPort(JpaRentalRepository jpaRentalRepository, RentalMapper rentalMapper) {
        return new RentalRepositoryAdapter(jpaRentalRepository, rentalMapper);
    }

    /**
     * Configures the Movie use case implementation.
     * 
     * @param movieRepositoryPort the repository port
     * @param movieMapper the mapper
     * @return MovieUseCase implementation
     */
    @Bean
    public MovieUseCase movieUseCase(MovieRepositoryPort movieRepositoryPort, MovieMapper movieMapper) {
        return new MovieService(movieRepositoryPort, movieMapper);
    }

    /**
     * Configures the Movie repository port implementation.
     * 
     * @param jpaMovieRepository the JPA repository
     * @param movieMapper the mapper
     * @return MovieRepositoryPort implementation
     */
    @Bean
    public MovieRepositoryPort movieRepositoryPort(JpaMovieRepository jpaMovieRepository, MovieMapper movieMapper) {
        return new MovieRepositoryAdapter(jpaMovieRepository, movieMapper);
    }

}