package br.com.fiap.restaurant.controller;

import br.com.fiap.restaurant.dto.request.CreateItemRequest;
import br.com.fiap.restaurant.dto.request.UpdateItemRequest;
import br.com.fiap.restaurant.dto.response.ItemResponse;
import br.com.fiap.restaurant.model.User;
import br.com.fiap.restaurant.service.impl.ItemServiceImpl;
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
@RequestMapping("/api/restaurant/{restaurantId}/item")
@RequiredArgsConstructor
@Tag(name = "Item")
public class ItemController {

    private final ItemServiceImpl itemService;

    @Operation(summary = "Create a new item for a restaurant")
    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@PathVariable Long restaurantId,
                                                   @Valid @RequestBody CreateItemRequest request,
                                                   @AuthenticationPrincipal User authenticatedUser) {
        final ItemResponse itemResponse = itemService.createItem(request, restaurantId, authenticatedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemResponse);
    }

    @Operation(summary = "Update an existing item of a restaurant")
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponse> updateItem(@PathVariable Long restaurantId,
                                                   @PathVariable Long itemId,
                                                   @Valid @RequestBody UpdateItemRequest request,
                                                   @AuthenticationPrincipal User authenticatedUser) {
        final ItemResponse itemResponse = itemService.updateItem(request, restaurantId, itemId, authenticatedUser);
        return ResponseEntity.ok(itemResponse);
    }

    @Operation(summary = "Delete an existing item of a restaurant")
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long restaurantId,
                                           @PathVariable Long itemId,
                                           @AuthenticationPrincipal User authenticatedUser) {
        itemService.deleteItem(restaurantId, itemId, authenticatedUser);
        return ResponseEntity.noContent().build();
    }
}
