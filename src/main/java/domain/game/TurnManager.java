package domain.game;

import domain.character.Character;
import domain.common.Position;
import domain.enemy.EnemyContext;
import domain.enemy.EnemyIntent;
import domain.map.Level;
import domain.enemy.Enemy;

import java.util.ArrayList;
import java.util.List;

public class TurnManager {
    public List<GameEvent> nextTurn(GameSession session) {
        Character player = session.getPlayer();
        Level level = session.getCurrentLevel();
        List<GameEvent> events = new ArrayList<>();

        // ходы врагов
        for (Enemy enemy : level.getEnemies()) {
            if (!enemy.isAlive()) continue;

            EnemyContext context = new EnemyContext(
                    enemy.getPosition(),
                    player.getPosition(),
                    true
            );

            EnemyIntent intent = enemy.decideIntent(context);

            switch (intent) {
                case EnemyIntent.Attack a -> {
                    if (enemy.canAttack(player)) {
                        double hitChance = (double) enemy.getAgility() / (enemy.getAgility() + player.getAgility());
                        boolean isHit = Math.random() <= hitChance;

                        int damage = enemy.getStrength();

                        if (isHit) player.takeDamage(damage);
                        events.add(new GameEvent(
                                isHit ? "enemyHit" : "enemyMiss",
                                player.getX(),
                                player.getY(),
                                isHit ? damage : 0,
                                enemy.getType().name()
                        ));
                        if (!player.isAlive()) {
                            events.add(new GameEvent(
                                    "playerKilled",
                                    player.getX(),
                                    player.getY(),
                                    0,
                                    ""
                            ));
                        }
                    }
                }
                case EnemyIntent.Move m -> {
                    int newX = enemy.getX() + m.direction().dx;
                    int newY = enemy.getY() + m.direction().dy;
                    if (level.isWalkable(new Position(newX, newY)) && level.getEnemyAt(newX, newY) == null) {
                        enemy.setPosition(new domain.common.Position(newX, newY));
                    }
                }
                case EnemyIntent.Teleport t -> enemy.setPosition(t.target());
                case EnemyIntent.Idle i -> {}
            }
        }
        return events;
    }
}
