package br.com.fiap.restaurant.application.service.impl;

import br.com.fiap.restaurant.application.dto.request.CreateItemRequest;
import br.com.fiap.restaurant.application.dto.request.UpdateItemRequest;
import br.com.fiap.restaurant.application.dto.response.ItemResponse;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.entity.Item;
import br.com.fiap.restaurant.domain.entity.Restaurant;
import br.com.fiap.restaurant.domain.entity.User;
import br.com.fiap.restaurant.domain.repository.ItemRepository;
import br.com.fiap.restaurant.application.service.ItemService;
import br.com.fiap.restaurant.application.service.RestaurantService;
import br.com.fiap.restaurant.application.service.RestaurantUserService;
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
