package br.com.fiap.restaurant.infrastructure.persistence.adapter;

import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.domain.model.PageResult;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.infrastructure.persistence.EntityFixtures;
import br.com.fiap.restaurant.infrastructure.persistence.entity.UserEntity;
import br.com.fiap.restaurant.infrastructure.persistence.jpa.UserJpaRepository;
import br.com.fiap.restaurant.support.Fixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock UserJpaRepository jpaRepository;
    @InjectMocks UserRepositoryAdapter adapter;

    @Test
    void insertsNewUser() {
        when(jpaRepository.saveAndFlush(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            entity.setId(7L);
            return entity;
        });

        User saved = adapter.save(User.create("Jane Doe", "jane@mail.com", "jane", null, "hash", null));

        assertEquals(7L, saved.getId());
        assertEquals("jane", saved.getLogin());
    }

    @Test
    void updatesExistingUserOrFailsWhenMissing() {
        UserEntity existing = EntityFixtures.user(1L, "jane");
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(jpaRepository.saveAndFlush(existing)).thenReturn(existing);
        User user = Fixtures.user(1L, "jane");
        user.update("new@mail.com", null, null, null);

        assertEquals("new@mail.com", adapter.save(user).getEmail());
        assertEquals("new@mail.com", existing.getEmail());

        when(jpaRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> adapter.save(Fixtures.user(2L, "ghost")));
    }

    @Test
    void findsUsers() {
        UserEntity entity = EntityFixtures.user(1L, "jane");
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(jpaRepository.findByLogin("jane")).thenReturn(Optional.of(entity));
        when(jpaRepository.findAll()).thenReturn(List.of(entity));

        assertEquals("jane", adapter.findById(1L).orElseThrow().getLogin());
        assertEquals(1L, adapter.findByLogin("jane").orElseThrow().getId());
        assertEquals(1, adapter.findAll().size());
    }

    @Test
    void findsUsersByNameSortedAndPaged() {
        PageRequest pageable = PageRequest.of(0, 5, Sort.by("name").ascending());
        when(jpaRepository.findByNameIgnoreCase("jane", pageable))
                .thenReturn(new PageImpl<>(List.of(EntityFixtures.user(1L, "jane")), pageable, 1));

        PageResult<User> page = adapter.findByName("jane", 0, 5);

        assertEquals(1, page.content().size());
        assertEquals(0, page.page());
        assertEquals(5, page.size());
        assertEquals(1, page.totalElements());
        assertEquals(1, page.totalPages());
    }
}
