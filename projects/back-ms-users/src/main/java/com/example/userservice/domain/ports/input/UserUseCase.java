package com.example.userservice.domain.ports.input;

import com.example.userservice.application.dto.user.CreateUserRequestContent;
import com.example.userservice.application.dto.user.CreateUserResponseContent;
import com.example.userservice.application.dto.user.GetUserResponseContent;
import com.example.userservice.application.dto.user.UpdateUserRequestContent;
import com.example.userservice.application.dto.user.UpdateUserResponseContent;
import com.example.userservice.application.dto.user.DeleteUserResponseContent;
import com.example.userservice.application.dto.user.ListUsersResponseContent;

/**
 * Consolidated use case interface for all User operations.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public interface UserUseCase {
    
    CreateUserResponseContent create(CreateUserRequestContent request);

    GetUserResponseContent get(String userId);

    UpdateUserResponseContent update(String userId, UpdateUserRequestContent request);

    DeleteUserResponseContent delete(String userId);

    ListUsersResponseContent list(Integer page, Integer size, String search, String status, String dateFrom, String dateTo);

}