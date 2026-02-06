package domain;

import domain.character.Character;
import domain.map.Room;

public class GameSession {
    private domain.character.Character player;
    private Level currentLevel;

    private final ScoreBoard scoreBoard = new ScoreBoard();
    private int levelIndex = 1;

    private boolean gameActive = true;

    public GameSession(domain.character.Character player, Level level) {
        this.player = player;
        this.currentLevel = level;

        Room start = level.getStartRoom();
        // Установить игрока в стартовую позицию
        //Level.Position startPosition = currentLevel.getStartPosition();
        player.setPosition(start.getCenterX(), start.getCenterY());
    }

    public Character getPlayer() {
        return player;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    public boolean isGameOver() {
        return !player.isAlive();
    }

    public void nextLevel(Level newLevel) {
        this.currentLevel = newLevel;
        levelIndex++;
    }

    //public void restart(Level newLevel, Character newPlayer) {
    public void restart(Level newLevel) {
        //this.player = newPlayer;
        player.heal(player.getMaxHealth());
        this.currentLevel = newLevel;
        this.levelIndex = 1;
    }

    public void finishGame() {
        gameActive = false;
        scoreBoard.add(
            new GameResult(
                levelIndex,
                player.getBackpack().getTreasureAmount()
            )
        );

        // По ТЗ: "После любого прохождения (успешного и нет) результат игрока
        // фиксируется в таблицу рекордов"
        saveToLeaderboard();
    }

    // Сохранение в таблицу рекордов
    private void saveToLeaderboard() {
        // TODO: Реализовать в задании 5 (JSON сохранение)
        // Пока просто логируем
        System.out.println("Game finished. Level: " + levelIndex +
                ", Treasures: " + player.getBackpack().getTreasureAmount());
    }

    public boolean checkForExit() {
        Level.Position exitPos = currentLevel.getExitPosition();
        return exitPos != null &&
                player.getX() == exitPos.x &&
                player.getY() == exitPos.y;
    }

    // метод перехода на следующий уровень
    public void proceedToNextLevel(Level nextLevel) {
        if (checkForExit()) {
            // Сохранить прогресс перед переходом (по ТЗ задания 5)
            saveGameState();

            this.currentLevel = nextLevel;
            levelIndex++;

            // Установить игрока в стартовую позицию нового уровня
            Level.Position startPos = nextLevel.getStartPosition();
            player.setPosition(startPos.x, startPos.y);
        }
    }

    private void saveGameState() {
        // TODO: Сохранить состояние в JSON (для задания 5)
        // По ТЗ: "После прохождения каждого уровня необходимо сохранять
        // полученную статистику и номер пройденного уровня"
    }

    public boolean isGameActive() {
        return gameActive && player.isAlive();
    }

    public Level.Position getExitPosition() {
        return currentLevel != null ? currentLevel.getExitPosition() : null;
    }

    public boolean isExitReached() {
        return checkForExit();
    }
    public int getCurrentLevelNumber() {
        return levelIndex;
    }
}
