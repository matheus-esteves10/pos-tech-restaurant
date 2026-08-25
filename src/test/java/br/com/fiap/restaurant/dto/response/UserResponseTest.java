package br.com.fiap.restaurant.dto.response;

import br.com.fiap.restaurant.model.enums.UserType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserResponseTest {

    @Test
    void testUserResponseCreation() {
        AddressResponse addressResponse = new AddressResponse(
                "Rua Principal", "123", "Centro", "Sao Paulo", "SP", "01234-567", "Apto 42"
        );
        UserResponse response = new UserResponse(
                1L, "Joao Silva", "joao@email.com", "joao123", "11999999999",
                UserType.CLIENT, true, addressResponse
        );

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Joao Silva", response.name());
        assertEquals("joao@email.com", response.email());
        assertEquals("joao123", response.login());
        assertEquals("11999999999", response.phone());
        assertEquals(UserType.CLIENT, response.userType());
        assertEquals(true, response.enabled());
        assertNotNull(response.address());
    }

    @Test
    void testUserResponseWithoutAddress() {
        UserResponse response = new UserResponse(
                2L, "Maria Silva", "maria@email.com", "maria456", null,
                UserType.RESTAURANT_OWNER, true, null
        );

        assertNotNull(response);
        assertNull(response.address());
        assertNull(response.phone());
    }

    @Test
    void testUserResponseDisabled() {
        UserResponse response = new UserResponse(
                3L, "Pedro Silva", "pedro@email.com", "pedro789", "11987654321",
                UserType.RESTAURANT_EMPLOYEE, false, null
        );

        assertNotNull(response);
        assertEquals(false, response.enabled());
    }
}
