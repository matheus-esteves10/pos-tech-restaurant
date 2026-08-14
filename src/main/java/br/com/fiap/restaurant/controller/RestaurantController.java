package br.com.fiap.restaurant.controller;

import br.com.fiap.restaurant.dto.request.CreateRestaurantRequest;
import br.com.fiap.restaurant.dto.response.RestaurantResponse;
import br.com.fiap.restaurant.model.User;
import br.com.fiap.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
@Tag(name = "Restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Operation(summary = "Create a new restaurant")
    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody CreateRestaurantRequest restaurantRequest,
                                                               @AuthenticationPrincipal User authenticatedUser) {
        final RestaurantResponse restaurantResponse = restaurantService.createRestaurant(restaurantRequest, authenticatedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantResponse);
    }
}
