package br.com.fiap.restaurant.exception;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;

public class DatabaseConstraintViolationException extends DataIntegrityViolationException {

    public DatabaseConstraintViolationException(Throwable cause) {
        super(mapMessage(cause), cause);
    }

    private static String mapMessage(Throwable cause) {
        Throwable root = NestedExceptionUtils.getMostSpecificCause(cause);
        String rootMsg = root.getMessage() != null ? root.getMessage().toLowerCase() : "";

        if (!rootMsg.isEmpty()) {
            if (rootMsg.contains("unique") || rootMsg.contains("duplicate") || rootMsg.contains("constraint")) {
                if (rootMsg.contains("email")) {
                    return "Email already in use";
                } else if (rootMsg.contains("login")) {
                    return "Login already in use";
                } else if (rootMsg.contains("phone")) {
                    return "Phone already in use";
                } else {
                    return "Duplicate resource";
                }
            }
            return "Database integrity error";
        }

        return "Database constraint violation";
    }
}


