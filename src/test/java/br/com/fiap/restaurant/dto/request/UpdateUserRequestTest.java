package br.com.fiap.restaurant.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UpdateUserRequestTest {

    @Test
    void testUpdateUserRequestCreation() {
        AddressRequest addressRequest = new AddressRequest(
                "Rua Nova", "456", "Bairro", "Sao Paulo", "SP", "02345-678", "Apto 1"
        );
        UpdateUserRequest request = new UpdateUserRequest(
                "Joao Silva Updated", "joao.novo@email.com", "11988888888", addressRequest, "novaSenha123"
        );

        assertNotNull(request);
        assertEquals("Joao Silva Updated", request.name());
        assertEquals("joao.novo@email.com", request.email());
        assertEquals("11988888888", request.phone());
        assertNotNull(request.address());
        assertEquals("novaSenha123", request.password());
    }

    @Test
    void testUpdateUserRequestWithoutPassword() {
        UpdateUserRequest request = new UpdateUserRequest(
                "Joao Silva Updated", "joao.novo@email.com", "11988888888", null, null
        );

        assertNotNull(request);
        assertNull(request.password());
    }

    @Test
    void testUpdateUserRequestWithoutPhone() {
        UpdateUserRequest request = new UpdateUserRequest(
                "Joao Silva Updated", "joao.novo@email.com", null, null, null
        );

        assertNotNull(request);
        assertNull(request.phone());
    }

    @Test
    void testUpdateUserRequestWithoutAddress() {
        UpdateUserRequest request = new UpdateUserRequest(
                "Joao Silva Updated", "joao.novo@email.com", "11988888888", null, null
        );

        assertNotNull(request);
        assertNull(request.address());
    }
}
