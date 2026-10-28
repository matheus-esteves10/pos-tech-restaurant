package br.com.fiap.restaurant.presentation.controller;

import br.com.fiap.restaurant.application.usecase.restaurant.AddRestaurantEmployeeUseCase;
import br.com.fiap.restaurant.application.usecase.restaurant.CreateRestaurantCommand;
import br.com.fiap.restaurant.application.usecase.restaurant.CreateRestaurantUseCase;
import br.com.fiap.restaurant.application.usecase.restaurant.DeleteRestaurantUseCase;
import br.com.fiap.restaurant.application.usecase.restaurant.GetRestaurantUseCase;
import br.com.fiap.restaurant.application.usecase.restaurant.ListRestaurantEmployeesUseCase;
import br.com.fiap.restaurant.application.usecase.restaurant.ListRestaurantsUseCase;
import br.com.fiap.restaurant.application.usecase.restaurant.RemoveRestaurantEmployeeUseCase;
import br.com.fiap.restaurant.application.usecase.restaurant.RestaurantDetails;
import br.com.fiap.restaurant.application.usecase.restaurant.RestaurantMembership;
import br.com.fiap.restaurant.application.usecase.restaurant.SetRestaurantOwnerUseCase;
import br.com.fiap.restaurant.application.usecase.restaurant.UpdateRestaurantCommand;
import br.com.fiap.restaurant.application.usecase.restaurant.UpdateRestaurantUseCase;
import br.com.fiap.restaurant.domain.enums.KitchenType;
import br.com.fiap.restaurant.domain.enums.UserType;
import br.com.fiap.restaurant.domain.model.Restaurant;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.presentation.dto.request.CreateRestaurantRequest;
import br.com.fiap.restaurant.presentation.dto.request.UpdateRestaurantRequest;
import br.com.fiap.restaurant.presentation.dto.response.RestaurantResponse;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantControllerTest {

    @Mock ListRestaurantsUseCase listRestaurantsUseCase;
    @Mock GetRestaurantUseCase getRestaurantUseCase;
    @Mock ListRestaurantEmployeesUseCase listRestaurantEmployeesUseCase;
    @Mock CreateRestaurantUseCase createRestaurantUseCase;
    @Mock UpdateRestaurantUseCase updateRestaurantUseCase;
    @Mock AddRestaurantEmployeeUseCase addRestaurantEmployeeUseCase;
    @Mock RemoveRestaurantEmployeeUseCase removeRestaurantEmployeeUseCase;
    @Mock SetRestaurantOwnerUseCase setRestaurantOwnerUseCase;
    @Mock DeleteRestaurantUseCase deleteRestaurantUseCase;
    @InjectMocks RestaurantController controller;

    private final User owner = Fixtures.user(1L, "owner");
    private final User employee = Fixtures.user(2L, "employee");
    private final Restaurant restaurant = Fixtures.restaurant(10L, owner, employee);
    private final RestaurantDetails details = new RestaurantDetails(restaurant, List.of(Fixtures.item(3L, 10L)));

    @Test
    void listsAndGetsRestaurants() {
        when(listRestaurantsUseCase.execute()).thenReturn(List.of(details));
        when(getRestaurantUseCase.execute(10L)).thenReturn(details);

        assertEquals(1, controller.getRestaurants().getBody().size());
        RestaurantResponse response = controller.getRestaurant(10L).getBody();
        assertEquals(10L, response.id());
        assertEquals("Cafe", response.name());
        assertEquals(KitchenType.BRAZILIAN_FOOD, response.kitchenType());
        assertEquals("Sao Paulo", response.address().city());
        assertEquals(1, response.owners().size());
        assertEquals(1L, response.owners().getFirst().id());
        assertEquals("owner", response.owners().getFirst().login());
        assertEquals("User owner", response.owners().getFirst().name());
        assertEquals("Burger", response.items().getFirst().name());
        assertEquals("DELIVERY_OR_IN_PERSON", response.items().getFirst().itemType());
    }

    @Test
    void listsEmployees() {
        when(listRestaurantEmployeesUseCase.execute(10L, owner)).thenReturn(restaurant.getMembers());

        var employees = controller.getRestaurantEmployees(10L, owner).getBody();

        assertEquals(2, employees.size());
        assertEquals("owner", employees.getFirst().login());
        assertEquals(UserType.RESTAURANT_OWNER, employees.getFirst().role());
    }

    @Test
    void createsAndUpdatesRestaurant() {
        var create = new CreateRestaurantRequest("Cafe", "123", null, null, null, LocalTime.NOON, LocalTime.MIDNIGHT, null);
        var update = new UpdateRestaurantRequest("New cafe", null, null, null, null, null, null);
        when(createRestaurantUseCase.execute(
                new CreateRestaurantCommand("Cafe", "123", null, null, null, LocalTime.NOON, LocalTime.MIDNIGHT, null), owner))
                .thenReturn(details);
        when(updateRestaurantUseCase.execute(10L,
                new UpdateRestaurantCommand("New cafe", null, null, null, null, null, null), owner)).thenReturn(details);

        assertEquals(HttpStatus.CREATED, controller.createRestaurant(create, owner).getStatusCode());
        assertEquals(10L, controller.updateRestaurant(update, owner, 10L).getBody().id());
    }

    @Test
    void managesEmployeesAndOwnership() {
        when(addRestaurantEmployeeUseCase.execute(10L, 2L, owner))
                .thenReturn(new RestaurantMembership(restaurant, employee, UserType.RESTAURANT_EMPLOYEE));
        when(removeRestaurantEmployeeUseCase.execute(10L, 2L, owner))
                .thenReturn(new RestaurantMembership(restaurant, employee, null));

        var added = controller.addEmployee(10L, owner, 2L).getBody();
        var removed = controller.removeEmployee(10L, owner, 2L).getBody();

        assertEquals("Cafe", added.restaurantName());
        assertEquals("employee", added.userLogin());
        assertEquals(UserType.RESTAURANT_EMPLOYEE, added.userType());
        assertNull(removed.userType());
        assertEquals(HttpStatus.OK, controller.setUserAsOwner(10L, owner, 2L).getStatusCode());
        verify(setRestaurantOwnerUseCase).execute(10L, 2L, owner);
    }

    @Test
    void deletesRestaurant() {
        assertEquals(HttpStatus.NO_CONTENT, controller.deleteRestaurant(10L, owner).getStatusCode());
        verify(deleteRestaurantUseCase).execute(10L, owner);
    }
}
