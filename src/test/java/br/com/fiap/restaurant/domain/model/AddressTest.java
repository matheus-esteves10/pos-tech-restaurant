package br.com.fiap.restaurant.domain.model;

import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;

import br.com.fiap.restaurant.domain.exception.DomainValidationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddressTest {

    @Test
    void keepsCurrentValuesForNullChanges() {
        Address current = Fixtures.address();

        assertSame(current, current.mergedWith(null));
        assertEquals(current, current.mergedWith(new Address(null, null, null, null, null, null, null)));
    }

    @Test
    void requiresStreetNumberCityStateAndZipCode() {
        assertDoesNotThrow(() -> new Address("Main", "1", null, "SP", "SP", "01000-000", null).ensureComplete());
        assertThrows(DomainValidationException.class,
                () -> new Address(" ", "1", null, "SP", "SP", "01000-000", null).ensureComplete());
        assertThrows(DomainValidationException.class,
                () -> new Address("Main", null, null, "SP", "SP", "01000-000", null).ensureComplete());
        assertThrows(DomainValidationException.class,
                () -> new Address("Main", "1", null, "SP", null, "01000-000", null).ensureComplete());
        assertThrows(DomainValidationException.class,
                () -> new Address("Main", "1", null, "SP", "SP", "", null).ensureComplete());
    }

    @Test
    void replacesProvidedValues() {
        Address merged = Fixtures.address().mergedWith(
                new Address("Second St", "20", "North", "Campinas", "RJ", "02000-000", "House"));

        assertEquals(new Address("Second St", "20", "North", "Campinas", "RJ", "02000-000", "House"), merged);
    }
}
