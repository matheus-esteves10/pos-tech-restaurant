package br.com.fiap.restaurant.integration;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderIntegrationTest extends IntegrationTestSupport {

    @Test
    void customerPlacesOrderWithRestaurantItems() throws Exception {
        Session owner = newUser("owner");
        Session customer = newUser("customer");
        long restaurantId = createRestaurant(owner, "12345678000199");
        long lasanha = createItem(owner, restaurantId, "lasanha");
        long risoto = createItem(owner, restaurantId, "risoto");

        postAs(customer, "/restaurant/" + restaurantId + "/order", """
                {"itemIds": [%d, %d]}
                """.formatted(lasanha, risoto))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.restaurantId").value(restaurantId))
                .andExpect(jsonPath("$.userId").value(customer.id()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.items[*].name", containsInAnyOrder("lasanha", "risoto")));
    }

    @Test
    void rejectsInvalidOrders() throws Exception {
        Session owner = newUser("owner");
        Session customer = newUser("customer");
        long restaurantId = createRestaurant(owner, "11111111000111");
        long otherRestaurant = createRestaurant(owner, "22222222000122");
        long foreignItem = createItem(owner, otherRestaurant, "lasanha");
        long deletedItem = createItem(owner, restaurantId, "risoto");
        deleteAs(owner, "/restaurant/" + restaurantId + "/item/" + deletedItem).andExpect(status().isNoContent());
        String orders = "/restaurant/" + restaurantId + "/order";

        postAs(customer, orders, """
                {"itemIds": []}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.itemIds").value("Items cannot be empty"));
        postAs(customer, orders, "{\"itemIds\": [%d]}".formatted(foreignItem)).andExpect(status().isNotFound());
        postAs(customer, orders, "{\"itemIds\": [%d]}".formatted(deletedItem)).andExpect(status().isNotFound());
        postAs(customer, "/restaurant/999/order", "{\"itemIds\": [%d]}".formatted(foreignItem))
                .andExpect(status().isNotFound());
    }

    @Test
    void onlyMembersListAndManageOrders() throws Exception {
        Session owner = newUser("owner");
        Session employee = newUser("employee");
        Session customer = newUser("customer");
        long restaurantId = createRestaurant(owner, "12345678000199");
        addEmployee(owner, restaurantId, employee.id());
        long orderId = placeOrder(customer, restaurantId, createItem(owner, restaurantId, "lasanha"));
        String orders = "/restaurant/" + restaurantId + "/order";

        getAs(customer, orders).andExpect(status().isForbidden());
        patchAs(customer, orders + "/" + orderId + "/deliver", null).andExpect(status().isForbidden());
        patchAs(customer, orders + "/" + orderId + "/cancel", null).andExpect(status().isForbidden());

        getAs(employee, orders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(orderId));
        patchAs(employee, orders + "/999/deliver", null).andExpect(status().isNotFound());
    }

    @Test
    void followsStatusLifecycle() throws Exception {
        Session owner = newUser("owner");
        Session customer = newUser("customer");
        long restaurantId = createRestaurant(owner, "12345678000199");
        long item = createItem(owner, restaurantId, "lasanha");
        long delivered = placeOrder(customer, restaurantId, item);
        long canceled = placeOrder(customer, restaurantId, item);
        String orders = "/restaurant/" + restaurantId + "/order/";

        patchAs(owner, orders + delivered + "/deliver", null).andExpect(status().isNoContent());
        patchAs(owner, orders + canceled + "/cancel", null).andExpect(status().isNoContent());

        patchAs(owner, orders + delivered + "/cancel", null)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Order already delivered"));
        patchAs(owner, orders + canceled + "/deliver", null)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Order already canceled"));

        getAs(owner, "/restaurant/" + restaurantId + "/order")
                .andExpect(jsonPath("$[*].status", containsInAnyOrder("DELIVERED", "CANCELED")));
    }

    @Test
    void ordersFromAnotherRestaurantAreNotReachable() throws Exception {
        Session owner = newUser("owner");
        Session customer = newUser("customer");
        long first = createRestaurant(owner, "11111111000111");
        long second = createRestaurant(owner, "22222222000122");
        long orderId = placeOrder(customer, first, createItem(owner, first, "lasanha"));

        patchAs(owner, "/restaurant/" + second + "/order/" + orderId + "/deliver", null)
                .andExpect(status().isNotFound());
        getAs(owner, "/restaurant/" + second + "/order").andExpect(jsonPath("$", hasSize(0)));
    }

    private long placeOrder(Session customer, long restaurantId, long itemId) throws Exception {
        MvcResult result = postAs(customer, "/restaurant/" + restaurantId + "/order",
                "{\"itemIds\": [%d]}".formatted(itemId))
                .andExpect(status().isCreated())
                .andReturn();
        return idOf(result);
    }
}
