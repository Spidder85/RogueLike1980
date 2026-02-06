package domain.enemy;

import domain.common.Position;
import domain.enemy.impl.*;

import java.util.Random;

public class EnemyFactory {

    private static final Random random = new Random();

    public static Enemy randomEnemy(int level, Position position) {
        EnemyType type = randomType(level);
        return create(type, position);
    }

    private static EnemyType randomType(int level) {
        EnemyType[] values = EnemyType.values();

        // чем выше уровень — тем больше шанс сложных врагов
        int bound = Math.min(values.length, 1 + level);
        return values[random.nextInt(bound)];
    }

    public static Enemy create(EnemyType type, Position position) {
        return switch (type) {
            case ZOMBIE -> new Zombie(position);
            case VAMPIRE -> new Vampire(position);
            case GHOST -> new Ghost(position);
            case OGRE -> new Ogre(position);
            case SNAKE_MAGE -> new SnakeMage(position);
            case MIMIC -> new Mimic(position);
        };
    }
}
