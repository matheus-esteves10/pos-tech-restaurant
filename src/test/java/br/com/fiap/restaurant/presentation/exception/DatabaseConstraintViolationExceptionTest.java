package br.com.fiap.restaurant.presentation.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseConstraintViolationExceptionTest {
    @Test void mapsCommonUniqueConstraintMessages() {
        assertEquals("Email already in use", mapped("duplicate key email"));
        assertEquals("Login already in use", mapped("unique login"));
        assertEquals("Phone already in use", mapped("constraint phone"));
        assertEquals("Duplicate resource", mapped("duplicate cnpj"));
    }

    @Test void mapsOtherDatabaseErrors() {
        assertEquals("Database integrity error", mapped("foreign key error"));
        assertEquals("Database constraint violation", new DatabaseConstraintViolationException(new RuntimeException()).getMessage());
    }

    private String mapped(String message) {
        return new DatabaseConstraintViolationException(new DataIntegrityViolationException("failure", new RuntimeException(message))).getMessage();
    }
}
