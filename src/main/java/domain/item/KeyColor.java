package domain.item;

import com.googlecode.lanterna.TextColor;

public enum KeyColor {
    RED(1),
    BLUE(2),
    GREEN(4),
    YELLOW(8),
    ORANGE(16),
    PURPLE(32);

    private final int bit;

    KeyColor(int bit) {
        this.bit = bit;
    }

    public int bit() {
        return bit;
    }

    public TextColor toColor() {
        return switch (this) {
            case RED -> new TextColor.RGB(255,0,0);
            case BLUE -> new TextColor.RGB(0,0,255);
            case GREEN -> new TextColor.RGB(0,255,0);
            case YELLOW -> new TextColor.RGB(255,255,0);
            case ORANGE -> new TextColor.RGB(255,165,0);
            case PURPLE -> new TextColor.RGB(128,0,128);
        };
    }
}
