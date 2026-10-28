package br.com.fiap.restaurant.application.usecase.user;

import br.com.fiap.restaurant.domain.model.Address;

public record UpdateUserCommand(String email,
                                String login,
                                String phone,
                                Address address) {
}
