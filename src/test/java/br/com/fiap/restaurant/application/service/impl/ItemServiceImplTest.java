package br.com.fiap.restaurant.application.service.impl;

import br.com.fiap.restaurant.application.dto.request.CreateItemRequest;
import br.com.fiap.restaurant.application.dto.request.UpdateItemRequest;
import br.com.fiap.restaurant.application.service.RestaurantService;
import br.com.fiap.restaurant.application.service.RestaurantUserService;
import br.com.fiap.restaurant.domain.entity.Item;
import br.com.fiap.restaurant.domain.entity.Restaurant;
import br.com.fiap.restaurant.domain.entity.User;
import br.com.fiap.restaurant.domain.enums.ItemType;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock ItemRepository itemRepository;
    @Mock RestaurantService restaurantService;
    @Mock RestaurantUserService restaurantUserService;
    @InjectMocks ItemServiceImpl service;

    @Test void createsItemForRestaurantMember() {
        User user = User.builder().id(4L).build();
        Restaurant restaurant = Restaurant.builder().id(2L).build();
        Item saved = Item.builder().id(8L).name("Soup").value(new BigDecimal("12.50")).restaurant(restaurant).build();
        when(restaurantService.findRestaurantById(2L)).thenReturn(restaurant);
        when(itemRepository.save(any(Item.class))).thenReturn(saved);
        var response = service.createItem(new CreateItemRequest("Soup", null, new BigDecimal("12.50"), null, null), 2L, user);
        assertEquals(8L, response.id());
        assertEquals("Soup", response.name());
        verify(restaurantUserService).validateUserIsAssocieted(user, 2L);
    }

    @Test void updatesOnlySuppliedItemFields() {
        Item item = Item.builder().id(8L).name("Old").value(BigDecimal.ONE).restaurant(Restaurant.builder().id(2L).build()).build();
        when(itemRepository.findById(8L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        var response = service.updateItem(new UpdateItemRequest("New", null, null, null, null), 2L, 8L, User.builder().id(4L).build());
        assertEquals("New", response.name());
        assertEquals(BigDecimal.ONE, item.getValue());
    }

    @Test void softDeletesItem() {
        Item item = Item.builder().id(8L).enabled(true).restaurant(Restaurant.builder().id(2L).build()).build();
        when(itemRepository.findById(8L)).thenReturn(Optional.of(item));
        service.deleteItem(2L, 8L, User.builder().id(4L).build());
        assertFalse(item.getEnabled());
        verify(itemRepository).save(item);
    }

    @Test void findsItemById() {
        Item item = Item.builder().id(8L).build();
        when(itemRepository.findById(8L)).thenReturn(Optional.of(item));
        assertSame(item, service.findById(8L));
    }

    @Test void rejectsMissingItemAndWrongRestaurant() {
        when(itemRepository.findById(8L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.findById(8L));
        Item item = Item.builder().id(8L).restaurant(Restaurant.builder().id(5L).build()).build();
        when(itemRepository.findById(8L)).thenReturn(Optional.of(item));
        assertThrows(EntityNotFoundException.class, () -> service.findByIdAndRestaurantId(8L, 2L));
    }
}
