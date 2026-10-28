package br.com.fiap.restaurant.domain.model;

import br.com.fiap.restaurant.domain.enums.KitchenType;
import br.com.fiap.restaurant.domain.enums.UserType;
import br.com.fiap.restaurant.domain.exception.BusinessRuleException;
import br.com.fiap.restaurant.domain.exception.DomainValidationException;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.exception.ForbiddenOperationException;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    private final User owner = Fixtures.user(1L, "owner");
    private final User employee = Fixtures.user(2L, "employee");
    private final User stranger = Fixtures.user(3L, "stranger");

    @Test
    void createsRestaurantWithCreatorAsOwner() {
        Restaurant restaurant = Restaurant.create("Cafe", "123", "desc", Fixtures.address(), "11999999999",
                KitchenType.ITALIAN_FOOD, LocalTime.NOON, LocalTime.MIDNIGHT, owner);

        assertNull(restaurant.getId());
        assertTrue(restaurant.isEnabled());
        assertEquals("Cafe", restaurant.getName());
        assertEquals("123", restaurant.getCnpj());
        assertEquals("desc", restaurant.getDescription());
        assertEquals(Fixtures.address(), restaurant.getAddress());
        assertEquals("11999999999", restaurant.getPhone());
        assertEquals(KitchenType.ITALIAN_FOOD, restaurant.getKitchenType());
        assertEquals(LocalTime.NOON, restaurant.getOpeningAt());
        assertEquals(LocalTime.MIDNIGHT, restaurant.getClosingAt());
        assertEquals(1, restaurant.getMembers().size());
        assertTrue(restaurant.isOwner(owner.getId()));
    }

    @Test
    void rejectsMissingRequiredFields() {
        assertThrows(DomainValidationException.class, () -> Restaurant.create(" ", "123", null, null, null, null,
                LocalTime.NOON, LocalTime.MIDNIGHT, owner));
        assertThrows(DomainValidationException.class, () -> Restaurant.create("Cafe", null, null, null, null, null,
                LocalTime.NOON, LocalTime.MIDNIGHT, owner));
        assertThrows(DomainValidationException.class, () -> Restaurant.create("Cafe", "123", null, null, null, null,
                null, LocalTime.MIDNIGHT, owner));
        assertThrows(DomainValidationException.class, () -> Restaurant.create("Cafe", "123", null, null, null, null,
                LocalTime.NOON, null, owner));
        assertThrows(DomainValidationException.class, () -> Restaurant.create("Cafe", "123", null, null, null, null,
                LocalTime.NOON, LocalTime.MIDNIGHT, null));
    }

    @Test
    void updatesOnlyProvidedFields() {
        Restaurant restaurant = Fixtures.restaurant(10L, owner);
        restaurant.update(null, null, null, null, null, null, null);
        assertEquals("Cafe", restaurant.getName());

        Address newAddress = new Address("Other", "1", null, null, null, null, null);
        restaurant.update("Bistro", "new", newAddress, "11900000000", KitchenType.VEGAN_FOOD,
                LocalTime.of(9, 0), LocalTime.of(18, 0));

        assertEquals("Bistro", restaurant.getName());
        assertEquals("new", restaurant.getDescription());
        assertEquals(newAddress, restaurant.getAddress());
        assertEquals("11900000000", restaurant.getPhone());
        assertEquals(KitchenType.VEGAN_FOOD, restaurant.getKitchenType());
        assertEquals(LocalTime.of(9, 0), restaurant.getOpeningAt());
        assertEquals(LocalTime.of(18, 0), restaurant.getClosingAt());
        assertThrows(DomainValidationException.class,
                () -> restaurant.update(" ", null, null, null, null, null, null));
    }

    @Test
    void managesEmployeesAndOwnership() {
        Restaurant restaurant = Fixtures.restaurant(10L, owner);

        restaurant.addEmployee(employee);
        assertTrue(restaurant.hasMember(employee.getId()));
        assertFalse(restaurant.isOwner(employee.getId()));
        assertEquals(UserType.RESTAURANT_EMPLOYEE, restaurant.findMember(employee.getId()).orElseThrow().getRole());
        assertThrows(BusinessRuleException.class, () -> restaurant.addEmployee(employee));

        restaurant.promoteToOwner(employee.getId());
        assertTrue(restaurant.isOwner(employee.getId()));
        assertThrows(EntityNotFoundException.class, () -> restaurant.promoteToOwner(stranger.getId()));

        restaurant.removeMember(employee.getId());
        assertFalse(restaurant.hasMember(employee.getId()));
        assertFalse(restaurant.isOwner(stranger.getId()));
    }

    @Test
    void guardsMemberAndOwnerOperations() {
        Restaurant restaurant = Fixtures.restaurant(10L, owner, employee);

        assertDoesNotThrow(() -> restaurant.ensureMember(employee));
        assertDoesNotThrow(() -> restaurant.ensureOwner(owner));
        assertThrows(ForbiddenOperationException.class, () -> restaurant.ensureOwner(employee));
        ForbiddenOperationException error = assertThrows(ForbiddenOperationException.class,
                () -> restaurant.ensureMember(stranger));
        assertTrue(error.getMessage().contains("stranger"));
    }

    @Test
    void exposesMembersAsReadOnlyCopy() {
        Restaurant restaurant = Fixtures.restaurant(10L, owner);
        assertThrows(UnsupportedOperationException.class,
                () -> restaurant.getMembers().add(new RestaurantMember(employee, UserType.RESTAURANT_EMPLOYEE)));
    }

    @Test
    void memberWithoutIdNeverMatches() {
        RestaurantMember member = new RestaurantMember(
                new User(null, "Jane", "j@mail.com", "jane", null, "hash", null, true), UserType.CLIENT);
        assertFalse(member.belongsTo(1L));
        assertFalse(member.isOwner());
        assertThrows(DomainValidationException.class, () -> new RestaurantMember(null, UserType.CLIENT));
        assertThrows(DomainValidationException.class, () -> new RestaurantMember(owner, null));
    }
}
