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
import java.util.List;

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
        Address address = Fixtures.address();
        KitchenType kitchen = KitchenType.FAST_FOOD;
        assertRejected("Name cannot be blank", () -> Restaurant.create(" ", "123", null, address, null, kitchen,
                LocalTime.NOON, LocalTime.MIDNIGHT, owner));
        assertRejected("CNPJ cannot be blank", () -> Restaurant.create("Cafe", null, null, address, null, kitchen,
                LocalTime.NOON, LocalTime.MIDNIGHT, owner));
        assertRejected("Address cannot be null", () -> Restaurant.create("Cafe", "123", null, null, null, kitchen,
                LocalTime.NOON, LocalTime.MIDNIGHT, owner));
        assertRejected("Kitchen type cannot be null", () -> Restaurant.create("Cafe", "123", null, address, null, null,
                LocalTime.NOON, LocalTime.MIDNIGHT, owner));
        assertRejected("Opening time cannot be null", () -> Restaurant.create("Cafe", "123", null, address, null, kitchen,
                null, LocalTime.MIDNIGHT, owner));
        assertRejected("Closing time cannot be null", () -> Restaurant.create("Cafe", "123", null, address, null, kitchen,
                LocalTime.NOON, null, owner));
        assertRejected("Owner cannot be null", () -> Restaurant.create("Cafe", "123", null, address, null, kitchen,
                LocalTime.NOON, LocalTime.MIDNIGHT, null));
    }

    @Test
    void rejectsIncompleteAddress() {
        Address withoutCity = new Address("Main St", "10", null, null, "SP", "01000-000", null);

        assertRejected("Address city cannot be blank", () -> Restaurant.create("Cafe", "123", null, withoutCity, null,
                KitchenType.FAST_FOOD, LocalTime.NOON, LocalTime.MIDNIGHT, owner));
    }

    @Test
    void setsAddressWhenRestaurantHadNone() {
        Restaurant legacy = new Restaurant(10L, "Cafe", "123", null, null, null, null,
                LocalTime.NOON, LocalTime.MIDNIGHT, true, List.of(new RestaurantMember(owner, UserType.RESTAURANT_OWNER)));

        legacy.update(null, null, Fixtures.address(), null, null, null, null);

        assertEquals(Fixtures.address(), legacy.getAddress());
    }

    @Test
    void disablesAndExposesOwners() {
        Restaurant restaurant = Fixtures.restaurant(10L, owner, employee);
        assertEquals(List.of(owner), restaurant.getOwners());

        restaurant.promoteToOwner(employee.getId());
        restaurant.disable();

        assertEquals(List.of(owner, employee), restaurant.getOwners());
        assertFalse(restaurant.isEnabled());
    }

    private static void assertRejected(String message, org.junit.jupiter.api.function.Executable executable) {
        assertEquals(message, assertThrows(DomainValidationException.class, executable).getMessage());
    }

    @Test
    void updatesOnlyProvidedFields() {
        Restaurant restaurant = Fixtures.restaurant(10L, owner);
        restaurant.update(null, null, null, null, null, null, null);
        assertEquals("Cafe", restaurant.getName());

        restaurant.update("Bistro", "new", new Address("Other", "1", null, null, null, null, null), "11900000000",
                KitchenType.VEGAN_FOOD, LocalTime.of(9, 0), LocalTime.of(18, 0));

        assertEquals("Bistro", restaurant.getName());
        assertEquals("new", restaurant.getDescription());
        assertEquals("Other", restaurant.getAddress().street());
        assertEquals("1", restaurant.getAddress().number());
        assertEquals("Sao Paulo", restaurant.getAddress().city());
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
