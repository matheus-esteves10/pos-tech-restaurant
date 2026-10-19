package br.com.fiap.restaurant.application.service;

import br.com.fiap.restaurant.application.dto.response.UserResponse;
import br.com.fiap.restaurant.domain.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    User findById(Long userId);

    Page<UserResponse> findByName(String name, Pageable pageable);
}
