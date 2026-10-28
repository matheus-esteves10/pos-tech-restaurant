package br.com.fiap.restaurant.integration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RestaurantIntegrationTest extends IntegrationTestSupport {

    @Test
    void createsRestaurantWithAuthenticatedUserAsOwner() throws Exception {
        Session owner = newUser("owner");

        postAs(owner, "/restaurant", restaurantJson("12345678000199"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Cantina Bella Vista"))
                .andExpect(jsonPath("$.kitchenType").value("ITALIAN_FOOD"))
                .andExpect(jsonPath("$.openingAt").value("11:00:00"))
                .andExpect(jsonPath("$.closingAt").value("23:00:00"))
                .andExpect(jsonPath("$.address.city").value("São Paulo"))
                .andExpect(jsonPath("$.owners", hasSize(1)))
                .andExpect(jsonPath("$.owners[0].id").value(owner.id()))
                .andExpect(jsonPath("$.owners[0].login").value("owner"))
                .andExpect(jsonPath("$.items", hasSize(0)));

        assertThat(jdbcTemplate.queryForObject(
                "SELECT created_by FROM restaurant WHERE cnpj = '12345678000199'", String.class)).isEqualTo("owner");
        assertThat(jdbcTemplate.queryForObject(
                "SELECT user_type FROM restaurant_user WHERE user_id = ?", String.class, owner.id()))
                .isEqualTo("RESTAURANT_OWNER");
    }

    @Test
    void validatesRequiredFieldsAndUniqueCnpj() throws Exception {
        Session owner = newUser("owner");

        postAs(owner, "/restaurant", """
                {"name": "", "cnpj": "1", "openingAt": "11:00:00", "closingAt": "23:00:00"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.kitchenType").value("Kitchen type cannot be null"))
                .andExpect(jsonPath("$.validationErrors.address").value("Address cannot be null"));

        postAs(owner, "/restaurant", """
                {"name": "Cafe", "cnpj": "1", "kitchenType": "FAST_FOOD", "openingAt": "11:00:00",
                 "closingAt": "23:00:00", "address": {"street": "Rua A", "number": "1", "state": "SP", "zipCode": "01000000"}}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Address city cannot be blank"));

        createRestaurant(owner, "12345678000199");
        postAs(owner, "/restaurant", restaurantJson("12345678000199")).andExpect(status().isConflict());
    }

    @Test
    void getsAndListsRestaurants() throws Exception {
        Session owner = newUser("owner");
        Session customer = newUser("customer");
        long first = createRestaurant(owner, "11111111000111");
        createRestaurant(owner, "22222222000122");

        getAs(customer, "/restaurant/" + first)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cnpj").value("11111111000111"));
        getAs(customer, "/restaurant")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].cnpj", containsInAnyOrder("11111111000111", "22222222000122")));
        getAs(customer, "/restaurant/999").andExpect(status().isNotFound());
    }

    @Test
    void onlyOwnerUpdatesRestaurantAndAddressIsMerged() throws Exception {
        Session owner = newUser("owner");
        Session employee = newUser("employee");
        long restaurantId = createRestaurant(owner, "12345678000199");
        addEmployee(owner, restaurantId, employee.id());

        patchAs(employee, "/restaurant/" + restaurantId, """
                {"name": "Hacked"}
                """).andExpect(status().isForbidden());

        patchAs(owner, "/restaurant/" + restaurantId, """
                {"name": "Cantina Nova", "closingAt": "23:30:00", "address": {"complement": "Loja 2"}}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cantina Nova"))
                .andExpect(jsonPath("$.closingAt").value("23:30:00"))
                .andExpect(jsonPath("$.openingAt").value("11:00:00"))
                .andExpect(jsonPath("$.address.street").value("Rua das Palmeiras"))
                .andExpect(jsonPath("$.address.complement").value("Loja 2"));
    }

    @Test
    void managesEmployeesAndOwnership() throws Exception {
        Session owner = newUser("owner");
        Session employee = newUser("employee");
        Session stranger = newUser("stranger");
        long restaurantId = createRestaurant(owner, "12345678000199");
        String employees = "/restaurant/" + restaurantId + "/employee";

        postAs(owner, employees + "/" + employee.id(), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantName").value("Cantina Bella Vista"))
                .andExpect(jsonPath("$.userLogin").value("employee"))
                .andExpect(jsonPath("$.userType").value("RESTAURANT_EMPLOYEE"));
        postAs(owner, employees + "/" + employee.id(), null).andExpect(status().isConflict());
        postAs(employee, employees + "/" + stranger.id(), null).andExpect(status().isForbidden());
        postAs(owner, employees + "/999", null).andExpect(status().isNotFound());

        getAs(employee, employees)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].login", containsInAnyOrder("owner", "employee")));
        getAs(stranger, employees).andExpect(status().isForbidden());

        patchAs(owner, "/restaurant/" + restaurantId + "/owner/" + employee.id(), null).andExpect(status().isOk());
        getAs(stranger, "/restaurant/" + restaurantId)
                .andExpect(jsonPath("$.owners[*].login", containsInAnyOrder("owner", "employee")));
        patchAs(owner, "/restaurant/" + restaurantId + "/owner/" + stranger.id(), null)
                .andExpect(status().isNotFound());

        deleteAs(employee, employees + "/" + owner.id())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userType").doesNotExist());
        getAs(employee, employees).andExpect(jsonPath("$[*].login", containsInAnyOrder("employee")));
    }

    @Test
    void onlyOwnerSoftDeletesRestaurant() throws Exception {
        Session owner = newUser("owner");
        Session employee = newUser("employee");
        long restaurantId = createRestaurant(owner, "12345678000199");
        addEmployee(owner, restaurantId, employee.id());
        createItem(owner, restaurantId, "lasanha");

        deleteAs(employee, "/restaurant/" + restaurantId).andExpect(status().isForbidden());
        deleteAs(owner, "/restaurant/" + restaurantId).andExpect(status().isNoContent());

        getAs(owner, "/restaurant/" + restaurantId).andExpect(status().isNotFound());
        getAs(owner, "/restaurant").andExpect(jsonPath("$", hasSize(0)));
        getAs(owner, "/restaurant/" + restaurantId + "/item").andExpect(status().isNotFound());
        getAs(owner, "/restaurant/" + restaurantId + "/employee").andExpect(status().isNotFound());
        deleteAs(owner, "/restaurant/" + restaurantId).andExpect(status().isNotFound());
        assertThat(jdbcTemplate.queryForObject(
                "SELECT enabled FROM restaurant WHERE id = ?", Boolean.class, restaurantId)).isFalse();
    }
}
