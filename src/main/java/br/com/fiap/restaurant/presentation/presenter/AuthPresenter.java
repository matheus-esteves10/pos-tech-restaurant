package br.com.fiap.restaurant.presentation.presenter;

import br.com.fiap.restaurant.application.usecase.auth.AuthToken;
import br.com.fiap.restaurant.presentation.dto.response.LoginResponse;

public final class AuthPresenter {

    private AuthPresenter() {
    }

    public static LoginResponse toResponse(AuthToken token) {
        return new LoginResponse(token.token(), token.tokenType(), token.expiresInMs());
    }
}
