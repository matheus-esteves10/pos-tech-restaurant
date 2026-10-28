package br.com.fiap.restaurant.presentation.presenter;

import br.com.fiap.restaurant.domain.model.Item;
import br.com.fiap.restaurant.presentation.dto.response.ItemResponse;

import java.util.List;

public final class ItemPresenter {

    private ItemPresenter() {
    }

    public static ItemResponse toResponse(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getValue(),
                item.getImageUrl(),
                item.getItemType().name()
        );
    }

    public static List<ItemResponse> toResponse(List<Item> items) {
        return items.stream().map(ItemPresenter::toResponse).toList();
    }
}
