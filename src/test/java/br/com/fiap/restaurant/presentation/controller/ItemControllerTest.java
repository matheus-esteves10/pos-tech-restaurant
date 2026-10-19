package br.com.fiap.restaurant.presentation.controller;

import br.com.fiap.restaurant.application.dto.request.CreateItemRequest;
import br.com.fiap.restaurant.application.dto.request.UpdateItemRequest;
import br.com.fiap.restaurant.application.dto.response.ItemResponse;
import br.com.fiap.restaurant.application.service.impl.ItemServiceImpl;
import br.com.fiap.restaurant.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock ItemServiceImpl service;
    @InjectMocks ItemController controller;

    @Test void delegatesCreateAndUpdate() {
        User user = User.builder().id(1L).build();
        var create = new CreateItemRequest("Soup", null, BigDecimal.TEN, null, null);
        var update = new UpdateItemRequest("Soup 2", null, null, null, null);
        ItemResponse response = new ItemResponse(3L, "Soup", null, BigDecimal.TEN, null, "DELIVERY_OR_IN_PERSON");
        when(service.createItem(create, 2L, user)).thenReturn(response);
        when(service.updateItem(update, 2L, 3L, user)).thenReturn(response);
        assertEquals(HttpStatus.CREATED, controller.createItem(2L, create, user).getStatusCode());
        assertEquals(response, controller.updateItem(2L, 3L, update, user).getBody());
    }

    @Test void delegatesDelete() {
        User user = User.builder().id(1L).build();
        assertEquals(HttpStatus.NO_CONTENT, controller.deleteItem(2L, 3L, user).getStatusCode());
        verify(service).deleteItem(2L, 3L, user);
    }
}
