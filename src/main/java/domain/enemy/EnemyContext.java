package domain.enemy;

import domain.common.Position;

public final class EnemyContext {
    private final Position enemyPosition;
    private final Position playerPosition;
    private final boolean playerVisible;

    public EnemyContext(
            Position enemyPosition,
            Position playerPosition,
            boolean playerVisible
    ) {
        this.enemyPosition = enemyPosition;
        this.playerPosition = playerPosition;
        this.playerVisible = playerVisible;
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
}
