package br.com.fiap.restaurant.presentation.controller;

import br.com.fiap.restaurant.presentation.swagger.ApiErrorExamples;
import br.com.fiap.restaurant.presentation.dto.request.CreateItemRequest;
import br.com.fiap.restaurant.presentation.dto.request.UpdateItemRequest;
import br.com.fiap.restaurant.presentation.dto.response.ItemResponse;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.application.usecase.item.CreateItemUseCase;
import br.com.fiap.restaurant.application.usecase.item.DeleteItemUseCase;
import br.com.fiap.restaurant.application.usecase.item.GetItemUseCase;
import br.com.fiap.restaurant.application.usecase.item.ListRestaurantItemsUseCase;
import br.com.fiap.restaurant.application.usecase.item.UpdateItemUseCase;
import br.com.fiap.restaurant.presentation.presenter.ItemPresenter;
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
import org.springframework.http.ProblemDetail;
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

import static br.com.fiap.restaurant.presentation.common.ApiPaths.API_V1;

@RestController
@RequestMapping(API_V1 + "/restaurant/{restaurantId}/item")
@RequiredArgsConstructor
@Tag(name = "Item")
public class ItemController {

    private final CreateItemUseCase createItemUseCase;
    private final UpdateItemUseCase updateItemUseCase;
    private final DeleteItemUseCase deleteItemUseCase;
    private final ListRestaurantItemsUseCase listRestaurantItemsUseCase;
    private final GetItemUseCase getItemUseCase;

    @Operation(summary = "List the active items of a restaurant's menu")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED))),
            @ApiResponse(responseCode = "404", description = "Restaurant not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(name = "Not found", value = ApiErrorExamples.ENTITY_NOT_FOUND)))
    })
    @GetMapping
    public ResponseEntity<List<ItemResponse>> getItems(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(ItemPresenter.toResponse(listRestaurantItemsUseCase.execute(restaurantId)));
    }

    @Operation(summary = "Get an active item of a restaurant's menu")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED))),
            @ApiResponse(responseCode = "404", description = "Restaurant or item not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(name = "Not found", value = ApiErrorExamples.ENTITY_NOT_FOUND)))
    })
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponse> getItem(@PathVariable Long restaurantId, @PathVariable Long itemId) {
        return ResponseEntity.ok(ItemPresenter.toResponse(getItemUseCase.execute(restaurantId, itemId)));
    }

    @Operation(summary = "Create a new item for a restaurant")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(name = "Validation failed", value = ApiErrorExamples.VALIDATION_ERROR))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED))),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not associated with the restaurant",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(name = "Forbidden", value = ApiErrorExamples.FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = "Restaurant not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(name = "Not found", value = ApiErrorExamples.ENTITY_NOT_FOUND)))
    })
    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@PathVariable Long restaurantId,
                                                   @Valid @RequestBody CreateItemRequest request,
                                                   @AuthenticationPrincipal(expression = "user") User authenticatedUser) {
        final ItemResponse itemResponse = ItemPresenter.toResponse(
                createItemUseCase.execute(restaurantId, request.toCommand(), authenticatedUser));
        return ResponseEntity.status(HttpStatus.CREATED).body(itemResponse);
    }

    @Operation(summary = "Update an existing item of a restaurant")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(name = "Validation failed", value = ApiErrorExamples.VALIDATION_ERROR))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED))),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not associated with the restaurant",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(name = "Forbidden", value = ApiErrorExamples.FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = "Restaurant or item not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(name = "Not found", value = ApiErrorExamples.ENTITY_NOT_FOUND)))
    })
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponse> updateItem(@PathVariable Long restaurantId,
                                                   @PathVariable Long itemId,
                                                   @Valid @RequestBody UpdateItemRequest request,
                                                   @AuthenticationPrincipal(expression = "user") User authenticatedUser) {
        final ItemResponse itemResponse = ItemPresenter.toResponse(
                updateItemUseCase.execute(restaurantId, itemId, request.toCommand(), authenticatedUser));
        return ResponseEntity.ok(itemResponse);
    }

    @Operation(summary = "Delete an existing item of a restaurant")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED))),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not associated with the restaurant",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(name = "Forbidden", value = ApiErrorExamples.FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = "Restaurant or item not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(name = "Not found", value = ApiErrorExamples.ENTITY_NOT_FOUND)))
    })
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long restaurantId,
                                           @PathVariable Long itemId,
                                           @AuthenticationPrincipal(expression = "user") User authenticatedUser) {
        deleteItemUseCase.execute(restaurantId, itemId, authenticatedUser);
        return ResponseEntity.noContent().build();
    }
}
