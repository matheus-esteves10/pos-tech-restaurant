package br.com.fiap.restaurant.application.usecase.restaurant;

import br.com.fiap.restaurant.domain.enums.KitchenType;
import br.com.fiap.restaurant.domain.enums.UserType;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.exception.ForbiddenOperationException;
import br.com.fiap.restaurant.domain.model.Item;
import br.com.fiap.restaurant.domain.model.Restaurant;
import br.com.fiap.restaurant.domain.model.RestaurantMember;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.ItemRepository;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;
import br.com.fiap.restaurant.domain.repository.UserRepository;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantUseCasesTest {

    @Mock RestaurantRepository restaurantRepository;
    @Mock ItemRepository itemRepository;
    @Mock UserRepository userRepository;

    private final User owner = Fixtures.user(1L, "owner");
    private final User employee = Fixtures.user(2L, "employee");
    private final User stranger = Fixtures.user(3L, "stranger");

    @Test
    void createsRestaurantOwnedByAuthenticatedUser() {
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RestaurantDetails details = new CreateRestaurantUseCase(restaurantRepository).execute(
                new CreateRestaurantCommand("Cafe", "123", null, null, KitchenType.FAST_FOOD,
                        LocalTime.NOON, LocalTime.MIDNIGHT, Fixtures.address()), owner);

        assertTrue(details.restaurant().isOwner(owner.getId()));
        assertTrue(details.items().isEmpty());
    }

    @Test
    void updatesRestaurantWhenOwner() {
        Restaurant restaurant = Fixtures.restaurant(10L, owner);
        List<Item> items = List.of(Fixtures.item(1L, 10L));
        when(restaurantRepository.findActiveById(10L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);
        when(itemRepository.findAllActiveByRestaurantId(10L)).thenReturn(items);

        RestaurantDetails details = new UpdateRestaurantUseCase(restaurantRepository, itemRepository).execute(10L,
                new UpdateRestaurantCommand("Bistro", null, null, null, null, null, null), owner);

        assertEquals("Bistro", details.restaurant().getName());
        assertEquals(items, details.items());
    }

    @Test
    void refusesUpdateFromNonOwnerAndUnknownRestaurant() {
        when(restaurantRepository.findActiveById(10L)).thenReturn(Optional.of(Fixtures.restaurant(10L, owner, employee)));
        when(restaurantRepository.findActiveById(99L)).thenReturn(Optional.empty());
        UpdateRestaurantUseCase useCase = new UpdateRestaurantUseCase(restaurantRepository, itemRepository);
        UpdateRestaurantCommand command = new UpdateRestaurantCommand("x", null, null, null, null, null, null);

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(10L, command, employee));
        assertThrows(EntityNotFoundException.class, () -> useCase.execute(99L, command, owner));
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    void getsAndListsRestaurantsWithItems() {
        Restaurant restaurant = Fixtures.restaurant(10L, owner);
        List<Item> items = List.of(Fixtures.item(1L, 10L));
        when(restaurantRepository.findActiveById(10L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.findActiveById(99L)).thenReturn(Optional.empty());
        when(restaurantRepository.findAllActive()).thenReturn(List.of(restaurant));
        when(itemRepository.findAllActiveByRestaurantId(10L)).thenReturn(items);

        assertEquals(new RestaurantDetails(restaurant, items),
                new GetRestaurantUseCase(restaurantRepository, itemRepository).execute(10L));
        assertThrows(EntityNotFoundException.class,
                () -> new GetRestaurantUseCase(restaurantRepository, itemRepository).execute(99L));
        assertEquals(List.of(new RestaurantDetails(restaurant, items)),
                new ListRestaurantsUseCase(restaurantRepository, itemRepository).execute());
    }

    @Test
    void listsEmployeesOnlyForMembers() {
        when(restaurantRepository.findActiveById(10L)).thenReturn(Optional.of(Fixtures.restaurant(10L, owner, employee)));
        ListRestaurantEmployeesUseCase useCase = new ListRestaurantEmployeesUseCase(restaurantRepository);

        List<RestaurantMember> members = useCase.execute(10L, employee);

        assertEquals(2, members.size());
        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(10L, stranger));
    }

    @Test
    void addsAndRemovesEmployees() {
        Restaurant restaurant = Fixtures.restaurant(10L, owner);
        when(restaurantRepository.findActiveById(10L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);
        when(userRepository.findActiveById(2L)).thenReturn(Optional.of(employee));

        RestaurantMembership added = new AddRestaurantEmployeeUseCase(restaurantRepository, userRepository)
                .execute(10L, 2L, owner);
        assertEquals(UserType.RESTAURANT_EMPLOYEE, added.role());
        assertTrue(restaurant.hasMember(2L));

        RestaurantMembership removed = new RemoveRestaurantEmployeeUseCase(restaurantRepository, userRepository)
                .execute(10L, 2L, owner);
        assertNull(removed.role());
        assertSame(employee, removed.user());
        assertFalse(restaurant.hasMember(2L));
    }

    @Test
    void membershipChangesRequireOwnerAndExistingUser() {
        when(restaurantRepository.findActiveById(10L)).thenReturn(Optional.of(Fixtures.restaurant(10L, owner, employee)));
        when(userRepository.findActiveById(9L)).thenReturn(Optional.empty());
        AddRestaurantEmployeeUseCase add = new AddRestaurantEmployeeUseCase(restaurantRepository, userRepository);
        RemoveRestaurantEmployeeUseCase remove = new RemoveRestaurantEmployeeUseCase(restaurantRepository, userRepository);

        assertThrows(ForbiddenOperationException.class, () -> add.execute(10L, 3L, employee));
        assertThrows(ForbiddenOperationException.class, () -> remove.execute(10L, 3L, employee));
        assertThrows(EntityNotFoundException.class, () -> add.execute(10L, 9L, owner));
        assertThrows(EntityNotFoundException.class, () -> remove.execute(10L, 9L, owner));
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    void promotesMemberToOwner() {
        Restaurant restaurant = Fixtures.restaurant(10L, owner, employee);
        when(restaurantRepository.findActiveById(10L)).thenReturn(Optional.of(restaurant));
        SetRestaurantOwnerUseCase useCase = new SetRestaurantOwnerUseCase(restaurantRepository);

        useCase.execute(10L, 2L, owner);

        assertTrue(restaurant.isOwner(2L));
        verify(restaurantRepository).save(restaurant);
        assertThrows(EntityNotFoundException.class, () -> useCase.execute(10L, 3L, owner));
        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(10L, 1L, stranger));
    }

    @Test
    void ownerSoftDeletesRestaurant() {
        Restaurant restaurant = Fixtures.restaurant(10L, owner, employee);
        when(restaurantRepository.findActiveById(10L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.findActiveById(99L)).thenReturn(Optional.empty());
        DeleteRestaurantUseCase useCase = new DeleteRestaurantUseCase(restaurantRepository);

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(10L, employee));
        assertThrows(EntityNotFoundException.class, () -> useCase.execute(99L, owner));
        assertTrue(restaurant.isEnabled());

        useCase.execute(10L, owner);

        assertFalse(restaurant.isEnabled());
        verify(restaurantRepository).save(restaurant);
    }
}
