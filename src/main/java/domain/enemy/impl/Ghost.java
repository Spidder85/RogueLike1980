package domain.enemy.impl;

import domain.common.Direction;
import domain.common.Position;
import domain.enemy.*;

import java.util.List;
import java.util.Random;

public class Ghost extends Enemy {
    private static final Random RANDOM = new Random();
    private static final List<Direction> DIRECTIONS = List.of(
            Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT
    );

    private int teleportCooldown = 5;
    private boolean invisible = true;

    public Ghost(Position position) {
        super(
                EnemyType.GHOST,
                15,
                7,
                3,
                3,
                position
        );
    }

    @Override
    public EnemyIntent decideIntent(EnemyContext context) {

        // периодическая смена видимости
        if (RANDOM.nextInt(5) == 0) {
            invisible = !invisible;
        }

        if (!invisible &&
                context.getEnemyPosition().equals(context.getPlayerPosition())) {
            return new EnemyIntent.Attack();
        }

        // «телепортация» сведена к случайному движению
        return new EnemyIntent.Move(randomDirection());
    }

    private Direction randomDirection() {
        return DIRECTIONS.get(RANDOM.nextInt(DIRECTIONS.size()));
    }

    @Override
    public void update() {
        if (teleportCooldown > 0) teleportCooldown--;

        if (RANDOM.nextDouble() < 0.2) {
            invisible = !invisible;
        }
    }
}
