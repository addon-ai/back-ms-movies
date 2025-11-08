package com.example.userservice.domain.ports.input;

import com.example.userservice.application.dto.user.CreateUserRequestContent;
import com.example.userservice.application.dto.user.CreateUserResponseContent;
import com.example.userservice.application.dto.user.GetUserResponseContent;
import com.example.userservice.application.dto.user.UpdateUserRequestContent;
import com.example.userservice.application.dto.user.UpdateUserResponseContent;
import com.example.userservice.application.dto.user.DeleteUserResponseContent;
import com.example.userservice.application.dto.user.ListUsersResponseContent;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

/**
 * Consolidated use case interface for all User operations.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public interface UserUseCase {
    
    Mono<CreateUserResponseContent> create(CreateUserRequestContent request);

    Mono<GetUserResponseContent> get(String userId);

    Mono<UpdateUserResponseContent> update(String userId, UpdateUserRequestContent request);

    Mono<DeleteUserResponseContent> delete(String userId);

    Mono<ListUsersResponseContent> list(Integer page, Integer size, String search);

}