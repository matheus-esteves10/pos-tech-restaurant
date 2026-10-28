package br.com.fiap.restaurant.presentation.controller;

import br.com.fiap.restaurant.application.usecase.auth.AuthToken;
import br.com.fiap.restaurant.application.usecase.auth.LoginUseCase;
import br.com.fiap.restaurant.presentation.dto.request.LoginRequest;
import br.com.fiap.restaurant.presentation.dto.response.LoginResponse;
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

    @Mock LoginUseCase loginUseCase;
    @InjectMocks AuthController controller;

    @Test
    void returnsTokenFromLoginUseCase() {
        when(loginUseCase.execute("jane", "secret")).thenReturn(new AuthToken("jwt", "Bearer", 3600L));

        var response = controller.login(new LoginRequest("jane", "secret"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new LoginResponse("jwt", "Bearer", 3600L), response.getBody());
    }
}
