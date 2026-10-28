package br.com.fiap.restaurant.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageResultTest {

    @Test
    void mapsContentKeepingPaginationMetadata() {
        PageResult<Integer> page = new PageResult<>(List.of(1, 2), 0, 10, 2, 1);

        PageResult<String> mapped = page.map(String::valueOf);

        assertEquals(List.of("1", "2"), mapped.content());
        assertEquals(0, mapped.page());
        assertEquals(10, mapped.size());
        assertEquals(2, mapped.totalElements());
        assertEquals(1, mapped.totalPages());
        assertFalse(mapped.isEmpty());
        assertTrue(new PageResult<>(List.of(), 0, 10, 0, 0).isEmpty());
    }
}
