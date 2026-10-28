package br.com.fiap.restaurant.application.usecase.item;

import br.com.fiap.restaurant.domain.enums.ItemType;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.exception.ForbiddenOperationException;
import br.com.fiap.restaurant.domain.model.Item;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.domain.repository.ItemRepository;
import br.com.fiap.restaurant.domain.repository.RestaurantRepository;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemUseCasesTest {

    @Mock ItemRepository itemRepository;
    @Mock RestaurantRepository restaurantRepository;

    private final User owner = Fixtures.user(1L, "owner");
    private final User employee = Fixtures.user(2L, "employee");
    private final User stranger = Fixtures.user(3L, "stranger");
    private final ItemCommand command = new ItemCommand("Pizza", "Cheese", BigDecimal.TEN, "/pizza.png", ItemType.JUST_IN_PERSON);

    @Test
    void createsItemForRestaurantMember() {
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(Fixtures.restaurant(10L, owner, employee)));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Item item = new CreateItemUseCase(itemRepository, restaurantRepository).execute(10L, command, employee);

        assertEquals(10L, item.getRestaurantId());
        assertEquals("Pizza", item.getName());
        assertEquals(ItemType.JUST_IN_PERSON, item.getItemType());
    }

    @Test
    void updatesAndDeletesItem() {
        Item item = Fixtures.item(1L, 10L);
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(Fixtures.restaurant(10L, owner)));
        when(itemRepository.findByIdAndRestaurantId(1L, 10L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);

        Item updated = new UpdateItemUseCase(itemRepository, restaurantRepository).execute(10L, 1L, command, owner);
        assertEquals("Pizza", updated.getName());

        new DeleteItemUseCase(itemRepository, restaurantRepository).execute(10L, 1L, owner);
        assertFalse(item.isEnabled());
    }

    @Test
    void rejectsNonMembersUnknownRestaurantsAndUnknownItems() {
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(Fixtures.restaurant(10L, owner)));
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());
        when(itemRepository.findByIdAndRestaurantId(5L, 10L)).thenReturn(Optional.empty());
        CreateItemUseCase create = new CreateItemUseCase(itemRepository, restaurantRepository);
        UpdateItemUseCase update = new UpdateItemUseCase(itemRepository, restaurantRepository);
        DeleteItemUseCase delete = new DeleteItemUseCase(itemRepository, restaurantRepository);

        assertThrows(ForbiddenOperationException.class, () -> create.execute(10L, command, stranger));
        assertThrows(ForbiddenOperationException.class, () -> update.execute(10L, 1L, command, stranger));
        assertThrows(ForbiddenOperationException.class, () -> delete.execute(10L, 1L, stranger));
        assertThrows(EntityNotFoundException.class, () -> create.execute(99L, command, owner));
        assertThrows(EntityNotFoundException.class, () -> update.execute(10L, 5L, command, owner));
        assertThrows(EntityNotFoundException.class, () -> delete.execute(10L, 5L, owner));
        verify(itemRepository, never()).save(any());
    }
}
