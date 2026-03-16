package domain.enemy;

import domain.common.Position;

public final class EnemyContext {
    private final Position enemyPosition;
    private final Position playerPosition;
    private final boolean playerVisible;
    private final double attackRange;
    private double stepSize;

    public EnemyContext(
            Position enemyPosition,
            Position playerPosition,
            boolean playerVisible,
            double attackRange
    ) {
        this.enemyPosition = enemyPosition;
        this.playerPosition = playerPosition;
        this.playerVisible = playerVisible;
        this.attackRange = attackRange;
        this.stepSize = 1;
    }

    public Position getEnemyPosition() {
        return enemyPosition;
    }

    public Position getPlayerPosition() {
        return playerPosition;
    }

    public boolean isPlayerVisible() {
        return playerVisible;
    }

    public double getAttackRange() {
        return attackRange;
    }

    public double getStepSize() {
        return stepSize;
    }

    public void setStepSize(double step) {
        this.stepSize = step;
    }
}
