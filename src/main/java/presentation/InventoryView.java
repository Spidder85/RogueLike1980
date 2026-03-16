package presentation;

import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import domain.item.Item;
import domain.item.ItemType;
import domain.character.Player;

import java.util.List;

public class InventoryView {
    private final Screen screen;

    public InventoryView(Screen screen) {
        this.screen = screen;
    }

    public Integer chooseItem(Player player, ItemType type) {
        List<Item> items = player.getBackpack().getItems(type);
        boolean isWeapon = type == ItemType.WEAPON && player.getCurrentWeapon() != null;
        if (items.isEmpty() && !isWeapon) return null;

        draw(items, type, player);

        while (true) {
            try {
                KeyStroke key = screen.readInput();
                if (key == null) continue;

                if (key.getKeyType() == KeyType.Escape)
                    return null;

                if (key.getCharacter() == null)
                    continue;
                char c = key.getCharacter();

                if (type == ItemType.WEAPON && c == '0' && player.getCurrentWeapon() != null)
                    return -1;  // убрать оружие

                int index = c - '1';
                if (index >= 0 && index < items.size())
                    return index;
            } catch (Exception ignored) {
            }
        }
    }

    private void draw(List<Item> items, ItemType type, Player player) {
        screen.clear();
        var g = screen.newTextGraphics();

        g.putString(2, 1, "Выберите " + type + ":");

        int y = 3;
        int idx = 1;
        if (type == ItemType.WEAPON && player.getCurrentWeapon() != null) {
            g.putString(5, y++, "0: Убрать оружие");
        }
        for (Item it : items) {
            String str = (idx++) + ": " + Item.toString(it);
            g.putString(5, y++, str);
        }

        try {
            screen.refresh();
        } catch (Exception ignored) {}
    }
}
