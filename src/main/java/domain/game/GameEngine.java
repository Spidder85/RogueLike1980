package domain.game;

import domain.Item;
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

    public GameEngine(Player player) {
        this.session = new GameSession(player);
        this.turnManager = new TurnManager();
        this.levelManager = new LevelManager();
    }

    public void startNewGame() {
        levelManager.createLevels();
        Level firstLevel = levelManager.getLevel(1);
        session.reset();
        session.setCurrentLevel(firstLevel);

        Player player = session.getPlayer();
        Room startRoom = firstLevel.startRoom;
        player.setPosition(startRoom.getCenter().x, startRoom.getCenter().y);
    }

    public void goToNextLevel() {
        Level currentLevel = session.getCurrentLevel();
        int nextLevelNumber = currentLevel.getIndex() + 1;
        if (nextLevelNumber > GameSettings.MAX_LEVELS) {
            session.finishGame();
            return;
        }

        Level nextLevel = levelManager.getLevel(nextLevelNumber);
        session.setCurrentLevel(nextLevel);
        session.getPlayer().setPosition(nextLevel.startRoom.getCenter().x, nextLevel.startRoom.getCenter().y);
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
            goToNextLevel();
            return new GameEvent("exit", newX, newY, 0, "");
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
}