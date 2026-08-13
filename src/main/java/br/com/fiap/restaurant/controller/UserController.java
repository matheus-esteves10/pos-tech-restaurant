package br.com.fiap.restaurant.controller;

import br.com.fiap.restaurant.dto.request.CreateUserRequest;
import br.com.fiap.restaurant.dto.request.UpdateUserRequest;
import br.com.fiap.restaurant.dto.response.UserResponse;
import br.com.fiap.restaurant.model.User;
import br.com.fiap.restaurant.service.UserService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create a new user")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        final UserResponse userResponse = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @Operation(summary = "Update the authenticated user's own account")
    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest request,
                                    @AuthenticationPrincipal User authenticatedUser) {
        final UserResponse userResponse = userService.updateUser(request, authenticatedUser);
        return ResponseEntity.ok().body(userResponse);
    }

    @Operation(summary = "Delete the authenticated user's own account")
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal User authenticatedUser) {
        userService.deleteUser(authenticatedUser);
        return ResponseEntity.noContent().build();
    }
}
