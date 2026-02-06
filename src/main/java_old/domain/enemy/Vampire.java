package domain.enemy;

import domain.Level;
import domain.character.Character;

public class Vampire extends Enemy {
    private boolean firstTurn = true;

    public Vampire() {
        super(
            EnemyType.VAMPIRE,
            30,
            9,
            6,
            8
        );
    }

    @Override
    public void performSpecialAbility(Character player) {
        // Отнимает максимальное здоровье
        player.decreaseMaxHealth(3);
    }

    @Override
    public boolean canAttack(Character player) {
        return !firstTurn;
    }

    @Override
    public void update() {
        if (firstTurn) firstTurn = false;
    }

    @Override
    public void move(Level level) {
        moveRandomly(level);
    }
}
