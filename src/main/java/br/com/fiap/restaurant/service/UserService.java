package br.com.fiap.restaurant.service;

import br.com.fiap.restaurant.model.User;

public interface UserService {

    User findById(Long userId);
}
