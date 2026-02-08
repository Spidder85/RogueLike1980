package datalayer.mapper;

import datalayer.dto.ItemDTO;
import domain.Item;
import domain.ItemType;

public class ItemMapper {

    public static ItemDTO toDTO(Item i) {
        ItemDTO dto = new ItemDTO();

        dto.type = i.getType().name();
        dto.subtype = i.getSubtype();

        dto.health = i.getHealth();
        dto.maxHealth = i.getMaxHealth();
        dto.agility = i.getAgility();
        dto.strength = i.getStrength();
        dto.cost = i.getCost();
        dto.duration = i.getDuration();

        dto.x = i.getX();
        dto.y = i.getY();

        return dto;
    }

    public static Item fromDTO(ItemDTO dto) {
        Item item = new Item(
                ItemType.valueOf(dto.type),
                dto.subtype,
                dto.health,
                dto.maxHealth,
                dto.agility,
                dto.strength,
                dto.cost,
                dto.duration
        );
        item.setPosition(dto.x, dto.y);
        return item;
    }
}