package domain;

import domain.character.Character;
import domain.enemy.Enemy;

import java.util.*;

public class GameEngine {
    private final Character player;
    private Level level;
    private boolean gameOver = false;

    private final Random random = new Random();

    public GameEngine(Character player, Level level) {
        this.player = player;
        this.level = level;
    }

    // =========================
    // ОСНОВНОЙ ИГРОВОЙ ЦИКЛ
    // =========================
    public void nextTurn(PlayerAction action) {
        if (gameOver) return;         // игра окончена

        processPlayerTurn(action);          // ход игрока

        if (!player.isAlive()) {
            gameOver = true;          // игрок мертв
            return;
        }

        processEnemiesTurn();         // ход врагов

        if (!player.isAlive()) {
            gameOver = true;          // игрок мертв
            return;
        }

        updateEntities();             // обновление состояний
    }

    // =========================
    // ХОД ИГРОКА
    // =========================
    private void processPlayerTurn(PlayerAction action) {
        if (!player.canAct()) {
            return;
        }

        switch (action.type()) {
            case MOVE -> handleMove(action.dx(), action.dy());
            case USE_ITEM -> handleUseItem(action.item());
            case CHANGE_WEAPON -> handleChangeWeapon(action.item());
            case WAIT -> { }
        }

        Enemy enemy = level.getEnemyAt(player.getX(), player.getY());
        if (enemy != null && enemy.isAlive()) {
            attackEnemy(enemy);
        }
    }

    // =========================
    // ДВИЖЕНИЕ ИГРОКА
    // =========================
    private void handleMove(int dx, int dy) {
        int targetX = player.getX() + dx;
        int targetY = player.getY() + dy;

        if(!level.canMoveTo(targetX, targetY)) return;

        player.move(dx, dy);
    }

    private void handleUseItem(Item item) {
        if (player.getBackpack().removeItem(item)) {
            item.apply(player);
        }
    }

    private void handleChangeWeapon(Item weapon) {
        Item oldWeapon = player.getCurrentWeapon();
        if (oldWeapon != null) {
            player.getBackpack().addItem(oldWeapon);
            //🔧 Нужно:
            //положить oldWeapon в level.getItems()
            //рядом с игроком
        }
        if (player.getBackpack().removeItem(weapon)) {
            player.setCurrentWeapon(weapon);
        }
    }

    // ---------- ХОД ВРАГОВ ----------
    private void processEnemiesTurn() {
        List<Enemy> enemies = level.getEnemies();

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive() || !enemy.canAct()) continue; // пропускаем мертвых врагов

            if (enemy.isNear(player) && enemy.canAttack(player)) {
                attackPlayer(enemy);
                if (!player.isAlive()) return; // игрок мертв
            }
            else if(enemy.isPlayerInRange(player)) { // если видит игрока — идёт к нему
                enemy.moveTowardsPlayer(player, level);
            }
            else {
                enemy.move(level);
            }
        }
    }

    // =========================
    // АТАКИ
    // =========================

    // Проверка попадания
    private boolean hit(int attackerAgility, int defenderAgility) {
        int chance = attackerAgility * 100 / (attackerAgility + defenderAgility + 1);
        return random.nextInt(100) < chance;
    }

    private void attackEnemy(Enemy enemy) {
        // Проверка попадания
        if (!hit(player.getAgility(), enemy.getAgility())) return;

//        if (enemy.shouldSkipAttack()) {
//            enemy.resetSkip();
//            return;
//        }

        int weaponDamage = player.getCurrentWeapon() != null
                ? player.getCurrentWeapon().getStrength() : 0;

        int damage = Math.max(1,
                player.getStrength() + weaponDamage - enemy.getAgility()); // наносимый урон

        enemy.takeDamage(damage); // нанесение урона

        if (!enemy.isAlive()) {
            dropTreasure(player, enemy); // дроп предметов
        }
    }

    private void attackPlayer(Enemy enemy) {
        // Проверка специальных условий врага
        if (!enemy.canAttack(player)) return; //например первый удар по вампиру

        // Проверка попадания
        if (!hit(enemy.getAgility(), player.getAgility())) return;

        // расчет урона
        int damage = Math.max(1,
                enemy.getStrength() - player.getAgility()
        );

        player.takeDamage(damage);

        // применим специальную способность врага
        enemy.performSpecialAbility(player);
    }

    // =========================
    // ОБНОВЛЕНИЕ СОСТОЯНИЙ
    // =========================
    private void updateEntities() {
        player.update();

        level.getEnemies().removeIf(enemy -> !enemy.isAlive());

        for (Enemy enemy : level.getEnemies()) {
            enemy.update();
        }
    }

    private void dropTreasure(domain.character.Character player, Enemy enemy) {
        int value =
                enemy.getAgility() +
                        enemy.getStrength() +
                        enemy.getHostility();
        player.getBackpack().addItem(
                new Item(
                        ItemType.TREASURE,
                        "drop",
                        0,
                        0,
                        0,
                        0,
                        value,
                        0
                )
        );
    }

    // =========================
    // геттеры и сеттеры
    // =========================
    public boolean isGameOver() { return gameOver; }
    public Character getPlayer() { return player; }
    public Level getLevel() { return level; }

    public void setLevel(Level level) {
        this.level = level;
    }
}