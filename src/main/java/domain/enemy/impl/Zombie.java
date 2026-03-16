package domain.enemy.impl;

import domain.common.Direction;
import domain.common.Position;
import domain.enemy.*;

import java.util.List;
import java.util.Random;

public class Zombie extends Enemy {

    private static final Random RANDOM = new Random();
    private static final List<Direction> DIRECTIONS = List.of(
            Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT
    );

    public Zombie(Position position) {
        super(
                EnemyType.ZOMBIE,
                55,
                4,
                8,
                6,
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

        if ( distance <= context.getAttackRange() ) {
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

        if (dx == 0 && dy < 0) return Direction.UP;
        if (dx == 0 && dy > 0) return Direction.DOWN;
        if (dx < 0 && dy == 0) return Direction.LEFT;
        if (dx > 0 && dy == 0) return Direction.RIGHT;

        if (Math.random() < 0.5) {  // Случайный выбор между горизонталью и вертикалью
            // Идем по горизонтали
            return dx < 0 ? Direction.LEFT : Direction.RIGHT;
        } else {
            // Идем по вертикали
            return dy < 0 ? Direction.UP : Direction.DOWN;
        }
    }
    @Override
    public void update() {}
}
