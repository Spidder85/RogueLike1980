package domain.enemy;

import domain.common.Direction;
import domain.common.Position;

public sealed interface EnemyIntent
    permits EnemyIntent.Move,
        EnemyIntent.Teleport,
        EnemyIntent.Attack,
        EnemyIntent.Idle {

    record Move(Direction direction) implements EnemyIntent {}
    record Teleport(Position target) implements EnemyIntent {}
    record Attack() implements EnemyIntent {}
    record Idle() implements EnemyIntent {}
}
