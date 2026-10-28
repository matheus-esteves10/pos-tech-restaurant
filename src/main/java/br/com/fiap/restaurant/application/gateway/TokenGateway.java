package br.com.fiap.restaurant.application.gateway;

import br.com.fiap.restaurant.domain.model.User;

public interface TokenGateway {

    String generateToken(User user);

    long getExpirationMs();
}
