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
        if (request.email() != null) {
            user.setEmail(request.email());
        }
        if (request.login() != null) {
            user.setLogin(request.login());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        if (request.password() != null) {
            user.setPassword(request.password());
        }
        if (request.address() != null) {
            user.setAddress(mergeAddress(user.getAddress(), request.address()));
        }
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

    private Address mergeAddress(Address currentAddress, AddressRequest request) {
        if (currentAddress == null) {
            return toAddress(request);
        }

        if (request.street() != null) {
            currentAddress.setStreet(request.street());
        }
        if (request.number() != null) {
            currentAddress.setNumber(request.number());
        }
        if (request.neighborhood() != null) {
            currentAddress.setNeighborhood(request.neighborhood());
        }
        if (request.city() != null) {
            currentAddress.setCity(request.city());
        }
        if (request.state() != null) {
            currentAddress.setState(request.state());
        }
        if (request.zipCode() != null) {
            currentAddress.setZipCode(request.zipCode());
        }
        if (request.complement() != null) {
            currentAddress.setComplement(request.complement());
        }

        return currentAddress;
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
