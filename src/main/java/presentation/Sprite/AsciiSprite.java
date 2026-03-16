package presentation.Sprite;

import java.io.*;

public class AsciiSprite {

    public final int width;
    public final int height;

    private final char[] glyphs;
    private final short[] colors; // raw: BG<<4 | FG

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    // создать пустой (для редактора)
    public AsciiSprite(int w, int h) {
        this.width = w;
        this.height = h;

        int size = w * h;

        glyphs = new char[size];
        colors = new short[size];

        for (int i = 0; i < size; i++) {
            glyphs[i] = ' ';
            colors[i] = 15;
        }
    }

    // загрузить из файла (для движка и редактора)
    public AsciiSprite(String path) throws IOException {

        try (DataInputStream in = new DataInputStream(new FileInputStream(path))) {

            width = Integer.reverseBytes(in.readInt());
            height = Integer.reverseBytes(in.readInt());

            int size = width * height;

            colors = new short[size];
            glyphs = new char[size];

            // цвета
            for (int i = 0; i < size; i++)
                colors[i] = Short.reverseBytes(in.readShort());

            // глифы
            for (int i = 0; i < size; i++) {
                short g = Short.reverseBytes(in.readShort());
                if (g < 32) g = 32; // защита от control chars
                glyphs[i] = (char) g;
            }
        }
    }

    // =========================================================
    // SAVE / LOAD
    // =========================================================

    public void save(String path) throws IOException {

        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(path))) {

            out.writeInt(Integer.reverseBytes(width));
            out.writeInt(Integer.reverseBytes(height));

            for (short c : colors)
                out.writeShort(Short.reverseBytes(c));

            for (char g : glyphs)
                out.writeShort(Short.reverseBytes((short) g));
        }
    }

    // =========================================================
    // EDITOR METHODS
    // =========================================================

    public void setGlyph(int x, int y, char g) {
        if (!inBounds(x, y)) return;
        glyphs[y * width + x] = g;
    }

    public void setColor(int x, int y, short c) {
        if (!inBounds(x, y)) return;
        colors[y * width + x] = c;
    }

    public void set(int x, int y, char g, short c) {
        if (!inBounds(x, y)) return;
        int i = y * width + x;
        glyphs[i] = g;
        colors[i] = c;
    }

    // =========================================================
    // BASIC GETTERS
    // =========================================================

    public char getGlyph(int x, int y) {
        if (!inBounds(x, y)) return ' ';
        return glyphs[y * width + x];
    }

    public short getColorRaw(int x, int y) {
        if (!inBounds(x, y)) return 0;
        return colors[y * width + x];
    }

    public int getFG(int x, int y) {
        return getColorRaw(x, y) & 0xFF;
    }

    public int getBG(int x, int y) {
        return (getColorRaw(x, y) >> 8) & 0xFF;
    }

    // =========================================================
    // SAMPLING 0.0 – 1.0 (для raycaster)
    // =========================================================

    public char sampleGlyph(double u, double v) {
        int x = clamp((int) (u * width), 0, width - 1);
        int y = clamp((int) (v * height), 0, height - 1);
        return glyphs[y * width + x];
    }

    public short sampleColorRaw(double u, double v) {
        int x = clamp((int) (u * width), 0, width - 1);
        int y = clamp((int) (v * height), 0, height - 1);
        return colors[y * width + x];
    }

    public int sampleFG(double u, double v) {
        return sampleColorRaw(u, v) & 0xFF;
    }

    public int sampleBG(double u, double v) {
        return (sampleColorRaw(u, v) >> 8) & 0xFF;
    }

    // =========================================================
    // UTILS
    // =========================================================

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private int clamp(int v, int min, int max) {
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }
}