package br.com.fiap.restaurant.controller;

import br.com.fiap.restaurant.config.swagger.ApiErrorExamples;
import br.com.fiap.restaurant.dto.request.LoginRequest;
import br.com.fiap.restaurant.dto.response.LoginResponse;
import br.com.fiap.restaurant.exception.ErrorResponse;
import br.com.fiap.restaurant.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Authenticate a user and obtain a bearer token")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Validation failed", value = ApiErrorExamples.VALIDATION_ERROR))),
            @ApiResponse(responseCode = "401", description = "Invalid login or password",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Invalid credentials", value = ApiErrorExamples.INVALID_CREDENTIALS)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok().body(authService.login(request));
    }
}
