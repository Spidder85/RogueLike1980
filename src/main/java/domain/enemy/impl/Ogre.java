package domain.enemy.impl;

import domain.common.Direction;
import domain.common.Position;
import domain.enemy.*;

import java.util.List;
import java.util.Random;

public class Ogre extends Enemy {

    private static final Random RANDOM = new Random();
    private static final List<Direction> DIRECTIONS = List.of(
            Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT
    );

    private int restTurns = 0;
    private boolean counterAttackReady = false;

    public Ogre(Position position) {
        super(
                EnemyType.OGRE,
                60,
                1,
                15,
                5,
                position
        );
    }

    @Override
    public EnemyIntent decideIntent(EnemyContext context) {

        if (restTurns > 0) {
            restTurns--;
            return new EnemyIntent.Move(randomDirection());
        }

        if (counterAttackReady &&
                context.getEnemyPosition().equals(context.getPlayerPosition())) {
            counterAttackReady = false;
            return new EnemyIntent.Attack();
        }

        if (context.getEnemyPosition().equals(context.getPlayerPosition())) {
            restTurns = 1;
            counterAttackReady = true;
            return new EnemyIntent.Attack();
        }

        return new EnemyIntent.Move(randomDirection());
    }

    private Direction randomDirection() {
        return DIRECTIONS.get(RANDOM.nextInt(DIRECTIONS.size()));
    }

    @Override
    public void update() {
        if (restTurns > 0) {
            restTurns--;
        }
    }
}
