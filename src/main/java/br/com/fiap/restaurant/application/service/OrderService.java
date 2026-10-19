package br.com.fiap.restaurant.application.service;

import br.com.fiap.restaurant.domain.entity.Order;

public interface OrderService {

    Order findById(Long orderId);
}
