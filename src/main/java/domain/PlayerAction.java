package domain;

public record PlayerAction(PlayerActionType type, int dx, int dy, Item item) {}
