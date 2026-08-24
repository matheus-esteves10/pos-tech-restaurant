package br.com.fiap.restaurant.service.impl;

import br.com.fiap.restaurant.dto.request.CreateItemRequest;
import br.com.fiap.restaurant.dto.request.UpdateItemRequest;
import br.com.fiap.restaurant.dto.response.ItemResponse;
import br.com.fiap.restaurant.exception.EntityNotFoundException;
import br.com.fiap.restaurant.model.Item;
import br.com.fiap.restaurant.model.Restaurant;
import br.com.fiap.restaurant.model.User;
import br.com.fiap.restaurant.repository.ItemRepository;
import br.com.fiap.restaurant.service.ItemService;
import br.com.fiap.restaurant.service.RestaurantService;
import br.com.fiap.restaurant.service.RestaurantUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final RestaurantService restaurantService;
    private final RestaurantUserService restaurantUserService;

    @Transactional
    public ItemResponse createItem(CreateItemRequest request, Long restaurantId, User authenticatedUser) {
        restaurantUserService.validateUserIsAssocieted(authenticatedUser, restaurantId);

        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);

        Item item = CreateItemRequest.toItem(request, restaurant);
        Item savedItem = itemRepository.save(item);

        return ItemResponse.fromItem(savedItem);
    }

    @Transactional
    public ItemResponse updateItem(UpdateItemRequest request, Long restaurantId, Long itemId, User authenticatedUser) {
        restaurantUserService.validateUserIsAssocieted(authenticatedUser, restaurantId);

        Item item = findByIdAndRestaurantId(itemId, restaurantId);

        UpdateItemRequest.update(item, request);

        Item updatedItem = itemRepository.save(item);

        return ItemResponse.fromItem(updatedItem);
    }

    @Transactional
    public void deleteItem(Long restaurantId, Long itemId, User authenticatedUser) {
        restaurantUserService.validateUserIsAssocieted(authenticatedUser, restaurantId);

        Item item = findByIdAndRestaurantId(itemId, restaurantId);

        item.setEnabled(false);
        itemRepository.save(item);
    }

    @Override
    public Item findById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Item findByIdAndRestaurantId(Long itemId, Long restaurantId) {
        Item item = findById(itemId);

        if (!item.getRestaurant().getId().equals(restaurantId)) {
            throw new EntityNotFoundException();
        }

        return item;
    }
}
