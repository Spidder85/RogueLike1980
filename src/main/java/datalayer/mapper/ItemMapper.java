package datalayer.mapper;

import datalayer.dto.ItemDTO;
import domain.common.Position;
import domain.item.Item;
import domain.item.ItemType;
import domain.item.KeyColor;

public class ItemMapper {

    public static ItemDTO toDTO(Item i) {
        String keyColor = i.getType() == ItemType.KEY && i.getKeyColor() != null
            ? i.getKeyColor().name()
            : null;

        return new ItemDTO(
            i.getType().name(),
            i.getSubtype(),

            i.getHealth(),
            i.getMaxHealth(),
            i.getAgility(),
            i.getStrength(),
            i.getCost(),
            i.getDuration(),

            i.getX(),
            i.getY(),

            keyColor
        );
    }

    public static Item fromDTO(ItemDTO dto) {
        Item item = new Item(
                ItemType.valueOf(dto.type()),
                dto.subtype(),
                dto.health(),
                dto.maxHealth(),
                dto.agility(),
                dto.strength(),
                dto.cost(),
                dto.duration()
        );
        if (dto.keyColor() != null)
            item.setKeyColor(KeyColor.valueOf(dto.keyColor()));

        item.setPosition(new Position(dto.x(), dto.y()));
        return item;
    }
}
