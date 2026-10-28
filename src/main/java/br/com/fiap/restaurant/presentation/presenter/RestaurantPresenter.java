package br.com.fiap.restaurant.presentation.presenter;

import br.com.fiap.restaurant.application.usecase.restaurant.RestaurantDetails;
import br.com.fiap.restaurant.application.usecase.restaurant.RestaurantMembership;
import br.com.fiap.restaurant.domain.model.Restaurant;
import br.com.fiap.restaurant.domain.model.RestaurantMember;
import br.com.fiap.restaurant.presentation.dto.response.RestaurantEmployeeResponse;
import br.com.fiap.restaurant.presentation.dto.response.RestaurantResponse;
import br.com.fiap.restaurant.presentation.dto.response.RestaurantUserResponse;

import java.util.List;

public final class RestaurantPresenter {

    private RestaurantPresenter() {
    }

    public static RestaurantResponse toResponse(RestaurantDetails details) {
        Restaurant restaurant = details.restaurant();
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCnpj(),
                restaurant.getDescription(),
                restaurant.getPhone(),
                restaurant.getKitchenType(),
                restaurant.getOpeningAt(),
                restaurant.getClosingAt(),
                AddressPresenter.toResponse(restaurant.getAddress()),
                ItemPresenter.toResponse(details.items())
        );
    }

    public static List<RestaurantResponse> toResponse(List<RestaurantDetails> restaurants) {
        return restaurants.stream().map(RestaurantPresenter::toResponse).toList();
    }

    public static List<RestaurantEmployeeResponse> toEmployeeResponse(List<RestaurantMember> members) {
        return members.stream()
                .map(member -> new RestaurantEmployeeResponse(
                        member.getUser().getName(),
                        member.getUser().getLogin(),
                        member.getUser().getPhone(),
                        member.getRole()))
                .toList();
    }

    public static RestaurantUserResponse toMembershipResponse(RestaurantMembership membership) {
        return new RestaurantUserResponse(
                membership.restaurant().getName(),
                membership.user().getLogin(),
                membership.role()
        );
    }
}
