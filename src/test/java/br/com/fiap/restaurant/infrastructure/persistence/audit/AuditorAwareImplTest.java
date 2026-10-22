package br.com.fiap.restaurant.infrastructure.persistence.audit;

import br.com.fiap.restaurant.domain.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuditorAwareImplTest {
    private final AuditorAwareImpl auditor = new AuditorAwareImpl();

    @AfterEach void clearContext() { SecurityContextHolder.clearContext(); }

    @Test void returnsEmptyWithoutAuthenticatedPrincipal() {
        assertEquals(java.util.Optional.empty(), auditor.getCurrentAuditor());
        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.unauthenticated("jane", null));
        assertEquals(java.util.Optional.empty(), auditor.getCurrentAuditor());
    }

    @Test void resolvesUserUserDetailsAndNamedPrincipal() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(User.builder().login("user-login").build(), null, java.util.List.of()));
        assertEquals(java.util.Optional.of("user-login"), auditor.getCurrentAuditor());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("plain-login", null, java.util.List.of()));
        assertEquals(java.util.Optional.of("plain-login"), auditor.getCurrentAuditor());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("anonymousUser", null, java.util.List.of()));
        assertEquals(java.util.Optional.empty(), auditor.getCurrentAuditor());
    }
}
