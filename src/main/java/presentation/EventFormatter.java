package presentation;

import domain.game.GameEvent;

public class EventFormatter {
    public static String format(GameEvent e) {
        boolean isEnemy = e.getItemType().equals("player");
        return  switch (e.getType()) {
            case "moved" -> "Игрок переместился";
            case "blocked" -> "Путь заблокирован";

            case "pickup" -> "Игрок поднял " + formatItemName(e.getItemType());
            case "hit" -> "Игрок атаковал " + e.getItemType() + " и нанёс " + e.getValue() + " урона";
            case "miss" -> "Игрок промахнулся";
            case "enemyMiss" -> e.getItemType() + " промахнулся";
            case "enemyHit" -> e.getItemType() + " атаковал и нанёс " + e.getValue() + " урона";
            case "enemyKilled" -> "Враг повержен";
            case "playerKilled" -> "Игрок повержен";
            case "sleep" -> "Игрок уснул";
            case "levelChanged" -> "Найден переход на уровень" + e.getValue();
            case "gameFinished" -> "Игра завершена";

            case "weaponRemoved" -> "Оружие убрано";
            case "weaponEquipped" -> "Оружие " + e.getItemType() + " экипировано";
            case "usedItem" -> "Использован предмет " + e.getItemType();
            case "vampireDrain" -> "Вампир поглощает " + e.getValue() + " здоровья";
            case "snakeMagePutToSleep" -> "Змей маг усыпил игрока";

            case "doorOpened" -> "Дверь " + e.getItemType() + " открыта";
            case "doorLocked" -> "Дверь " + e.getItemType() + " закрыта";
            case "key" -> "Ключ " + e.getItemType() + " найден";

            case "inventoryFull" -> "Инвентарь полон, предмет не поднят";


            default -> "Произошло что-то странное...";
        };
    }

    private static String formatItemName(String itemType) {
        return switch (itemType) {
            case "TREASURE" -> "золото";
            case "FOOD" -> "еду";
            case "ELIXIR" -> "эликсир";
            case "SCROLL" -> "свиток";
            case "WEAPON" -> "оружие";
            case "KEY" -> "ключ";
            default -> itemType;
        };
    }
}
