package domain.game;

import domain.character.Backpack;
import domain.item.Item;
import domain.item.ItemType;
import domain.character.Player;
import domain.common.Position;
import domain.map.DoorMeta;
import domain.enemy.Enemy;
import domain.map.Level;
import domain.map.Room;
import settings.GameSettings;

import java.util.*;

public class GameEngine {
    private final GameSession session;
    private final TurnManager turnManager;
    private final LevelManager levelManager;

    private final Random random = new Random();

    public GameEngine(GameSession session, LevelManager levelManager) {
        this.session = session;
        this.turnManager = new TurnManager();
        this.levelManager = levelManager == null ? new LevelManager(): levelManager;
    }

    public GameEvent goToNextLevel() {
        Level currentLevel = session.getCurrentLevel();
        int nextLevelNumber = currentLevel.getIndex() + 1;

        if (nextLevelNumber > GameSettings.MAX_LEVELS) {
            session.finishGame();
            return new GameEvent("gameFinished", 0, "");
        }

        BalanceMode nextBalanceMode = BalanceEvaluator.evaluate(
                currentLevel.getIndex(),
                session.getPlayer(),
                session.getStats().currentLevelSnapshot()
        );
        session.setNextLevelBalanceMode(nextBalanceMode);

        Level nextLevel = levelManager.getOrCreateLevel(nextLevelNumber, nextBalanceMode);
        session.setCurrentLevel(nextLevel);
        Player p = session.getPlayer();
        p.getBackpack().clearKeys();  // очищаем ключи перед переходом на новый уровень

        Room start = nextLevel.startRoom;
        p.setPosition(
            start.getRandomPoint().x,// .getCenter().x,
            start.getRandomPoint().y //.getCenter().y
        );
        return new GameEvent("levelChanged", nextLevelNumber, "");
    }

    public GameEvent moveFP(int forward, int strafe) {
        Player p = session.getPlayer();

        if (forward == 0 && strafe == 0) {
            return null;
        }

        double speed = 0.20;
        double angle = p.getAngleRadian();    // переводим угол в радианы

        // forward vector (0° = вправо)
        double dirX = Math.cos(angle);
        double dirY = Math.sin(angle);

        // strafe vector (перпендикуляр вправо)
        double strafeX = -dirY;
        double strafeY = dirX;

        // итоговый вектор смещения
        double dx = dirX * forward + strafeX * strafe;
        double dy = dirY * forward + strafeY * strafe;

        // нормализуем вектор (нормализация диагонали)
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len > 0) {
            dx = (dx / len) * speed;
            dy = (dy / len) * speed;
        }

        GameEvent e = movePlayer(dx, dy, false);
        if (e.getType().equals("blocked")) {
            // wall sliding
            // сначала пробуем двигаться по X
            e = movePlayer(dx, 0, false);
            if (e.getType().equals("blocked")) {
                // затем по Y
                e = movePlayer(0, dy, false);
            }
        }

