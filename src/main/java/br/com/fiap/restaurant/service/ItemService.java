package br.com.fiap.restaurant.service;

import br.com.fiap.restaurant.model.Item;

public interface ItemService {

    Item findById(Long itemId);
}
