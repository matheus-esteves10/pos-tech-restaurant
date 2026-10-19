package br.com.fiap.restaurant.presentation.controller;

import br.com.fiap.restaurant.application.dto.request.LoginRequest;
import br.com.fiap.restaurant.application.dto.response.LoginResponse;
import br.com.fiap.restaurant.application.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock AuthService service;
    @InjectMocks AuthController controller;

    @Test void returnsLoginResponse() {
        var request = new LoginRequest("jane", "password123");
        var response = new LoginResponse("token", "Bearer", 60000);
        when(service.login(request)).thenReturn(response);
        var result = controller.login(request);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }
}
