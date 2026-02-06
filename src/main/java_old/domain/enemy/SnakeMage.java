package domain.enemy;

import domain.Level;
import domain.character.Character;

public class SnakeMage extends Enemy {

    public SnakeMage() {
        super(
            EnemyType.SNAKE_MAGE,
            20,
            9,
            5,
            8
        );
    }

    @Override
    public void performSpecialAbility(Character player) {
        // шанс усыпить игрока
        if (random.nextInt(100) < 30) {
            player.putToSleep(1);
        }
    }

    @Override
    public boolean canAttack(Character player) {
        return true;
    }

    @Override
    public void update() {
        // нет состояний
    }

    @Override
    public void move(Level level) {
        int[][] dirs = {
                {1, 1}, {-1, -1}, {1, -1}, {-1, 1}
        };
        int[] d = dirs[random.nextInt(dirs.length)];

        int nx = getX() + d[0];
        int ny = getY() + d[1];

        if (level.canMoveTo(nx, ny)) {
            setPosition(nx, ny);
        }
    }
}
