package domain.enemy;

import domain.Level;
import domain.character.Character;

public class Ogre extends Enemy {

    private int restTurns = 0;
    private boolean counterAttackReady = false;

    public Ogre() {
        super(
            EnemyType.OGRE,
            60,
            1,
            15,
            5
        );
    }

    @Override
    public void performSpecialAbility(Character player) {
        // отдых 1 ход после атаки
        restTurns = 1;
        counterAttackReady = false;  // Сброс после контратаки
    }

    @Override
    public boolean canAttack(Character player) {
        if (!canAct()) return false;

        // Контратака: если готов к ней и игрок рядом
        if (counterAttackReady && isNear(player)) {
            return true;
        }
        // Обычная атака: если игрок рядом
        return isNear(player);
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        if (isAlive()) {
            counterAttackReady = true;
        }
    }

    @Override
    public void update() {
        if (restTurns > 0) {
            restTurns--;
        }
    }

    @Override
    public void move(Level level) {
        if (!canAct()) return;

        // огр ходит на 2 клетки в случайном направлении
        moveRandomly(level);
        moveRandomly(level);
    }

    // -------------------- контратака --------------------
    @Override
    public boolean canAct() {
        return restTurns == 0 && isAlive();
    }
}
