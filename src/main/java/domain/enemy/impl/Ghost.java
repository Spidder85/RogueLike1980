package domain.enemy.impl;

import domain.common.Direction;
import domain.common.Position;
import domain.enemy.*;

import java.util.Random;

public class Ghost extends Enemy {
    private static final Random RANDOM = new Random();

    private int teleportCooldown = 3;
    private boolean combatStarted = false;
    private boolean invisible = RANDOM.nextBoolean();

    public Ghost(Position position) {
        super(
                EnemyType.GHOST,
                18,
                12,
                4,
                3,
                position
        );
    }

    @Override
    public EnemyIntent decideIntent(EnemyContext context) {
        Position ePos = context.getEnemyPosition();
        Position pPos = context.getPlayerPosition();
        double dx = Math.abs(ePos.dX - pPos.dX);
        double dy = Math.abs(ePos.dY - pPos.dY);
        double distance = Math.sqrt(dx * dx + dy * dy);

        // атака
        if (distance <= context.getAttackRange()) {
            combatStarted = true;
            invisible = false;

            return new EnemyIntent.Attack();
        }

        // если игрок виден → преследуем
        if (context.isPlayerVisible()) {
            Direction direction = chooseDirectionToPlayer(ePos, pPos);
            return new EnemyIntent.Move(direction);
        }

        // телепортация
        if (teleportCooldown == 0) {
            teleportCooldown = 3;
            return new EnemyIntent.Teleport();
        }

        teleportCooldown--;
        return new EnemyIntent.Idle();
    }

    private Direction chooseDirectionToPlayer(Position from, Position to) {
        int dx = Integer.compare(to.x, from.x);
        int dy = Integer.compare(to.y, from.y);

        if (dx == 0 && dy < 0) return Direction.UP;
        if (dx == 0 && dy > 0) return Direction.DOWN;
        if (dx < 0 && dy == 0) return Direction.LEFT;
        if (dx > 0 && dy == 0) return Direction.RIGHT;

        return RANDOM.nextBoolean()
                ? (dx < 0 ? Direction.LEFT : Direction.RIGHT)
                : (dy < 0 ? Direction.UP : Direction.DOWN);
    }

    @Override
    public void update() {
        // меняем невидимость только до начало боя
        if (!combatStarted && RANDOM.nextDouble() < 0.15) {
            invisible = !invisible;
        }
    }

    public boolean isInvisible() {
        return invisible;
    }
}
