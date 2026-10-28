package br.com.fiap.restaurant.presentation.presenter;

import br.com.fiap.restaurant.domain.model.Order;
import br.com.fiap.restaurant.presentation.dto.response.OrderResponse;

import java.util.List;

public final class OrderPresenter {

    private OrderPresenter() {
    }

    public static OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                ItemPresenter.toResponse(order.getItems()),
                order.getRestaurantId(),
                order.getUserId(),
                order.getCreatedAt()
        );
    }

    public static List<OrderResponse> toResponse(List<Order> orders) {
        return orders.stream().map(OrderPresenter::toResponse).toList();
    }
}
