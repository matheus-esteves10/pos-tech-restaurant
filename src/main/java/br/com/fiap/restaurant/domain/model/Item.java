package br.com.fiap.restaurant.domain.model;

import br.com.fiap.restaurant.domain.enums.ItemType;

import java.math.BigDecimal;

import static br.com.fiap.restaurant.domain.model.Guard.requireNonNull;
import static br.com.fiap.restaurant.domain.model.Guard.requirePositive;
import static br.com.fiap.restaurant.domain.model.Guard.requireText;

public class Item {

    private final Long id;
    private String name;
    private String description;
    private BigDecimal value;
    private String imageUrl;
    private ItemType itemType;
    private final Long restaurantId;
    private boolean enabled;

    public Item(Long id, String name, String description, BigDecimal value, String imageUrl, ItemType itemType,
                Long restaurantId, boolean enabled) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.value = value;
        this.imageUrl = imageUrl;
        this.itemType = itemType;
        this.restaurantId = restaurantId;
        this.enabled = enabled;
    }

    public static Item create(Long restaurantId, String name, String description, BigDecimal value,
                              String imageUrl, ItemType itemType) {
        requireNonNull(restaurantId, "Restaurant");
        requireText(name, "Name");
        requirePositive(value, "Value");
        return new Item(null, name, description, value, imageUrl,
                itemType != null ? itemType : ItemType.DELIVERY_OR_IN_PERSON, restaurantId, true);
    }

    public void update(String name, String description, BigDecimal value, String imageUrl, ItemType itemType) {
        if (name != null) {
            this.name = requireText(name, "Name");
        }
        if (description != null) {
            this.description = description;
        }
        if (value != null) {
            this.value = requirePositive(value, "Value");
        }
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
        if (itemType != null) {
            this.itemType = itemType;
        }
    }

    public void disable() {
        this.enabled = false;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
