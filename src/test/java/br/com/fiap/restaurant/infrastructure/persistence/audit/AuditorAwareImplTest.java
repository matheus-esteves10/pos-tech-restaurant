package br.com.fiap.restaurant.infrastructure.persistence.audit;

import br.com.fiap.restaurant.infrastructure.security.AuthenticatedUser;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuditorAwareImplTest {
    private final AuditorAwareImpl auditor = new AuditorAwareImpl();

    @AfterEach void clearContext() { SecurityContextHolder.clearContext(); }

    @Test void returnsEmptyWithoutAuthenticatedPrincipal() {
        assertEquals(Optional.empty(), auditor.getCurrentAuditor());
        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.unauthenticated("jane", null));
        assertEquals(Optional.empty(), auditor.getCurrentAuditor());
    }

    @Test void resolvesUserDetailsAndNamedPrincipal() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new AuthenticatedUser(Fixtures.user(1L, "user-login")), null, List.of()));
        assertEquals(Optional.of("user-login"), auditor.getCurrentAuditor());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("plain-login", null, List.of()));
        assertEquals(Optional.of("plain-login"), auditor.getCurrentAuditor());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("anonymousUser", null, List.of()));
        assertEquals(Optional.empty(), auditor.getCurrentAuditor());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(42, null, List.of()));
        assertEquals(Optional.empty(), auditor.getCurrentAuditor());
    }
}
