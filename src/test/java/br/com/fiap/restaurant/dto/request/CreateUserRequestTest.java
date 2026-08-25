package br.com.fiap.restaurant.dto.request;

import br.com.fiap.restaurant.model.enums.UserType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CreateUserRequestTest {

    @Test
    void testCreateUserRequestCreation() {
        AddressRequest addressRequest = new AddressRequest(
                "Rua Principal", "123", "Centro", "Sao Paulo", "SP", "01234-567", "Apto 42"
        );
        CreateUserRequest request = new CreateUserRequest(
                "Joao Silva", "joao@email.com", "joao123", "11999999999",
                "senha123", UserType.CLIENT, addressRequest
        );

        assertNotNull(request);
        assertEquals("Joao Silva", request.name());
        assertEquals("joao@email.com", request.email());
        assertEquals("joao123", request.login());
        assertEquals("11999999999", request.phone());
        assertEquals("senha123", request.password());
        assertEquals(UserType.CLIENT, request.userType());
        assertNotNull(request.address());
    }

    @Test
    void testCreateUserRequestWithoutAddress() {
        CreateUserRequest request = new CreateUserRequest(
                "Maria Silva", "maria@email.com", "maria456", "11888888888",
                "senha456", UserType.RESTAURANT_OWNER, null
        );

        assertNotNull(request);
        assertNull(request.address());
    }

    @Test
    void testCreateUserRequestWithMinimalData() {
        CreateUserRequest request = new CreateUserRequest(
                "Test User", "test@email.com", "testuser", null,
                "password", UserType.CLIENT, null
        );

        assertNotNull(request);
        assertNull(request.phone());
    }
}
