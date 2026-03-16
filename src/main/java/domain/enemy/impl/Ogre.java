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
                90,
                2,
                18,
                5,
                position
        );
    }

    @Override
    public EnemyIntent decideIntent(EnemyContext context) {
        context.setStepSize(2.0);

        Position ePos = context.getEnemyPosition();
        Position pPos = context.getPlayerPosition();

        double dx = Math.abs(ePos.dX - pPos.dX);
        double dy = Math.abs(ePos.dY - pPos.dY);
        double distance = Math.sqrt(dx * dx + dy * dy);

        // отдых после удара
        if (restTurns > 0) {
            restTurns--;
            return new EnemyIntent.Idle();
        }
        // гарантированная контратака
        if (counterAttackReady &&
                distance <= context.getAttackRange() * 2.0 ) {
            counterAttackReady = false;
            return new EnemyIntent.Attack();
        }

        // обычная атака
        if (distance <= context.getAttackRange()*2.0 ) {
            restTurns = 1;
            counterAttackReady = true;
            return new EnemyIntent.Attack();
        }

        // преследование игрока
        if (context.isPlayerVisible()) {
            Direction dir = chooseDirectionToPlayer(ePos, pPos);
            return new EnemyIntent.Move(dir);
        }

        // паттерн движения по комнате
        return new EnemyIntent.Move(randomDirection());
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
