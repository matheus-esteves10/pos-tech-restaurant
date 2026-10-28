package br.com.fiap.restaurant.presentation.controller;

import br.com.fiap.restaurant.application.usecase.item.CreateItemUseCase;
import br.com.fiap.restaurant.application.usecase.item.DeleteItemUseCase;
import br.com.fiap.restaurant.application.usecase.item.ItemCommand;
import br.com.fiap.restaurant.application.usecase.item.UpdateItemUseCase;
import br.com.fiap.restaurant.domain.enums.ItemType;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.presentation.dto.request.CreateItemRequest;
import br.com.fiap.restaurant.presentation.dto.request.UpdateItemRequest;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock CreateItemUseCase createItemUseCase;
    @Mock UpdateItemUseCase updateItemUseCase;
    @Mock DeleteItemUseCase deleteItemUseCase;
    @InjectMocks ItemController controller;

    private final User user = Fixtures.user(1L, "owner");

    @Test
    void createsAndUpdatesItem() {
        var create = new CreateItemRequest("Burger", "Tasty", BigDecimal.TEN, "/img.png", ItemType.JUST_IN_PERSON);
        var update = new UpdateItemRequest("Pizza", null, null, null, null);
        when(createItemUseCase.execute(10L, new ItemCommand("Burger", "Tasty", BigDecimal.TEN, "/img.png",
                ItemType.JUST_IN_PERSON), user)).thenReturn(Fixtures.item(3L, 10L));
        when(updateItemUseCase.execute(10L, 3L, new ItemCommand("Pizza", null, null, null, null), user))
                .thenReturn(Fixtures.item(3L, 10L));

        var created = controller.createItem(10L, create, user);

        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertEquals(3L, created.getBody().id());
        assertEquals(new BigDecimal("25.90"), created.getBody().value());
        assertEquals(HttpStatus.OK, controller.updateItem(10L, 3L, update, user).getStatusCode());
    }

    @Test
    void deletesItem() {
        assertEquals(HttpStatus.NO_CONTENT, controller.deleteItem(10L, 3L, user).getStatusCode());
        verify(deleteItemUseCase).execute(10L, 3L, user);
    }
}
