package br.com.fiap.restaurant.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AddressResponseTest {

    @Test
    void testAddressResponseCreation() {
        AddressResponse response = new AddressResponse(
                "Rua Principal", "123", "Centro", "Sao Paulo", "SP", "01234-567", "Apto 42"
        );

        assertNotNull(response);
        assertEquals("Rua Principal", response.street());
        assertEquals("123", response.number());
        assertEquals("Centro", response.neighborhood());
        assertEquals("Sao Paulo", response.city());
        assertEquals("SP", response.state());
        assertEquals("01234-567", response.zipCode());
        assertEquals("Apto 42", response.complement());
    }

    @Test
    void testAddressResponseWithoutComplement() {
        AddressResponse response = new AddressResponse(
                "Rua Secundaria", "456", "Bairro", "Rio de Janeiro", "RJ", "20000-000", null
        );

        assertNotNull(response);
        assertNull(response.complement());
    }

    @Test
    void testAddressResponseWithMinimalData() {
        AddressResponse response = new AddressResponse(
                "Rua", "1", "Bairro", "Cidade", "UF", "00000-000", null
        );

        assertNotNull(response);
        assertEquals("Rua", response.street());
    }
}
