package br.com.fiap.restaurant.domain.model;

import br.com.fiap.restaurant.domain.exception.DomainValidationException;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void createsEnabledUserWithoutId() {
        User user = User.create("Jane Doe", "jane@mail.com", "jane", "11999999999", "hash", Fixtures.address());

        assertNull(user.getId());
        assertTrue(user.isEnabled());
        assertEquals("Jane Doe", user.getName());
        assertEquals("jane@mail.com", user.getEmail());
        assertEquals("jane", user.getLogin());
        assertEquals("11999999999", user.getPhone());
        assertEquals("hash", user.getPassword());
        assertEquals(Fixtures.address(), user.getAddress());
    }

    @Test
    void rejectsInvalidRequiredFields() {
        assertThrows(DomainValidationException.class, () -> User.create(" ", "a@b.com", "jane", null, "hash", null));
        assertThrows(DomainValidationException.class, () -> User.create("Jo", "a@b.com", "jane", null, "hash", null));
        assertThrows(DomainValidationException.class, () -> User.create("J".repeat(101), "a@b.com", "jane", null, "hash", null));
        assertThrows(DomainValidationException.class, () -> User.create("Jane", null, "jane", null, "hash", null));
        assertThrows(DomainValidationException.class, () -> User.create("Jane", "a@b.com", "", null, "hash", null));
        assertThrows(DomainValidationException.class, () -> User.create("Jane", "a@b.com", "jane", null, null, null));
    }

    @Test
    void updatesOnlyProvidedFieldsAndMergesAddress() {
        User user = Fixtures.user(1L, "jane");

        user.update(null, null, null, null);
        assertEquals("jane@mail.com", user.getEmail());

        user.update("new@mail.com", "jane2", "11911112222",
                new Address(null, "99", null, null, null, null, null));

        assertEquals("new@mail.com", user.getEmail());
        assertEquals("jane2", user.getLogin());
        assertEquals("11911112222", user.getPhone());
        assertEquals("99", user.getAddress().number());
        assertEquals("Main St", user.getAddress().street());
    }

    @Test
    void setsAddressWhenUserHadNone() {
        User user = new User(1L, "Jane", "jane@mail.com", "jane", null, "hash", null, true);
        Address address = Fixtures.address();

        user.update(null, null, null, address);

        assertEquals(address, user.getAddress());
    }

    @Test
    void rejectsBlankEmailOrLoginOnUpdate() {
        User user = Fixtures.user(1L, "jane");
        assertThrows(DomainValidationException.class, () -> user.update(" ", null, null, null));
        assertThrows(DomainValidationException.class, () -> user.update(null, "", null, null));
    }

    @Test
    void changesPasswordAndDisables() {
        User user = Fixtures.user(1L, "jane");

        user.changePassword("new-hash");
        user.disable();

        assertEquals("new-hash", user.getPassword());
        assertFalse(user.isEnabled());
        assertThrows(DomainValidationException.class, () -> user.changePassword(" "));
    }
}
