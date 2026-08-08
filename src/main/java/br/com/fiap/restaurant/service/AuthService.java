package br.com.fiap.restaurant.service;

import br.com.fiap.restaurant.dto.request.LoginRequest;
import br.com.fiap.restaurant.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
