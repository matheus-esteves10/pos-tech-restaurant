package br.com.fiap.restaurant.domain.model;

import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class AddressTest {

    @Test
    void keepsCurrentValuesForNullChanges() {
        Address current = Fixtures.address();

        assertSame(current, current.mergedWith(null));
        assertEquals(current, current.mergedWith(new Address(null, null, null, null, null, null, null)));
    }

    @Test
    void replacesProvidedValues() {
        Address merged = Fixtures.address().mergedWith(
                new Address("Second St", "20", "North", "Campinas", "RJ", "02000-000", "House"));

        assertEquals(new Address("Second St", "20", "North", "Campinas", "RJ", "02000-000", "House"), merged);
    }
}
