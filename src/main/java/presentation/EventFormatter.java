package presentation;

import domain.game.GameEvent;

public class EventFormatter {
    public static String format(GameEvent e) {
        boolean isEnemy = e.getItemType().equals("player");
        return  switch (e.getType()) {
            case "moved" -> "Игрок переместился";
            case "blocked" -> "Путь заблокирован";

            case "pickup" -> "Игрок поднял " + e.getItemType();
            case "hit" -> "Игрок атаковал " + e.getItemType() + " и нанёс " + e.getValue() + " урона";
            case "miss" -> "Игрок промахнулся";
            case "enemyMiss" -> e.getItemType() + " промахнулся";
            case "enemyHit" -> e.getItemType() + " атаковал и нанёс " + e.getValue() + " урона";
            case "enemyKilled" -> "Враг повержен";
            case "playerKilled" -> "Игрок повержен";
            case "sleep" -> "Игрок уснул";
            case "exit" -> "Найден переход на следующий уровень";

            case "weaponRemoved" -> "Оружие " + e.getItemType() + " убрано";
            case "weaponEquipped" -> "Оружие " + e.getItemType() + " экипировано'";
            case "usedItem" -> "Использован предмет " + e.getItemType();
            case "vampireDrain" -> "Вампир поглощает " + e.getValue() + " здоровья";
            case "snakeMagePutToSleep" -> "Змей маг усыпил игрока";

            default -> "Произошло что-то странное...";
        };
    }
}
