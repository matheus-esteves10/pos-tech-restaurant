package br.com.fiap.restaurant.controller;

import br.com.fiap.restaurant.dto.request.CreateRestaurantRequest;
import br.com.fiap.restaurant.dto.request.UpdateRestaurantRequest;
import br.com.fiap.restaurant.dto.response.RestaurantResponse;
import br.com.fiap.restaurant.dto.response.RestaurantUserResponse;
import br.com.fiap.restaurant.model.User;
import br.com.fiap.restaurant.service.impl.RestaurantServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
@Tag(name = "Restaurant")
public class RestaurantController {

    private final RestaurantServiceImpl restaurantService;

    @Operation(summary = "Create a new restaurant")
    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody CreateRestaurantRequest restaurantRequest,
                                                               @AuthenticationPrincipal User authenticatedUser) {
        final RestaurantResponse restaurantResponse = restaurantService.createRestaurant(restaurantRequest, authenticatedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantResponse);
    }

    @Operation(summary = "Update an existing restaurant")
    @PatchMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(@Valid @RequestBody UpdateRestaurantRequest restaurantRequest,
                                                               @AuthenticationPrincipal User authenticatedUser,
                                                               @PathVariable Long restaurantId) {
        final RestaurantResponse restaurantResponse = restaurantService.updateRestaurant(restaurantRequest,
                authenticatedUser, restaurantId);
        return ResponseEntity.ok(restaurantResponse);
    }

    @Operation(summary = "Add new employee to a restaurant")
    @PostMapping("/{restaurantId}/employee/{userId}")
    public ResponseEntity<RestaurantUserResponse> addEmployee(@PathVariable Long restaurantId,
                                                              @AuthenticationPrincipal User authenticatedUser,
                                                              @PathVariable Long userId) {
        final RestaurantUserResponse restaurantUserResponse = restaurantService.addUser(restaurantId, authenticatedUser, userId);
        return ResponseEntity.ok(restaurantUserResponse);
    }

    @Operation(summary = "Remove an employee from a restaurant")
    @DeleteMapping("/{restaurantId}/employee/{userId}")
    public ResponseEntity<RestaurantUserResponse> removeEmployee(@PathVariable Long restaurantId,
                                                                 @AuthenticationPrincipal User authenticatedUser,
                                                                 @PathVariable Long userId) {
        final RestaurantUserResponse restaurantUserResponse = restaurantService.removeUser(restaurantId, authenticatedUser, userId);
        return ResponseEntity.ok(restaurantUserResponse);
    }

    @Operation(summary = "Sets user as owner of a restaurant")
    @PatchMapping("/{restaurantId}/owner/{userId}")
    public ResponseEntity<Void> setUserAsOwner(@PathVariable Long restaurantId,
                                               @AuthenticationPrincipal User authenticatedUser,
                                               @PathVariable Long userId) {
        restaurantService.setAsOwner(restaurantId, authenticatedUser, userId);
        return ResponseEntity.ok().build();
    }

}
