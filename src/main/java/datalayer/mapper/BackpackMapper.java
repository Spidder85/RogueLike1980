package datalayer.mapper;

import datalayer.dto.BackpackDTO;
import datalayer.dto.ItemDTO;
import domain.item.Item;
import domain.item.ItemType;
import domain.character.Backpack;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BackpackMapper {
    public static BackpackDTO toDTO(Backpack bp) {
        BackpackDTO dto = new BackpackDTO();
        dto.treasureAmount = bp.getTreasureAmount();
        dto.keyMask = bp.getKeyMask();

        dto.items = bp.getItemsMap().entrySet().stream()
            .collect(Collectors.toMap(
                e -> e.getKey().name(), // ItemType → String
                e -> e.getValue().stream()
                    .map(ItemMapper::toDTO)
                    .toList()
            ));

        return dto;
    }

    public static void fromDTO(BackpackDTO dto, Backpack bp) {
        bp.clear();

        for (Map.Entry<String, List<ItemDTO>> e : dto.items.entrySet()) {
            for (ItemDTO itemDTO : e.getValue()) {
                Item item = ItemMapper.fromDTO(itemDTO);
                bp.addItem(item);
            }
        }
        if (dto.treasureAmount > 0) {
            Item treasure = new Item(
                    ItemType.TREASURE,
                    "gold",
                    0, 0, 0, 0,
                    dto.treasureAmount,
                    0
            );
            bp.addItem(treasure);
        }
        if (dto.keyMask > 0) {
            bp.setKeyMask(dto.keyMask);
//            for (int i = 0; i < 4; i++) {
//                if ((dto.keyMask & (1 << i)) != 0) {
//                    bp.addKey(ItemType.KeyColor.values()[i]);
//                }
//            }
        }
    }
}
