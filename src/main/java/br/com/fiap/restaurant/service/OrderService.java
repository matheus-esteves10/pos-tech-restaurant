package br.com.fiap.restaurant.service;

import br.com.fiap.restaurant.model.Order;

public interface OrderService {

    Order findById(Long orderId);
}
