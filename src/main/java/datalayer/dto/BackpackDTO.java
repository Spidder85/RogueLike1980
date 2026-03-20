package datalayer.dto;

import java.util.List;
import java.util.Map;

public record BackpackDTO (
    Map<String, List<ItemDTO>> items,
    int treasureAmount,
    int keyMask
){}
