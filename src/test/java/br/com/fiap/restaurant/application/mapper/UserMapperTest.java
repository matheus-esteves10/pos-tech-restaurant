package br.com.fiap.restaurant.application.mapper;

import br.com.fiap.restaurant.application.dto.request.AddressRequest;
import br.com.fiap.restaurant.application.dto.request.CreateUserRequest;
import br.com.fiap.restaurant.application.dto.request.UpdateUserRequest;
import br.com.fiap.restaurant.domain.entity.User;
import br.com.fiap.restaurant.domain.valueobject.Address;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    private final UserMapper mapper = new UserMapper();

    @Test void mapsUserWithAndWithoutOptionalFields() {
        var request = new CreateUserRequest("Jane", "jane@example.com", "jane", "12345678901", "password123", address("Main", "A"));
        User user = mapper.toEntity(request, "encoded");
        assertEquals("encoded", user.getPassword());
        assertEquals("12345678901", user.getPhone());
        assertEquals("Main", user.getAddress().getStreet());
        User minimal = mapper.toEntity(new CreateUserRequest("Jane", "j@example.com", "j", null, "password123", null), "hash");
        assertNull(minimal.getAddress());
        assertNull(minimal.getPhone());
    }

    @Test void updatesFieldsAndMergesPartialAddress() {
        User user = User.builder().email("old@example.com").login("old").phone("11111111111")
                .address(Address.builder().street("Old street").city("Old city").state("SP").build()).build();
        mapper.updateEntity(user, new UpdateUserRequest("new@example.com", "new", "22222222222", address("New street", null)));
        assertEquals("new@example.com", user.getEmail());
        assertEquals("new", user.getLogin());
        assertEquals("22222222222", user.getPhone());
        assertEquals("New street", user.getAddress().getStreet());
        assertEquals("Old city", user.getAddress().getCity());
        User noAddress = User.builder().build();
        mapper.updateEntity(noAddress, new UpdateUserRequest(null, null, null, address("New", null)));
        assertEquals("New", noAddress.getAddress().getStreet());
    }

    @Test void mapsResponseWithAndWithoutAddress() {
        User user = User.builder().id(5L).name("Jane").email("jane@example.com").login("jane").enabled(false)
                .address(Address.builder().street("Main").build()).build();
        var response = mapper.toResponse(user);
        assertEquals(5L, response.id());
        assertEquals("Main", response.address().street());
        assertFalse(response.enabled());
        assertNull(mapper.toResponse(User.builder().enabled(true).build()).address());
    }

    private AddressRequest address(String street, String city) {
        return new AddressRequest(street, "1", "Center", city, "SP", "01000000", null);
    }
}
