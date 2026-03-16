package domain.game;

import domain.character.Player;
import domain.common.Direction;
import domain.common.Position;
import domain.enemy.EnemyContext;
import domain.enemy.EnemyIntent;
import domain.map.Level;
import domain.enemy.Enemy;
import domain.map.Room;
import settings.GameSettings;

import java.util.*;

public class TurnManager {
    private boolean isPlayerVisible(Enemy enemy, Player player, Level level) {
        double ex = enemy.getDX();
        double ey = enemy.getDY();

        double px = player.getPosX();
        double py = player.getPosY();

        double dx = px - ex;
        double dy = py - ey;

        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance > enemy.getHostility()) return false;

        double step = 0.2;

        double rayX = ex;
        double rayY = ey;

        double dirX = dx / distance;
        double dirY = dy / distance;

        for (double d = 0; d < distance; d += step) {
            rayX += dirX * step;
            rayY += dirY * step;

            int cx = (int) Math.floor(rayX);
            int cy = (int) Math.floor(rayY);

            if (!level.isWalkable(new Position(cx, cy)))
                return false;
        }
        return true;
    }

    private boolean overlaps(Position a, Position b) {
        double colBoxSize = GameSettings.COLLISION_BOX_SIZE;

        return a.dX >= b.dX - colBoxSize &&
               a.dX <= b.dX + colBoxSize &&
               a.dY >= b.dY - colBoxSize &&
               a.dY <= b.dY + colBoxSize;
    }

    private boolean isReachable(Position start, Position target, Level level) {
        Queue<Position> queue = new LinkedList<>();
        Set<Position> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Position current = queue.poll();

            if (current.equals(target))
                return true;

            for (Direction d : Direction.values()) {
                Position next = new Position(
                        current.x + d.dx,
                        current.y + d.dy
                );

                if (!visited.contains(next) && level.isWalkable(next)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        return false;
    }

    public List<GameEvent> nextTurn(GameSession session) {
        Player player = session.getPlayer();
        Level level = session.getCurrentLevel();
        List<GameEvent> events = new ArrayList<>();

        // ходы врагов
        for (Enemy enemy : level.getEnemies()) {
            if (!enemy.isAlive()) continue;

            enemy.update();

            boolean visible = isPlayerVisible(enemy, player, level);
            EnemyContext context = new EnemyContext(
                    enemy.getPosition(),
                    player.getPosition(),
                    visible,
                    session.isTopDown() ? 1 : GameSettings.COLLISION_BOX_SIZE
            );

            EnemyIntent intent = enemy.decideIntent(context);

            switch (intent) {
                case EnemyIntent.Attack a -> {
                    if (enemy.canAttack(player)) {
                        double hitChance = (double) enemy.getAgility() / (enemy.getAgility() + player.getAgility());
                        boolean isHit = Math.random() <= hitChance;

                        int damage = enemy.getStrength();

                        if (isHit) {
                            player.takeDamage(damage);
                            enemy.performSpecialAbility(player, events);
                        }
                        events.add(new GameEvent(
                                isHit ? "enemyHit" : "enemyMiss",
                                isHit ? damage : 0,
                                enemy.getType().name()
                        ));
                        if (!player.isAlive()) {
                            events.add(new GameEvent(
                                    "playerKilled",
                                    0,
                                    ""
                            ));
                        }
                    }
                }
                case EnemyIntent.Move m -> {
                    double step = (session.isFirstPerson() ? 0.2 : 1) * context.getStepSize();
                    double newX = enemy.getDX() + m.direction().dx * step;
                    double newY = enemy.getDY() + m.direction().dy * step;
                    Position newPos = new Position(newX, newY);
                    if (level.isWalkable(newPos) && !overlaps(newPos, player.getPosition())) {
                        Enemy otherEnemy = level.getEnemyAt(newPos);
                        if (otherEnemy == null || otherEnemy == enemy)
                            enemy.setPosition(newPos);
                    }
                }
                case EnemyIntent.Teleport t -> {
                    Room room = level.findNearestRoom(enemy.getPosition());
                    if (room == null) break;

                    Position cell = room.getRandomFreePoint(pos ->
                            level.getEnemyAt(pos) == null &&
                                    !(player.getPosition().equals(pos))
                    );
                    enemy.setPosition(new Position(cell.dX+0.4, cell.dY+0.4));
                }
                case EnemyIntent.Idle i -> {}
            }
        }
        return events;
    }
}
