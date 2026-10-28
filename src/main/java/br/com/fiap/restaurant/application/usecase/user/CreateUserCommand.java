package br.com.fiap.restaurant.application.usecase.user;

import br.com.fiap.restaurant.domain.model.Address;

public record CreateUserCommand(String name,
                                String email,
                                String login,
                                String phone,
                                String password,
                                Address address) {
}
