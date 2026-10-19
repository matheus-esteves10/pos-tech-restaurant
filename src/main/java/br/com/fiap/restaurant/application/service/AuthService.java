package br.com.fiap.restaurant.application.service;

import br.com.fiap.restaurant.application.dto.request.LoginRequest;
import br.com.fiap.restaurant.application.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
