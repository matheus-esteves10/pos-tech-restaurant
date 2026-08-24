package br.com.fiap.restaurant.repository;

import br.com.fiap.restaurant.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
