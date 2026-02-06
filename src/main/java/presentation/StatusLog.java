package presentation;

import domain.game.GameEvent;

import java.util.ArrayDeque;
import java.util.Deque;

public class StatusLog {
    private final Deque<String> messages = new ArrayDeque<>();
    private final int maxLines;

    public StatusLog(int maxLines) {
        this.maxLines = maxLines;
    }

    public void add(GameEvent e) {
        messages.addFirst(EventFormatter.format(e));
        while (messages.size() > maxLines)
            messages.removeLast();
    }

    public Iterable<String> getLines() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }
}
