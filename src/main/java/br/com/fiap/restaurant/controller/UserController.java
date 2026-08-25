package br.com.fiap.restaurant.controller;

import br.com.fiap.restaurant.config.swagger.ApiErrorExamples;
import br.com.fiap.restaurant.dto.request.CreateUserRequest;
import br.com.fiap.restaurant.dto.request.UpdateUserPasswordRequest;
import br.com.fiap.restaurant.dto.request.UpdateUserRequest;
import br.com.fiap.restaurant.dto.response.UserResponse;
import br.com.fiap.restaurant.exception.ErrorResponse;
import br.com.fiap.restaurant.model.User;
import br.com.fiap.restaurant.service.UserService;
import br.com.fiap.restaurant.service.impl.UserServiceImpl;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {

    private final UserServiceImpl userService;

    @Operation(summary = "List all users")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED)))
    })
    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers() {
        final List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get a user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Not found", value = ApiErrorExamples.ENTITY_NOT_FOUND)))
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        final UserResponse userResponse = userService.getUser(userId);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "Create a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Validation failed", value = ApiErrorExamples.VALIDATION_ERROR))),
            @ApiResponse(responseCode = "409", description = "Email, login or phone already in use",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Duplicate resource", value = ApiErrorExamples.DUPLICATE_RESOURCE)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        final UserResponse userResponse = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @Operation(summary = "Update the authenticated user's own account")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Validation failed", value = ApiErrorExamples.VALIDATION_ERROR))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED))),
            @ApiResponse(responseCode = "409", description = "Email, login or phone already in use",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Duplicate resource", value = ApiErrorExamples.DUPLICATE_RESOURCE)))
    })
    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest request,
                                    @AuthenticationPrincipal User authenticatedUser) {
        final UserResponse userResponse = userService.updateUser(request, authenticatedUser);
        return ResponseEntity.ok().body(userResponse);
    }

    @Operation(summary = "Delete the authenticated user's own account")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED)))
    })
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal User authenticatedUser) {
        userService.deleteUser(authenticatedUser);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update the password of the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Validation failed", value = ApiErrorExamples.VALIDATION_ERROR))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Unauthenticated", value = ApiErrorExamples.UNAUTHENTICATED)))
    })
    @PatchMapping("/me/password")
    public ResponseEntity<UserResponse> updatePassword(@Valid @RequestBody UpdateUserPasswordRequest request,
                                                       @AuthenticationPrincipal User authenticatedUser) {
        final UserResponse userResponse = userService.updatePassword(request, authenticatedUser);
        return ResponseEntity.ok().body(userResponse);
    }
}