        return e;
    }

    public void rotatePlayer(double delta) {
        session.getPlayer().rotate(delta);
    }

    public GameEvent movePlayer(double dx, double dy, boolean byCell) {
        Player player = session.getPlayer();
        Level currentLevel = session.getCurrentLevel();

        double newX = player.getPosX() + dx;
        double newY = player.getPosY() + dy;

        Position newPos = new Position(newX, newY);

        Position lookupPos = byCell ? new Position(newPos.x, newPos.y) : newPos;

        DoorMeta door = currentLevel.getDoorAt(lookupPos);
        if (door != null) {
            Backpack bp = player.getBackpack();
            if (bp.hasKey(door.getColor())) {
                bp.useKey(door.getColor()); // тратим ключ
                currentLevel.removeDoor(door);  // открываем дверь

                session.pushEvent(new GameEvent("doorOpened", 0, door.getColor().name()));
            } else {
                session.pushEvent(new GameEvent("doorLocked", 0, door.getColor().name()));
                return new GameEvent("blocked", 0, "");
            }
        }

        if (!currentLevel.isWalkable(newPos)) {
            return new GameEvent("blocked", 0, "");
        }

        Enemy enemy = currentLevel.getEnemyAt(lookupPos);
        if (enemy != null) {   // если "наступили" на врага
            double hitChance = (double) player.getAgility() / (enemy.getAgility() + player.getAgility());
            boolean isHit = Math.random() <= hitChance;
            // расчет урона с долей случайности
            int damage = player.getStrength()
                    + (player.getCurrentWeapon() != null ? player.getCurrentWeapon().getStrength() : 0)
                    + (int)(Math.random() * 4);

            if (isHit) isHit = enemy.takeDamage(damage);

            if (!enemy.isAlive()) {
                generateTreasure(enemy);
                currentLevel.removeEnemy(enemy);
                return new GameEvent("enemyKilled", 0, enemy.getType().name());
            } else {
                return new GameEvent(
                        isHit ? "hit" : "miss",
                        isHit ? damage : 0,
                        enemy.getType().name()
                );
            }
        }

        player.setPosition(newX, newY);

        if (currentLevel.isExit(newPos)) {  // если "наступили" на выход
            return goToNextLevel();
        }

        List<Item> itemsAtCell = new ArrayList<>(currentLevel.getItemsAt(lookupPos));
        List<GameEvent> events = new ArrayList<>();

        for (Item item : itemsAtCell) { // обработка всех предметов лежащих в одной клетке
            if(item.getType() == ItemType.KEY) {
                player.getBackpack().addKey(item.getKeyColor());
                currentLevel.removeItem(item);
                events.add(new GameEvent("key", item.getCost(), item.getSubtype()));
            }else {
                boolean picked = player.getBackpack().addItem(item);
                if (picked) {
                    currentLevel.removeItem(item);
                    events.add(new GameEvent("pickup", item.getCost(), item.getType().name()));
                } else {
                    events.add(new GameEvent("inventoryFull", 0, item.getType().name()));
                }
            }
        }
        if (events.isEmpty())
            return new GameEvent("moved", 0,"");

        for (int i = 0; i < events.size() - 1; i++) {
            session.pushEvent(events.get(i));
        }
        return events.getLast();
    }

    public void nextTurn() {
        List<GameEvent> events = turnManager.nextTurn(session);
        Player player = session.getPlayer();
        player.update();
        for (GameEvent e : events) {
            session.pushEvent(e);
        }
    }

    public GameSession getSession() { return session; }
    public LevelManager getLevelManager() { return levelManager; }
    public TurnManager getTurnManager() { return turnManager; }

    public GameEvent useItem(ItemType type, int index) {
        Player player = session.getPlayer();
        Level level = session.getCurrentLevel();

        // снять оружие
        if (type == ItemType.WEAPON && index == -1) {
            Item currentWeapon = player.getCurrentWeapon();
            if (currentWeapon != null) {
                player.setCurrentWeapon(null);
                player.getBackpack().addItem(currentWeapon);
            }
            return new GameEvent(
                    "weaponRemoved",
                    0,
                    ""
            );
        }

        List<Item> items = player.getBackpack().getItems(type);
        if (index < 0 || index >= items.size())
            return null;

        Item item = items.get(index);

        if (type == ItemType.WEAPON) {
            Item oldWeapon = player.getCurrentWeapon();
            if (oldWeapon != null) {
                // выбросить на пол
                Position dropPos = level.findFreeAdjacentCell(player.getPosition());
                if (dropPos != null) {
                    oldWeapon.setPosition(dropPos);
                    level.addItem(oldWeapon);
                }
            }
            player.setCurrentWeapon(item);
        }
        // применяем предмет
        item.apply(player);

        player.getBackpack().removeItem(item);

        return new GameEvent(
                type == ItemType.WEAPON ? "weaponEquipped" : "usedItem",
                0,
                item.getSubtype()
        );
    }

    private void generateTreasure(Enemy enemy) {
        Level level = session.getCurrentLevel();

        int base = enemy.getHostility() +
                   enemy.getStrength() +
                   enemy.getAgility() +
                   enemy.getMaxHealth() / 2;
        int min = Math.max(1, base / 4);
        int max = Math.max(min, base / 2);

        int amount = random.nextInt(max - min + 1) + min;

        Item gold = new Item(
            ItemType.TREASURE,
            "gold",
            0,
            0,
            0,
            0,
            amount,
            0
        );
        gold.setPosition(new Position(enemy.getX(), enemy.getY()));
        level.addItem(gold);
    }
}
