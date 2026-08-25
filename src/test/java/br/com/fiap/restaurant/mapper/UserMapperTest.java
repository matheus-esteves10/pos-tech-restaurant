package br.com.fiap.restaurant.mapper;

import br.com.fiap.restaurant.common.audit.Address;
import br.com.fiap.restaurant.dto.request.AddressRequest;
import br.com.fiap.restaurant.dto.request.CreateUserRequest;
import br.com.fiap.restaurant.dto.request.UpdateUserRequest;
import br.com.fiap.restaurant.dto.response.AddressResponse;
import br.com.fiap.restaurant.dto.response.UserResponse;
import br.com.fiap.restaurant.model.User;
import br.com.fiap.restaurant.model.enums.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void testToEntityFromCreateUserRequest() {
        AddressRequest addressRequest = new AddressRequest(
                "Rua Principal", "123", "Centro", "São Paulo", "SP", "01234-567", "Apto 42"
        );
        CreateUserRequest request = new CreateUserRequest(
                "João Silva", "joao@email.com", "joao123", "11999999999",
                "senha123", UserType.CLIENT, addressRequest
        );
        String encodedPassword = "encoded_senha123";

        User user = userMapper.toEntity(request, encodedPassword);

        assertNotNull(user);
        assertEquals("João Silva", user.getName());
        assertEquals("joao@email.com", user.getEmail());
        assertEquals("joao123", user.getLogin());
        assertEquals("11999999999", user.getPhone());
        assertEquals(encodedPassword, user.getPassword());
        assertEquals(UserType.CLIENT, user.getUserType());
        assertNotNull(user.getAddress());
        assertEquals("Rua Principal", user.getAddress().getStreet());
        assertEquals("123", user.getAddress().getNumber());
    }

    @Test
    void testToEntityWithoutAddress() {
        CreateUserRequest request = new CreateUserRequest(
                "Maria Silva", "maria@email.com", "maria456", "11888888888",
                "senha456", UserType.RESTAURANT_OWNER, null
        );
        String encodedPassword = "encoded_senha456";

        User user = userMapper.toEntity(request, encodedPassword);

        assertNotNull(user);
        assertEquals("Maria Silva", user.getName());
        assertNull(user.getAddress());
    }

    @Test
    void testUpdateEntity() {
        User user = User.builder()
                .id(1L)
                .name("João Silva")
                .email("joao@email.com")
                .phone("11999999999")
                .address(Address.builder().street("Rua Velha").build())
                .build();

        AddressRequest addressRequest = new AddressRequest(
                "Rua Nova", "456", "Bairro", "São Paulo", "SP", "02345-678", "Apto 1"
        );
        UpdateUserRequest request = new UpdateUserRequest(
                "João Silva Atualizado", "joao.novo@email.com", "11988888888", addressRequest, null
        );

        userMapper.updateEntity(user, request);

        assertEquals("João Silva Atualizado", user.getName());
        assertEquals("joao.novo@email.com", user.getEmail());
        assertEquals("11988888888", user.getPhone());
        assertNotNull(user.getAddress());
        assertEquals("Rua Nova", user.getAddress().getStreet());
        assertEquals("456", user.getAddress().getNumber());
    }

    @Test
    void testUpdateEntityWithoutAddress() {
        User user = User.builder()
                .id(1L)
                .name("João Silva")
                .email("joao@email.com")
                .build();

        UpdateUserRequest request = new UpdateUserRequest(
                "João Silva Atualizado", "joao.novo@email.com", null, null, null
        );

        userMapper.updateEntity(user, request);

        assertEquals("João Silva Atualizado", user.getName());
        assertEquals("joao.novo@email.com", user.getEmail());
        assertNull(user.getAddress());
    }

    @Test
    void testToResponse() {
        Address address = Address.builder()
                .street("Rua Principal")
                .number("123")
                .neighborhood("Centro")
                .city("São Paulo")
                .state("SP")
                .zipCode("01234-567")
                .complement("Apto 42")
                .build();

        User user = User.builder()
                .id(1L)
                .name("João Silva")
                .email("joao@email.com")
                .login("joao123")
                .phone("11999999999")
                .userType(UserType.CLIENT)
                .enabled(true)
                .address(address)
                .build();

        UserResponse response = userMapper.toResponse(user);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("João Silva", response.name());
        assertEquals("joao@email.com", response.email());
        assertEquals("joao123", response.login());
        assertEquals("11999999999", response.phone());
        assertEquals(UserType.CLIENT, response.userType());
        assertTrue(response.enabled());
        assertNotNull(response.address());
        assertEquals("Rua Principal", response.address().street());
        assertEquals("123", response.address().number());
        assertEquals("01234-567", response.address().zipCode());
    }

    @Test
    void testToResponseWithoutAddress() {
        User user = User.builder()
                .id(2L)
                .name("Maria Silva")
                .email("maria@email.com")
                .login("maria456")
                .userType(UserType.RESTAURANT_OWNER)
                .enabled(true)
                .build();

        UserResponse response = userMapper.toResponse(user);

        assertNotNull(response);
        assertEquals(2L, response.id());
        assertEquals("Maria Silva", response.name());
        assertNull(response.address());
    }

    @Test
    void testToResponseDisabledUser() {
        User user = User.builder()
                .id(3L)
                .name("Pedro Silva")
                .email("pedro@email.com")
                .login("pedro789")
                .userType(UserType.RESTAURANT_EMPLOYEE)
                .enabled(false)
                .build();

        UserResponse response = userMapper.toResponse(user);

        assertNotNull(response);
        assertFalse(response.enabled());
    }
}
