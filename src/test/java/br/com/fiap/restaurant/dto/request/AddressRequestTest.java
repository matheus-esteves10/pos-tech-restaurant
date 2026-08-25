package br.com.fiap.restaurant.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AddressRequestTest {

    @Test
    void testAddressRequestCreation() {
        AddressRequest request = new AddressRequest(
                "Rua Principal", "123", "Centro", "Sao Paulo", "SP", "01234-567", "Apto 42"
        );

        assertNotNull(request);
        assertEquals("Rua Principal", request.street());
        assertEquals("123", request.number());
        assertEquals("Centro", request.neighborhood());
        assertEquals("Sao Paulo", request.city());
        assertEquals("SP", request.state());
        assertEquals("01234-567", request.zipCode());
        assertEquals("Apto 42", request.complement());
    }

    @Test
    void testAddressRequestWithoutComplement() {
        AddressRequest request = new AddressRequest(
                "Rua Secundaria", "456", "Bairro", "Rio de Janeiro", "RJ", "20000-000", null
        );

        assertNotNull(request);
        assertNull(request.complement());
    }

    @Test
    void testAddressRequestWithMinimalData() {
        AddressRequest request = new AddressRequest(
                "Rua", "1", "Bairro", "Cidade", "UF", "00000-000", null
        );

        assertNotNull(request);
        assertEquals("Rua", request.street());
    }
}
