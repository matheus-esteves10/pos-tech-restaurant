package br.com.fiap.restaurant.domain.repository;

import br.com.fiap.restaurant.domain.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
