package br.com.fiap.restaurant.domain.model;

import br.com.fiap.restaurant.domain.enums.ItemType;
import br.com.fiap.restaurant.domain.exception.DomainValidationException;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void createsItemWithDefaultType() {
        Item item = Item.create(5L, "Burger", "Tasty", BigDecimal.TEN, "/img.png", null);

        assertNull(item.getId());
        assertEquals(5L, item.getRestaurantId());
        assertEquals("Burger", item.getName());
        assertEquals("Tasty", item.getDescription());
        assertEquals(BigDecimal.TEN, item.getValue());
        assertEquals("/img.png", item.getImageUrl());
        assertEquals(ItemType.DELIVERY_OR_IN_PERSON, item.getItemType());
        assertTrue(item.isEnabled());
        assertEquals(ItemType.JUST_IN_PERSON,
                Item.create(5L, "Soup", null, BigDecimal.ONE, null, ItemType.JUST_IN_PERSON).getItemType());
    }

    @Test
    void rejectsInvalidValues() {
        assertThrows(DomainValidationException.class, () -> Item.create(null, "Burger", null, BigDecimal.TEN, null, null));
        assertThrows(DomainValidationException.class, () -> Item.create(5L, " ", null, BigDecimal.TEN, null, null));
        assertThrows(DomainValidationException.class, () -> Item.create(5L, "Burger", null, null, null, null));
        assertThrows(DomainValidationException.class, () -> Item.create(5L, "Burger", null, BigDecimal.ZERO, null, null));
    }

    @Test
    void updatesProvidedFieldsAndDisables() {
        Item item = Fixtures.item(1L, 5L);
        item.update(null, null, null, null, null);
        assertEquals("Burger", item.getName());

        item.update("Pizza", "Cheesy", BigDecimal.ONE, "/pizza.png", ItemType.JUST_DELIVERY);
        item.disable();

        assertEquals("Pizza", item.getName());
        assertEquals("Cheesy", item.getDescription());
        assertEquals(BigDecimal.ONE, item.getValue());
        assertEquals("/pizza.png", item.getImageUrl());
        assertEquals(ItemType.JUST_DELIVERY, item.getItemType());
        assertFalse(item.isEnabled());
        assertThrows(DomainValidationException.class, () -> item.update(null, null, new BigDecimal("-1"), null, null));
        assertThrows(DomainValidationException.class, () -> item.update("", null, null, null, null));
    }
}
