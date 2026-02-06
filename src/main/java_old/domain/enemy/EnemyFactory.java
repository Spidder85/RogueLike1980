package domain.enemy;

import java.util.Random;

public class EnemyFactory {

    private static final Random random = new Random();

    public static Enemy randomEnemy(int level) {
        EnemyType type = randomType(level);
        return create(type);
    }

    private static EnemyType randomType(int level) {
        EnemyType[] values = EnemyType.values();

        // чем выше уровень — тем больше шанс сложных врагов
        int bound = Math.min(values.length, 1 + level);
        return values[random.nextInt(bound)];
    }

    public static Enemy create(EnemyType type) {
        return switch (type) {
            case ZOMBIE -> new Zombie();
            case VAMPIRE -> new Vampire();
            case GHOST -> new Ghost();
            case OGRE -> new Ogre();
            case SNAKE_MAGE -> new SnakeMage();
        };
    }
}
