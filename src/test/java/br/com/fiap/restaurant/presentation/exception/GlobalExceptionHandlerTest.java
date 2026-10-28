package br.com.fiap.restaurant.presentation.exception;

import br.com.fiap.restaurant.domain.exception.BusinessRuleException;
import br.com.fiap.restaurant.domain.exception.DomainValidationException;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.exception.ForbiddenOperationException;
import br.com.fiap.restaurant.domain.exception.InvalidCredentialsException;
import br.com.fiap.restaurant.domain.exception.InvalidOrderStatusException;
import br.com.fiap.restaurant.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final HttpServletRequest request = request();

    @Test void mapsNotFoundConflictsAndForbiddenErrors() {
        assertEquals(HttpStatus.NOT_FOUND, handler.handleResourceNotFound(new ResourceNotFoundException("gone"), request).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, handler.handleEntityNotFound(new EntityNotFoundException(), request).getStatusCode());
        assertEquals(HttpStatus.CONFLICT, handler.handleInvalidOrderStatus(new InvalidOrderStatusException("done"), request).getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN, handler.handleForbiddenOperation(new ForbiddenOperationException("jane"), request).getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN, handler.handleAccessDenied(new AccessDeniedException("private"), request).getStatusCode());
    }

    @Test void mapsDomainValidationAndBusinessRuleErrors() {
        var validation = handler.handleDomainValidation(new DomainValidationException("Name cannot be blank"), request);
        assertEquals(HttpStatus.BAD_REQUEST, validation.getStatusCode());
        assertEquals("Name cannot be blank", validation.getBody().getDetail());
        var rule = handler.handleBusinessRule(new BusinessRuleException("already associated"), request);
        assertEquals(HttpStatus.CONFLICT, rule.getStatusCode());
        assertEquals("already associated", rule.getBody().getDetail());
    }

    @Test void mapsInvalidCredentialsAndDatabaseConflicts() {
        assertEquals(HttpStatus.UNAUTHORIZED, handler.handleInvalidCredentials(new InvalidCredentialsException("no"), request).getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, handler.handleInvalidCredentials(new BadCredentialsException("no"), request).getStatusCode());
        var response = handler.handleDataIntegrityViolation(new DataIntegrityViolationException("duplicate", new RuntimeException("unique email")), request);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email already in use", response.getBody().getDetail());
    }

    @Test void returnsGenericErrorAndRequestPath() {
        var response = handler.handleGeneric(new RuntimeException("unexpected"), request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ProblemDetail body = response.getBody();
        assertEquals("unexpected", body.getDetail());
        assertEquals("/api/test", body.getInstance().toString());
        var noMessage = handler.handleGeneric(new RuntimeException(), request);
        assertEquals("An unexpected error occurred", noMessage.getBody().getDetail());
    }

    private HttpServletRequest request() {
        HttpServletRequest result = mock(HttpServletRequest.class);
        when(result.getRequestURI()).thenReturn("/api/test");
        return result;
    }
}
