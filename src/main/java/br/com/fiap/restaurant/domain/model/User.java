package br.com.fiap.restaurant.domain.model;

import br.com.fiap.restaurant.domain.exception.DomainValidationException;

import static br.com.fiap.restaurant.domain.model.Guard.requireText;

public class User {

    private static final int NAME_MIN_LENGTH = 3;
    private static final int NAME_MAX_LENGTH = 100;

    private final Long id;
    private final String name;
    private String email;
    private String login;
    private String phone;
    private String password;
    private Address address;
    private boolean enabled;

    public User(Long id, String name, String email, String login, String phone, String password,
                Address address, boolean enabled) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.login = login;
        this.phone = phone;
        this.password = password;
        this.address = address;
        this.enabled = enabled;
    }

    public static User create(String name, String email, String login, String phone,
                              String encodedPassword, Address address) {
        validateName(name);
        requireText(email, "Email");
        requireText(login, "Login");
        requireText(encodedPassword, "Password");
        return new User(null, name, email, login, phone, encodedPassword, address, true);
    }

    public void update(String email, String login, String phone, Address addressChanges) {
        if (email != null) {
            this.email = requireText(email, "Email");
        }
        if (login != null) {
            this.login = requireText(login, "Login");
        }
        if (phone != null) {
            this.phone = phone;
        }
        if (addressChanges != null) {
            this.address = address == null ? addressChanges : address.mergedWith(addressChanges);
        }
    }

    public void changePassword(String encodedPassword) {
        this.password = requireText(encodedPassword, "Password");
    }

    public void disable() {
        this.enabled = false;
    }

    private static void validateName(String name) {
        requireText(name, "Name");
        if (name.length() < NAME_MIN_LENGTH || name.length() > NAME_MAX_LENGTH) {
            throw new DomainValidationException(
                    "Name must be between " + NAME_MIN_LENGTH + " and " + NAME_MAX_LENGTH + " characters");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public Address getAddress() {
        return address;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
