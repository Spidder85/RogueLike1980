package domain.game;

import domain.Item;
import domain.ItemType;
import domain.character.Player;
import domain.common.Position;
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

//    public GameEngine(Player player) {
//        this.session = new GameSession(player);
//        this.turnManager = new TurnManager();
//        this.levelManager = new LevelManager();
//    }

    public GameEngine(GameSession session, LevelManager levelManager) {
        this.session = session;
        this.turnManager = new TurnManager();
        this.levelManager = levelManager == null ? new LevelManager(): levelManager;
        //this.levelManager.createLevels();
    }

//    public void startNewGame() {
//        levelManager.createLevels();
//        Level firstLevel = levelManager.getLevel(1);
//        session.reset();
//        session.setCurrentLevel(firstLevel);
//
//        Player player = session.getPlayer();
//        Room startRoom = firstLevel.startRoom;
//        player.setPosition(startRoom.getCenter().x, startRoom.getCenter().y);
//    }

    public GameEvent goToNextLevel() {
        Level currentLevel = session.getCurrentLevel();
        int nextLevelNumber = currentLevel.getIndex() + 1;

        if (nextLevelNumber > GameSettings.MAX_LEVELS) {
            session.finishGame();
            return new GameEvent("gameFinished", 0, 0, 0, "");
        }

        Level nextLevel = levelManager.getLevel(nextLevelNumber);
        session.setCurrentLevel(nextLevel);
        Player p = session.getPlayer();
        Room start = nextLevel.startRoom;
        p.setPosition(
            start.getCenter().x,
            start.getCenter().y
        );
        return new GameEvent("levelChanged", 0, 0, nextLevelNumber, "");
    }

    public GameEvent movePlayer(int dx, int dy) {
        Player player = session.getPlayer();
        Level currentLevel = session.getCurrentLevel();

        int newX = player.getX() + dx;
        int newY = player.getY() + dy;
        Position newPos = new Position(newX, newY);

        if (!currentLevel.isWalkable(newPos)) {
            return null;//new GameEvent("blocked", newX, newY, 0, "");
        }

        Object obj = currentLevel.getObjectAt(newPos);

        if (obj instanceof Enemy enemy) {   // если "наступили" на врага
            double hitChance = (double) player.getAgility() / (enemy.getAgility() + player.getAgility());
            boolean isHit = Math.random() <= hitChance;
            int damage = player.getStrength() + (player.getCurrentWeapon() != null ? player.getCurrentWeapon().getStrength() : 0);
            if (isHit) isHit = enemy.takeDamage(damage);

            if (!enemy.isAlive()) {
                generateTreasure(enemy);
                currentLevel.removeEnemy(enemy);
                return new GameEvent("enemyKilled", newX, newY, 0, enemy.getType().name());
            } else {
                return new GameEvent(
                        isHit ? "hit" : "miss",
                        newX,
                        newY,
                        isHit ? damage : 0,
                        enemy.getType().name()
                );
            }
        }

        player.setPosition(newX, newY);

        if (obj instanceof Item item) { // если "наступили" на предмет
            boolean picked = player.getBackpack().addItem(item);
            if (picked)
                currentLevel.removeItem(item);
            return new GameEvent("pickup", newX, newY, item.getCost(), item.getType().name());
        }

        if (currentLevel.isExit(newPos)) {  // если "наступили" на выход
            return goToNextLevel();
            //return new GameEvent("exit", newX, newY, 0, "");
        }
        return null; //new GameEvent("moved", newX, newY, 0, "");
    }

    public void nextTurn() {
        List<GameEvent> events = turnManager.nextTurn(session);
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
                    player.getX(),
                    player.getY(),
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
                    oldWeapon.setPosition(dropPos.x, dropPos.y);
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
                player.getX(),
                player.getY(),
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
        gold.setPosition(enemy.getX(), enemy.getY());
        level.addItem(gold);
    }
}