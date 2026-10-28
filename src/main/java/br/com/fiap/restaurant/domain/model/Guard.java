package br.com.fiap.restaurant.domain.model;

import br.com.fiap.restaurant.domain.exception.DomainValidationException;

import java.math.BigDecimal;
import java.util.Collection;

final class Guard {

    private Guard() {
    }

    static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(field + " cannot be blank");
        }
        return value;
    }

    static <T> T requireNonNull(T value, String field) {
        if (value == null) {
            throw new DomainValidationException(field + " cannot be null");
        }
        return value;
    }

    static BigDecimal requirePositive(BigDecimal value, String field) {
        requireNonNull(value, field);
        if (value.signum() <= 0) {
            throw new DomainValidationException(field + " must be greater than zero");
        }
        return value;
    }

    static <C extends Collection<?>> C requireNotEmpty(C values, String field) {
        if (values == null || values.isEmpty()) {
            throw new DomainValidationException(field + " cannot be empty");
        }
        return values;
    }
}
