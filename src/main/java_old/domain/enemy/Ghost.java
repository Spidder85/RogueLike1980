package domain.enemy;

import domain.Level;
import domain.character.Character;
//import domain.map.Room;

public class Ghost extends Enemy {

    private boolean invisible = true;
    private int teleportCooldown = 0;

    public Ghost() {
        super(
            EnemyType.GHOST,
            15,
            7,
            3,
            3
        );
    }

    @Override
    public void update() {
        if (teleportCooldown > 0) teleportCooldown--;

        if (random.nextDouble() < 0.2) {
            invisible = !invisible;
        }
    }

    @Override
    public boolean canAttack(Character player) {
        return !invisible || isNear(player);
    }

    @Override
    public void performSpecialAbility(Character player) {
        // у призрака нет спецэффекта атаки
    }

    @Override
    public void move(Level level) {
        if (teleportCooldown > 0 && room == null) return;

        teleportCooldown = 5;

        int nx = room.getX() + random.nextInt(room.getWidth());
        int ny = room.getY() + random.nextInt(room.getHeight());

        setPosition(nx, ny);
    }
}
