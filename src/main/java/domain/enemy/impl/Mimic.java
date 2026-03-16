package domain.enemy.impl;

import domain.common.Direction;
import domain.common.Position;
import domain.enemy.*;
import domain.item.Item;
import domain.item.ItemType;
import domain.item.KeyColor;

import java.util.List;
import java.util.Random;

public class Mimic extends Enemy {
    private static final int COOLDOWN_STEPS = 7;

    private static final Random RANDOM = new Random();
    private static final List<Direction> DIRECTIONS = List.of(
            Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT
    );

    private int stepCooldown = COOLDOWN_STEPS;

    public boolean isDisguised() {
        return disguised;
    }

    public Item getDisguiseItem() {
        return disguiseType;
    }

    private boolean disguised = true;
    private Item disguiseType;

    public Mimic(Position position) {
        super(
                EnemyType.MIMIC,
                50,
                11,
                5,
                2,
                position
        );
        this.disguiseType = randomItem();
    }

    private Item randomItem() {
        ItemType[] values = ItemType.values();
        ItemType type = values[RANDOM.nextInt(values.length)];
        if (type == ItemType.KEY){
            KeyColor color = KeyColor.values()[RANDOM.nextInt((KeyColor.values().length))];
            Item it = new Item(type, color.name().toLowerCase(),0,0,0,0,0,0);
            it.setKeyColor(color);
            return it;
        }

        return new Item(type,"",0,0,0,0,0,0);
    }

    @Override
    public EnemyIntent decideIntent(EnemyContext context) {
        Position ePos = context.getEnemyPosition();
        Position pPos = context.getPlayerPosition();
        double dx = Math.abs(ePos.dX - pPos.dX);
        double dy = Math.abs(ePos.dY - pPos.dY);
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (disguised) {    // если замаскирован
            // в радиусе атаки
            if ( distance <= context.getAttackRange()*2 && context.isPlayerVisible() ) {
                disguised = false;
                return new EnemyIntent.Attack(); // попадание
            }
            // ждать
            return new EnemyIntent.Idle();
        }

        if (!context.isPlayerVisible()) {
            if (stepCooldown <= 0) {
                makeDisguise();
                return new EnemyIntent.Idle();
            }
            else {
                stepCooldown--;
                return new EnemyIntent.Move(randomDirection());
            }
        }
        if (stepCooldown < COOLDOWN_STEPS) stepCooldown = COOLDOWN_STEPS;
        Direction direction = chooseDirectionToPlayer(ePos, pPos);
        return new EnemyIntent.Move(direction);
    }

    private Direction randomDirection() {
        return DIRECTIONS.get(RANDOM.nextInt(DIRECTIONS.size()));
    }

    private void makeDisguise() {
        this.disguiseType = randomItem();
        //stepCooldown = 3;
        disguised = true;
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
