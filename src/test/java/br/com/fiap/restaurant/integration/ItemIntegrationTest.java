package br.com.fiap.restaurant.integration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ItemIntegrationTest extends IntegrationTestSupport {

    @Test
    void membersCreateItemsWithAllMenuFields() throws Exception {
        Session owner = newUser("owner");
        Session employee = newUser("employee");
        long restaurantId = createRestaurant(owner, "12345678000199");
        addEmployee(owner, restaurantId, employee.id());

        postAs(employee, "/restaurant/" + restaurantId + "/item", """
                {"name": "Lasanha", "description": "À bolonhesa", "value": 59.90,
                 "imageUrl": "/images/lasanha.png", "itemType": "JUST_IN_PERSON"}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Lasanha"))
                .andExpect(jsonPath("$.description").value("À bolonhesa"))
                .andExpect(jsonPath("$.value").value(59.90))
                .andExpect(jsonPath("$.imageUrl").value("/images/lasanha.png"))
                .andExpect(jsonPath("$.itemType").value("JUST_IN_PERSON"));

        postAs(owner, "/restaurant/" + restaurantId + "/item", """
                {"name": "Refrigerante", "value": 7}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemType").value("DELIVERY_OR_IN_PERSON"));
    }

    @Test
    void rejectsInvalidItemsAndNonMembers() throws Exception {
        Session owner = newUser("owner");
        Session stranger = newUser("stranger");
        long restaurantId = createRestaurant(owner, "12345678000199");
        String items = "/restaurant/" + restaurantId + "/item";

        postAs(owner, items, """
                {"name": "", "value": 0}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.value").value("Value must be greater than zero"));
        postAs(stranger, items, """
                {"name": "Pizza", "value": 10}
                """).andExpect(status().isForbidden());
        postAs(owner, "/restaurant/999/item", """
                {"name": "Pizza", "value": 10}
                """).andExpect(status().isNotFound());
    }

    @Test
    void listsAndGetsMenuItems() throws Exception {
        Session owner = newUser("owner");
        Session customer = newUser("customer");
        long restaurantId = createRestaurant(owner, "12345678000199");
        long lasanha = createItem(owner, restaurantId, "lasanha");
        createItem(owner, restaurantId, "risoto");

        getAs(customer, "/restaurant/" + restaurantId + "/item")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("lasanha", "risoto")));
        getAs(customer, "/restaurant/" + restaurantId + "/item/" + lasanha)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("lasanha"));
        getAs(customer, "/restaurant/" + restaurantId)
                .andExpect(jsonPath("$.items", hasSize(2)));
        getAs(customer, "/restaurant/" + restaurantId + "/item/999").andExpect(status().isNotFound());
    }

    @Test
    void itemsBelongToTheirRestaurant() throws Exception {
        Session owner = newUser("owner");
        long first = createRestaurant(owner, "11111111000111");
        long second = createRestaurant(owner, "22222222000122");
        long item = createItem(owner, first, "lasanha");

        getAs(owner, "/restaurant/" + second + "/item/" + item).andExpect(status().isNotFound());
        patchAs(owner, "/restaurant/" + second + "/item/" + item, """
                {"value": 1}
                """).andExpect(status().isNotFound());
    }

    @Test
    void updatesOnlyProvidedFields() throws Exception {
        Session owner = newUser("owner");
        long restaurantId = createRestaurant(owner, "12345678000199");
        long item = createItem(owner, restaurantId, "lasanha");

        patchAs(owner, "/restaurant/" + restaurantId + "/item/" + item, """
                {"value": 64.90, "itemType": "DELIVERY_OR_IN_PERSON"}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("lasanha"))
                .andExpect(jsonPath("$.value").value(64.90))
                .andExpect(jsonPath("$.itemType").value("DELIVERY_OR_IN_PERSON"));
    }

    @Test
    void softDeletedItemDisappearsFromMenu() throws Exception {
        Session owner = newUser("owner");
        long restaurantId = createRestaurant(owner, "12345678000199");
        long lasanha = createItem(owner, restaurantId, "lasanha");
        createItem(owner, restaurantId, "risoto");
        String lasanhaPath = "/restaurant/" + restaurantId + "/item/" + lasanha;

        deleteAs(owner, lasanhaPath).andExpect(status().isNoContent());

        getAs(owner, lasanhaPath).andExpect(status().isNotFound());
        patchAs(owner, lasanhaPath, """
                {"value": 1}
                """).andExpect(status().isNotFound());
        deleteAs(owner, lasanhaPath).andExpect(status().isNotFound());
        getAs(owner, "/restaurant/" + restaurantId + "/item")
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("risoto")));
        getAs(owner, "/restaurant/" + restaurantId)
                .andExpect(jsonPath("$.items[*].name", containsInAnyOrder("risoto")));
        assertThat(jdbcTemplate.queryForObject(
                "SELECT enabled FROM item WHERE id = ?", Boolean.class, lasanha)).isFalse();
    }
}
