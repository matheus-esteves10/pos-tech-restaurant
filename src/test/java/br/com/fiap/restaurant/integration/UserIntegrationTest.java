package br.com.fiap.restaurant.integration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserIntegrationTest extends IntegrationTestSupport {

    @Test
    void getsAndListsUsersWithoutExposingPasswords() throws Exception {
        Session jane = newUser("jane");
        newUser("john");

        getAs(jane, "/users/" + jane.id())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("jane"))
                .andExpect(jsonPath("$.email").value("jane@mail.com"))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.password").doesNotExist());

        getAs(jane, "/users")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].login", containsInAnyOrder("jane", "john")));

        getAs(jane, "/users/999").andExpect(status().isNotFound());
    }

    @Test
    void updatesOwnProfileMergingAddress() throws Exception {
        Session jane = newUser("jane");
        patchAs(jane, "/users/me", """
                {"address": {"street": "Av. Paulista", "number": "1000", "city": "São Paulo"}}
                """).andExpect(status().isOk());

        patchAs(jane, "/users/me", """
                {"phone": "11911112222", "address": {"complement": "Sala 10"}}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("11911112222"))
                .andExpect(jsonPath("$.address.street").value("Av. Paulista"))
                .andExpect(jsonPath("$.address.number").value("1000"))
                .andExpect(jsonPath("$.address.complement").value("Sala 10"));

        patchAs(jane, "/users/me", """
                {"phone": "123"}
                """).andExpect(status().isBadRequest());
    }

    @Test
    void changesPasswordAndRequiresTheNewOneToLogin() throws Exception {
        Session jane = newUser("jane");

        patchAs(jane, "/users/me/password", """
                {"password": "brand-new-pass"}
                """).andExpect(status().isOk());

        assertThat(login("jane", "brand-new-pass")).isNotBlank();
        postAs(null, "/auth/login", """
                {"login": "jane", "password": "%s"}
                """.formatted(PASSWORD)).andExpect(status().isUnauthorized());
    }

    @Test
    void findsUsersByNameWithPagination() throws Exception {
        Session jane = newUser("jane");

        getAs(jane, "/users/name/USER JANE?page=0&size=5")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].login").value("jane"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        getAs(jane, "/users/name/Nobody").andExpect(status().isNotFound());
    }

    @Test
    void softDeletedUserDisappearsButStaysInDatabase() throws Exception {
        Session jane = newUser("jane");
        Session john = newUser("john");

        deleteAs(john, "/users/me").andExpect(status().isNoContent());

        getAs(jane, "/users").andExpect(jsonPath("$[*].login", containsInAnyOrder("jane")));
        getAs(jane, "/users/" + john.id()).andExpect(status().isNotFound());
        getAs(jane, "/users/name/User john").andExpect(status().isNotFound());
        assertThat(jdbcTemplate.queryForObject(
                "SELECT enabled FROM users WHERE id = ?", Boolean.class, john.id())).isFalse();
    }
}
