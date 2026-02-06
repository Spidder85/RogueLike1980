package domain.enemy.impl;

import domain.common.Direction;
import domain.common.Position;
import domain.enemy.*;

import java.util.List;
import java.util.Random;

public class Vampire extends Enemy {
    private static final Random RANDOM = new Random();
    private  static final List<Direction> DIRECTIONS = List.of(
            Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT
    );


    private boolean firstHitIgnored  = true;

    public Vampire(Position position) {
        super(
            EnemyType.VAMPIRE,
            30,
            9,
            6,
            8,
            position
        );
    }

    @Override
    public EnemyIntent decideIntent(EnemyContext context) {
        Position ePos = context.getEnemyPosition();
        Position pPos = context.getPlayerPosition();
        int dx = Math.abs(ePos.x - pPos.x);
        int dy = Math.abs(ePos.y - pPos.y);
        if ( dx + dy == 1 ) {
            return new EnemyIntent.Attack(); // попадание
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

        if (dx == 0 && dy < 0) return Direction.UP;
        if (dx == 0 && dy > 0) return Direction.DOWN;
        if (dx < 0 && dy == 0) return Direction.LEFT;
        if (dx > 0 && dy == 0) return Direction.RIGHT;

        return randomDirection();
    }

    @Override
    public void update() {}

    @Override
    public boolean takeDamage(int damage) {
        if (firstHitIgnored) {
            firstHitIgnored = false;
            return false;
        }
        return super.takeDamage(damage); // вызов родительского метода
    }
}
