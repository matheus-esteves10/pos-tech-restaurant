package br.com.fiap.restaurant.controller;

import br.com.fiap.restaurant.config.swagger.ApiErrorExamples;
import br.com.fiap.restaurant.dto.request.CreateOrderRequest;
import br.com.fiap.restaurant.dto.response.OrderResponse;
import br.com.fiap.restaurant.exception.ErrorResponse;
import br.com.fiap.restaurant.model.User;
import br.com.fiap.restaurant.service.impl.OrderServiceImpl;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant/{restaurantId}/order")
@RequiredArgsConstructor
@Tag(name = "Order")
public class OrderController {

    private final OrderServiceImpl orderService;

    @Operation(summary = "Create a new order for a restaurant")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Validation failed", value = ApiErrorExamples.VALIDATION_ERROR))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED))),
            @ApiResponse(responseCode = "404", description = "Restaurant or item not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Not found", value = ApiErrorExamples.ENTITY_NOT_FOUND)))
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@PathVariable Long restaurantId,
                                                      @Valid @RequestBody CreateOrderRequest request,
                                                      @AuthenticationPrincipal User authenticatedUser) {
        final OrderResponse orderResponse = orderService.createOrder(request, restaurantId, authenticatedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

    @Operation(summary = "Cancel an existing order of a restaurant")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED))),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not associated with the restaurant",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Forbidden", value = ApiErrorExamples.FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = "Restaurant or order not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Not found", value = ApiErrorExamples.ENTITY_NOT_FOUND))),
            @ApiResponse(responseCode = "409", description = "Order is not in progress",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Order already delivered", value = ApiErrorExamples.ORDER_ALREADY_DELIVERED)))
    })
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long restaurantId,
                                            @PathVariable Long orderId,
                                            @AuthenticationPrincipal User authenticatedUser) {
        orderService.cancelOrder(restaurantId, orderId, authenticatedUser);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Mark an existing order of a restaurant as delivered")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED))),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not associated with the restaurant",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Forbidden", value = ApiErrorExamples.FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = "Restaurant or order not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Not found", value = ApiErrorExamples.ENTITY_NOT_FOUND))),
            @ApiResponse(responseCode = "409", description = "Order is not in progress",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Order already canceled", value = ApiErrorExamples.ORDER_ALREADY_CANCELED)))
    })
    @PatchMapping("/{orderId}/deliver")
    public ResponseEntity<Void> deliverOrder(@PathVariable Long restaurantId,
                                             @PathVariable Long orderId,
                                             @AuthenticationPrincipal User authenticatedUser) {
        orderService.deliverOrder(restaurantId, orderId, authenticatedUser);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List all orders of a restaurant")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED))),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not associated with the restaurant",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Forbidden", value = ApiErrorExamples.FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = "Restaurant not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Not found", value = ApiErrorExamples.ENTITY_NOT_FOUND)))
    })
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(@PathVariable Long restaurantId,
                                                         @AuthenticationPrincipal User authenticatedUser) {
        final List<OrderResponse> orders = orderService.getOrdersByRestaurant(restaurantId, authenticatedUser);
        return ResponseEntity.ok(orders);
    }
}
