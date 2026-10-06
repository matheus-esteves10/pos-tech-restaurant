package br.com.fiap.restaurant.service;

import br.com.fiap.restaurant.dto.response.UserResponse;
import br.com.fiap.restaurant.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    User findById(Long userId);

    Page<UserResponse> findByName(String name, Pageable pageable);
}
