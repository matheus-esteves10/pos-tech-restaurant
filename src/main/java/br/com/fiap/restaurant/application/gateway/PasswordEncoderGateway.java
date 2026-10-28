package br.com.fiap.restaurant.application.gateway;

public interface PasswordEncoderGateway {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
