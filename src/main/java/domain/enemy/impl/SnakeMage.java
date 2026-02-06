package domain.enemy.impl;

import domain.common.Direction;
import domain.common.Position;
import domain.character.Character;
import domain.enemy.*;

import java.util.List;
import java.util.Random;

public class SnakeMage extends Enemy {

    private static final Random RANDOM = new Random();
    private static final List<Direction> DIRECTIONS = List.of(
            Direction.UP_LEFT,
            Direction.UP_RIGHT,
            Direction.DOWN_LEFT,
            Direction.DOWN_RIGHT
    );

    public SnakeMage(Position position) {
        super(
                EnemyType.SNAKE_MAGE,
                20,
                9,
                5,
                9,
                position
        );
    }

    @Override
    public EnemyIntent decideIntent(EnemyContext context) {
        Position ePos = context.getEnemyPosition();
        Position pPos = context.getPlayerPosition();
        int dx = Math.abs(ePos.x - pPos.x);
        int dy = Math.abs(ePos.y - pPos.y);

        // Атака только когда по диагонали вплотную
        if ( dx == 1 && dy == 1 ) {
            return new EnemyIntent.Attack();
        }

        if (!context.isPlayerVisible()) {
            return new EnemyIntent.Move(randomDirection());
        }

        Direction direction = chooseDirectionToPlayer(ePos, pPos);
        return new EnemyIntent.Move(direction);
    }

    private Direction randomDirection() {
        return DIRECTIONS.get(RANDOM.nextInt(DIRECTIONS.size()));
    }

    private Direction chooseDirectionToPlayer(Position from, Position to) {
        int dx = Integer.compare(to.x, from.x);
        int dy = Integer.compare(to.y, from.y);

        if (dx < 0 && dy < 0) return Direction.UP_LEFT;
        if (dx > 0 && dy < 0) return Direction.UP_RIGHT;
        if (dx > 0 && dy > 0) return Direction.DOWN_RIGHT;
        if (dx < 0 && dy > 0) return Direction.DOWN_LEFT;

        return randomDirection();
    }

    @Override
    public void performSpecialAbility(Character player) {
        if (RANDOM.nextInt(100) < 30) {
            player.putToSleep(1);
        }
    }

    @Override
    public void update() {}
}
