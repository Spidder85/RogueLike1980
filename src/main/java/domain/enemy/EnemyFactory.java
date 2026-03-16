package domain.enemy;

import domain.common.Position;
import domain.game.BalanceMode;
import domain.enemy.impl.*;

import java.util.Random;

public class EnemyFactory {

    private static final Random random = new Random();

    public static Enemy randomEnemy(int level, Position position) {
        EnemyType type = randomType(level, BalanceMode.NORMAL, random);
        return create(type, position);
    }

    public static Enemy randomEnemy(int level, Position position, BalanceMode balanceMode, Random random) {
        EnemyType type = randomType(level, balanceMode, random);
        return create(type, position);
    }

    private static EnemyType randomType(int level, BalanceMode balanceMode, Random random) {
        EnemyType[] values = EnemyType.values();
        int baseBound = Math.min(values.length, 1 + level);

        return switch (balanceMode) {
            case EASY_ASSIST -> {
                int bound = Math.max(1, baseBound - 1);
                int easyBound = Math.max(1, (bound + 1) / 2);
                yield values[random.nextDouble() < 0.75
                        ? random.nextInt(easyBound)
                        : random.nextInt(bound)];
            }
            case HARDER -> {
                int bound = Math.min(values.length, baseBound + 1);
                int hardStart = Math.max(0, bound / 2);
                yield values[random.nextDouble() < 0.45
                        ? hardStart + random.nextInt(bound - hardStart)
                        : random.nextInt(bound)];
            }
            case NORMAL -> values[random.nextInt(baseBound)];
        };
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
