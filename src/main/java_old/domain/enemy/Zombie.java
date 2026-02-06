package domain.enemy;

import domain.Level;
import domain.character.Character;

public class Zombie extends Enemy {

    public Zombie() {
        super(
            EnemyType.ZOMBIE,
                40,
                2,
                7,
                4
        );
    }

    @Override
    public void performSpecialAbility(Character player) {
        // У зомби нет спецспособностей
    }

    @Override
    public boolean canAttack(Character player) {
        return true;
    }

    @Override
    public void update() {
        // у зомби нет временных состояний
    }

    @Override
    public void move(Level level) {
        if  (random.nextDouble() < 0.3) {
            moveRandomly(level);
        }
    }
}
