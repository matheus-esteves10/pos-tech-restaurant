package br.com.fiap.restaurant.domain.model;

public record Address(String street,
                      String number,
                      String neighborhood,
                      String city,
                      String state,
                      String zipCode,
                      String complement) {

    public void ensureComplete() {
        Guard.requireText(street, "Address street");
        Guard.requireText(number, "Address number");
        Guard.requireText(city, "Address city");
        Guard.requireText(state, "Address state");
        Guard.requireText(zipCode, "Address zip code");
    }

    public Address mergedWith(Address changes) {
        if (changes == null) {
            return this;
        }
        return new Address(
                pick(changes.street, street),
                pick(changes.number, number),
                pick(changes.neighborhood, neighborhood),
                pick(changes.city, city),
                pick(changes.state, state),
                pick(changes.zipCode, zipCode),
                pick(changes.complement, complement)
        );
    }

    private static String pick(String newValue, String currentValue) {
        return newValue != null ? newValue : currentValue;
    }
}
