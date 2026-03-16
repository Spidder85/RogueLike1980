package domain.game;

import domain.character.Player;

public final class BalanceEvaluator {
    private BalanceEvaluator() {
    }

    public static BalanceMode evaluate(int levelIndex, Player player, LevelPerformanceSnapshot snapshot) {
        double healthRatio = player.getMaxHealth() == 0
                ? 0
                : (double) player.getHealth() / player.getMaxHealth();
        double damagePressure = player.getMaxHealth() == 0
                ? snapshot.damageTaken()
                : (double) snapshot.damageTaken() / player.getMaxHealth();
        int consumablesUsed = snapshot.foodEaten() + snapshot.elixirsDrunk() + snapshot.scrollsRead();
        double stepPressure = Math.max(0, snapshot.steps() - expectedSteps(levelIndex)) / 20.0;

        double score = 0.0;
        score += healthRatio * 45.0;
        score += Math.min(snapshot.enemiesKilled(), 8) * 3.5;
        score += Math.min(snapshot.damageDealt(), 60) * 0.15;
        score -= damagePressure * 30.0;
        score -= consumablesUsed * 7.0;
        score -= stepPressure * 4.0;

        if (score <= 18.0) {
            return BalanceMode.EASY_ASSIST;
        }
        if (score >= 42.0) {
            return BalanceMode.HARDER;
        }
        return BalanceMode.NORMAL;
    }

    private static int expectedSteps(int levelIndex) {
        return 50 + levelIndex * 8;
    }
}
