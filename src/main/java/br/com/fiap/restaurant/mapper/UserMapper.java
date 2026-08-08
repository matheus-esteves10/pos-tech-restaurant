package br.com.fiap.restaurant.mapper;

import br.com.fiap.restaurant.common.audit.Address;
import br.com.fiap.restaurant.dto.request.AddressRequest;
import br.com.fiap.restaurant.dto.request.CreateUserRequest;
import br.com.fiap.restaurant.dto.request.UpdateUserRequest;
import br.com.fiap.restaurant.dto.response.AddressResponse;
import br.com.fiap.restaurant.dto.response.UserResponse;
import br.com.fiap.restaurant.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(CreateUserRequest request, String encodedPassword) {
        return User.builder()
                .name(request.name())
                .email(request.email())
                .login(request.login())
                .phone(request.phone())
                .password(encodedPassword)
                .userType(request.userType())
                .address(toAddress(request.address()))
                .build();
    }

    public void updateEntity(User user, UpdateUserRequest request) {
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setAddress(toAddress(request.address()));
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getPhone(),
                user.getUserType(),
                user.getEnabled(),
                toAddressResponse(user.getAddress())
        );
    }

    private Address toAddress(AddressRequest request) {
        if (request == null) {
            return null;
        }
        return Address.builder()
                .street(request.street())
                .number(request.number())
                .neighborhood(request.neighborhood())
                .city(request.city())
                .state(request.state())
                .zipCode(request.zipCode())
                .complement(request.complement())
                .build();
    }

    private AddressResponse toAddressResponse(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressResponse(
                address.getStreet(),
                address.getNumber(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getComplement()
        );
    }
}
