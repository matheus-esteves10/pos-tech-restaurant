package br.com.fiap.restaurant.exception;

public class EntityNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Entity not found";

    public EntityNotFoundException() {
        super(MESSAGE);
    }
}
