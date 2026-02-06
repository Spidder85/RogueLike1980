package domain.enemy.impl;

import domain.common.Direction;
import domain.common.Position;
import domain.enemy.*;

import java.util.List;
import java.util.Random;

public class Mimic extends Enemy {

    private static final Random RANDOM = new Random();
    private static final List<Direction> DIRECTIONS = List.of(
            Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT
    );

    private boolean disguised = true;

    public Mimic(Position position) {
        super(
                EnemyType.MIMIC,
                40,
                9,
                3,
                2,
                position
        );
    }

    @Override
    public EnemyIntent decideIntent(EnemyContext context) {

        if (disguised &&
                context.getEnemyPosition().equals(context.getPlayerPosition())) {
            disguised = false;
            return new EnemyIntent.Attack();
        }

        if (!disguised &&
                context.getEnemyPosition().equals(context.getPlayerPosition())) {
            return new EnemyIntent.Attack();
        }

        return new EnemyIntent.Move(randomDirection());
    }

    private Direction randomDirection() {
        return DIRECTIONS.get(RANDOM.nextInt(DIRECTIONS.size()));
    }

    @Override
    public void update() { }
}
