package br.com.fiap.restaurant.service;

import br.com.fiap.restaurant.dto.request.CreateUserRequest;
import br.com.fiap.restaurant.dto.request.UpdateUserRequest;
import br.com.fiap.restaurant.dto.response.UserResponse;
import br.com.fiap.restaurant.model.User;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(UpdateUserRequest request, User authenticatedUser);

    void deleteUser(User authenticatedUser);
}
