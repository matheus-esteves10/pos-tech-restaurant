package br.com.fiap.restaurant.exception;

public class ForbiddenOperationException extends RuntimeException {

    private static final String MESSAGE = "User %s is not allowed to perform this operation";

    public ForbiddenOperationException(String user) {
        super(String.format(MESSAGE, user));
    }
}
