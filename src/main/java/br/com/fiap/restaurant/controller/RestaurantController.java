package br.com.fiap.restaurant.controller;

import br.com.fiap.restaurant.config.swagger.ApiErrorExamples;
import br.com.fiap.restaurant.dto.request.CreateRestaurantRequest;
import br.com.fiap.restaurant.dto.request.UpdateRestaurantRequest;
import br.com.fiap.restaurant.dto.response.RestaurantResponse;
import br.com.fiap.restaurant.dto.response.RestaurantUserResponse;
import br.com.fiap.restaurant.exception.ErrorResponse;
import br.com.fiap.restaurant.model.User;
import br.com.fiap.restaurant.service.impl.RestaurantServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
@Tag(name = "Restaurant")
public class RestaurantController {

    private final RestaurantServiceImpl restaurantService;

    @Operation(summary = "List all restaurants")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED)))
    })
    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getRestaurants() {
        final List<RestaurantResponse> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @Operation(summary = "Get a restaurant by id")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED))),
            @ApiResponse(responseCode = "404", description = "Restaurant not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Not found", value = ApiErrorExamples.ENTITY_NOT_FOUND)))
    })
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> getRestaurant(@PathVariable Long restaurantId) {
        final RestaurantResponse restaurantResponse = restaurantService.getRestaurant(restaurantId);
        return ResponseEntity.ok(restaurantResponse);
    }

    @Operation(summary = "Create a new restaurant")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Validation failed", value = ApiErrorExamples.VALIDATION_ERROR))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED))),
            @ApiResponse(responseCode = "409", description = "CNPJ already in use",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Duplicate resource", value = ApiErrorExamples.DUPLICATE_RESOURCE)))
    })
    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody CreateRestaurantRequest restaurantRequest,
                                                               @AuthenticationPrincipal User authenticatedUser) {
        final RestaurantResponse restaurantResponse = restaurantService.createRestaurant(restaurantRequest, authenticatedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantResponse);
    }

    @Operation(summary = "Update an existing restaurant")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Validation failed", value = ApiErrorExamples.VALIDATION_ERROR))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED))),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not the restaurant owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Forbidden", value = ApiErrorExamples.FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = "Restaurant not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Not found", value = ApiErrorExamples.ENTITY_NOT_FOUND)))
    })
    @PatchMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(@Valid @RequestBody UpdateRestaurantRequest restaurantRequest,
                                                               @AuthenticationPrincipal User authenticatedUser,
                                                               @PathVariable Long restaurantId) {
        final RestaurantResponse restaurantResponse = restaurantService.updateRestaurant(restaurantRequest,
                authenticatedUser, restaurantId);
        return ResponseEntity.ok(restaurantResponse);
    }

    @Operation(summary = "Add new employee to a restaurant")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED))),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not the restaurant owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Forbidden", value = ApiErrorExamples.FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = "Restaurant or user not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Not found", value = ApiErrorExamples.ENTITY_NOT_FOUND)))
    })
    @PostMapping("/{restaurantId}/employee/{userId}")
    public ResponseEntity<RestaurantUserResponse> addEmployee(@PathVariable Long restaurantId,
                                                              @AuthenticationPrincipal User authenticatedUser,
                                                              @PathVariable Long userId) {
        final RestaurantUserResponse restaurantUserResponse = restaurantService.addUser(restaurantId, authenticatedUser, userId);
        return ResponseEntity.ok(restaurantUserResponse);
    }

    @Operation(summary = "Remove an employee from a restaurant")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED))),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not the restaurant owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Forbidden", value = ApiErrorExamples.FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = "Restaurant or user not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Not found", value = ApiErrorExamples.ENTITY_NOT_FOUND)))
    })
    @DeleteMapping("/{restaurantId}/employee/{userId}")
    public ResponseEntity<RestaurantUserResponse> removeEmployee(@PathVariable Long restaurantId,
                                                                 @AuthenticationPrincipal User authenticatedUser,
                                                                 @PathVariable Long userId) {
        final RestaurantUserResponse restaurantUserResponse = restaurantService.removeUser(restaurantId, authenticatedUser, userId);
        return ResponseEntity.ok(restaurantUserResponse);
    }

    @Operation(summary = "Sets user as owner of a restaurant")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED))),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not the restaurant owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Forbidden", value = ApiErrorExamples.FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = "Restaurant or user not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Not found", value = ApiErrorExamples.ENTITY_NOT_FOUND)))
    })
    @PatchMapping("/{restaurantId}/owner/{userId}")
    public ResponseEntity<Void> setUserAsOwner(@PathVariable Long restaurantId,
                                               @AuthenticationPrincipal User authenticatedUser,
                                               @PathVariable Long userId) {
        restaurantService.setAsOwner(restaurantId, authenticatedUser, userId);
        return ResponseEntity.ok().build();
    }

}
