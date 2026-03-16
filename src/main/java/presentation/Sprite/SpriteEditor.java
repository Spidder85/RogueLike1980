package presentation.Sprite;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.*;

public class SpriteEditor {

    private AsciiSprite sprite = new AsciiSprite(16, 16);

    private int cursorX = 0;
    private int cursorY = 0;

    private int scrollX = 0;
    private int scrollY = 0;

    private char currentGlyph = '█';
    private int currentFG = 7;
    private int currentBG = 0;

    private static final char[] GLYPH_PRESETS = {'█', '▓', '▒', '░'};
    private static final TextColor[] PALETTE = {//TextColor.ANSI.values();
            new TextColor.RGB(0, 0, 0),//TextColor.ANSI.BLACK;
            new TextColor.RGB(0,0,170),//.ANSI.BLUE;
            new TextColor.RGB(0,170,0),//.ANSI.GREEN;
            new TextColor.RGB(0,170,170),//TextColor.ANSI.CYAN;
            new TextColor.RGB(170,0,0),// .ANSI.RED;
            new TextColor.RGB(170,0,170),//TextColor.ANSI.MAGENTA;
            new TextColor.RGB(170,170,0),//TextColor.ANSI.YELLOW;
            new TextColor.RGB(170,170,170),//.ANSI.WHITE;
            new TextColor.RGB(85,85,85),//TextColor.ANSI.BLACK_BRIGHT;
            new TextColor.RGB(85,85,255),//TextColor.ANSI.BLUE_BRIGHT;
            new TextColor.RGB(85,255,85),//TextColor.ANSI.GREEN_BRIGHT;
            new TextColor.RGB(85,255,255),//TextColor.ANSI.CYAN_BRIGHT;
            new TextColor.RGB(255,85,85),//TextColor.ANSI.RED_BRIGHT;
            new TextColor.RGB(255,85,255),//TextColor.ANSI.MAGENTA_BRIGHT;
            new TextColor.RGB(255,255,85),//TextColor.ANSI.YELLOW_BRIGHT;
            new TextColor.RGB(255,255,255),//TextColor.ANSI.WHITE_BRIGHT;
            new TextColor.RGB(129,143,111),
            new TextColor.RGB(78,114,70),
            new TextColor.RGB(112,64,17), // brown
            new TextColor.RGB(153,103,47),
            new TextColor.RGB(255,170,0),   // orange
            new TextColor.RGB(220,204,207)
};

    private int canvasW = 40;
    private int canvasH = 25;

    private int zoom = 2;
    private int offsetX = 1;
    private int offsetY = 9;

    private String currentFile = "sprite.spr";

    private boolean resizing = false;
    private String resizeInput = "";

    public void run() throws Exception {
        Screen screen = new DefaultTerminalFactory()
                .setInitialTerminalSize(new TerminalSize(100, 50))
                .setForceAWTOverSwing(true)
                .createScreen();

        screen.startScreen();
        screen.setCursorPosition(null);

        TextGraphics g = screen.newTextGraphics();
        int paletteSize = PALETTE.length;

        while (true) {
            // INPUT
            KeyStroke key = screen.pollInput();

            if (key != null) {
                if (resizing) {
                    if (key.getKeyType() == KeyType.Escape) {
                        resizing = false;
                        resizeInput = "";
                        continue;
                    }

                    if (key.getKeyType() == KeyType.Enter) {

                        String[] parts = resizeInput.split("x");

                        if (parts.length == 2) {
                            try {
                                int w = Integer.parseInt(parts[0]);
                                int h = Integer.parseInt(parts[1]);

                                if (w > 0 && h > 0 && w <= 256 && h <= 256) {
                                    sprite = new AsciiSprite(w, h);
                                    cursorX = cursorY = scrollX = scrollY = 0;
                                }
                            } catch (Exception ignored) {}
                        }

                        resizing = false;
                        resizeInput = "";
                        continue;
                    }

                    if (key.getKeyType() == KeyType.Backspace) {
                        if (!resizeInput.isEmpty())
                            resizeInput = resizeInput.substring(0, resizeInput.length() - 1);
                        continue;
                    }

                    if (key.getKeyType() == KeyType.Character) {
                        char c = key.getCharacter();
                        if (Character.isDigit(c) || c == 'x')
                            resizeInput += c;
                        continue;
                    }

                    continue;
                }
                boolean shift = key.isShiftDown();
                boolean alt = key.isAltDown();

                if (key.getKeyType() == KeyType.Escape) break;
                // === CURSOR / SCROLL ===
                int tilesX = tilesX();
                int tilesY = tilesY();

                if (!shift) {
                    if (key.getKeyType() == KeyType.ArrowLeft)  cursorX--;
                    if (key.getKeyType() == KeyType.ArrowRight) cursorX++;
                    if (key.getKeyType() == KeyType.ArrowUp)    cursorY--;
                    if (key.getKeyType() == KeyType.ArrowDown)  cursorY++;

                    // автоскролл по X
                    if (cursorX < 0) {
                        if (scrollX > 0) scrollX--;
                        cursorX = 0;
                    }
                    if (cursorX >= tilesX) {
                        if (scrollX < sprite.width - tilesX) scrollX++;
                        cursorX = tilesX - 1;
                    }

                    // автоскролл по Y
                    if (cursorY < 0) {
                        if (scrollY > 0) scrollY--;
                        cursorY = 0;
                    }
                    if (cursorY >= tilesY) {
                        if (scrollY < sprite.height - tilesY) scrollY++;
                        cursorY = tilesY - 1;
                    }
                } else {
                    if (key.getKeyType() == KeyType.ArrowUp) scrollY--;
                    if (key.getKeyType() == KeyType.ArrowDown) scrollY++;
                    if (key.getKeyType() == KeyType.ArrowLeft) scrollX--;
                    if (key.getKeyType() == KeyType.ArrowRight) scrollX++;
                }

                // zoom
                if (key.getKeyType() == KeyType.PageUp) zoom = Math.min(zoom +1, 8);
                if (key.getKeyType() == KeyType.PageDown) zoom = Math.max(zoom -1, 1);

                // clamp
                int maxCursorX = Math.min(tilesX - 1, sprite.width  - 1 - scrollX);
                int maxCursorY = Math.min(tilesY - 1, sprite.height - 1 - scrollY);
                scrollX = clamp(scrollX, 0, Math.max(0, sprite.width - tilesX));
                scrollY = clamp(scrollY, 0, Math.max(0, sprite.height - tilesY));
                cursorX = clamp(cursorX, 0, Math.max(0, maxCursorX));
                cursorY = clamp(cursorY, 0, Math.max(0, maxCursorY));

                // === DRAW ===
                if (key.getKeyType() == KeyType.Character &&
                        key.getCharacter() == ' ') {
                    int sx = cursorX + scrollX;
                    int sy = cursorY + scrollY;
                    sprite.set(sx, sy, currentGlyph, packColor(currentFG, currentBG));
                }
                // стереть
                if (key.getKeyType() == KeyType.Delete) {
                    int sx = cursorX + scrollX;
                    int sy = cursorY + scrollY;
                    sprite.set(sx, sy, ' ', (short) 0);
                }

                // === GLYPH PRESETS ===
                if (key.getKeyType() == KeyType.F1) currentGlyph = GLYPH_PRESETS[0];
                if (key.getKeyType() == KeyType.F2) currentGlyph = GLYPH_PRESETS[1];
                if (key.getKeyType() == KeyType.F3) currentGlyph = GLYPH_PRESETS[2];
                if (key.getKeyType() == KeyType.F4) currentGlyph = GLYPH_PRESETS[3];

                // === COLOR ===
                if (key.getKeyType() == KeyType.Tab) {
                    if (alt) {
                        currentBG = shift ? (currentBG + paletteSize-1) % paletteSize : (currentBG + 1) % paletteSize;
                    } else {
                        currentFG = shift ? (currentFG + paletteSize-1) % paletteSize : (currentFG + 1) % paletteSize;
                    }
                }
                if (key.getKeyType() == KeyType.Character) {
                    if (key.getCharacter() == 'q') currentFG = (currentFG + paletteSize-1) % paletteSize;
                    if (key.getCharacter() == 'a') currentFG = (currentFG + 1) % paletteSize;
                    if (key.getCharacter() == 'w') currentBG = (currentBG + paletteSize-1) % paletteSize;
                    if (key.getCharacter() == 's') currentBG = (currentBG + 1) % paletteSize;
                }

                // === SAVE / LOAD ===
                if (key.getKeyType() == KeyType.F9) {
                    sprite = new AsciiSprite(currentFile);
                    cursorX = cursorY = 0;
                }

                if (key.getKeyType() == KeyType.F10)
                    sprite.save(currentFile);

                // === NEW SPRITE ===
                if (key.getKeyType() == KeyType.F5) {
                    sprite = new AsciiSprite(32, 32);
                    cursorX = cursorY = scrollX = scrollY = 0;
                }

                // смена глифа
                if (key.getKeyType() == KeyType.Character) {
                    char c = key.getCharacter();
                    if (c > 32) currentGlyph = c;
                }

                if (key.getKeyType() == KeyType.F6) {
                    resizing = true;
                    resizeInput = "";
                }
                if (key.getKeyType() == KeyType.F12) {
                    break;
                }
            }

            /* ******************** */
            /*        RENDER        */
            /* ******************** */
            //g.setForegroundColor(TextColor.ANSI.WHITE);
            g.setBackgroundColor(TextColor.ANSI.BLACK);
            g.fill(' ');

            drawCanvas(g);
            drawPalette(g);
            drawPreview(g);
            drawUI(g);

            screen.refresh();
        }

        screen.stopScreen();
    }

    private void drawCanvas(TextGraphics g) {
        g.setForegroundColor(TextColor.ANSI.WHITE);
        g.putString(offsetX+7, offsetY-1, "SCALE: " + zoom + ":1");
        drawFrame(offsetX, offsetX + canvasW + 1,
                  offsetY, offsetY + canvasH + 1, g);
        int clipL = offsetX + 1;
        int clipT = offsetY + 1;
        int clipR = clipL + canvasW;
        int clipB = clipT + canvasH;

        int tilesX = tilesX();
        int tilesY = tilesY();

        for (int y = 0; y <= tilesY; y++) {
            for (int x = 0; x <= tilesX; x++) {
                int sx = x + scrollX;
                int sy = y + scrollY;

                int dx = x * zoom + offsetX + 1;
                int dy = y * zoom + offsetY + 1;

                char glyph = ' ';
                int fg = 7;
                int bg = 0;

                if (sx >= 0 && sy >= 0 && sx < sprite.width && sy < sprite.height) {
                    glyph = sprite.getGlyph(sx, sy);
                    fg = sprite.getFG(sx, sy);
                    bg = sprite.getBG(sx, sy);
                } else
                    continue;

                for (int zy = 0; zy < zoom; zy++) {
                    for (int zx = 0; zx < zoom; zx++){
                        int px = dx + zx;
                        int py = dy + zy;

                        if (px < clipL || px >= clipR || py < clipT || py >= clipB)
                            continue;

                        if (glyph == ' ') {
                            g.setForegroundColor(TextColor.ANSI.WHITE);
                            g.setCharacter(px, py, '.');
                        } else {
                            g.setForegroundColor(PALETTE[fg]);
                            g.setBackgroundColor(PALETTE[bg]);
                            g.setCharacter(px, py, glyph);
                            g.setBackgroundColor(TextColor.ANSI.BLACK);
                        }
                    }
                }
            }
        }

        // курсор
        int cx = cursorX;
        int cy = cursorY;

        if (cx >= 0 && cy >= 0 && cx < canvasW && cy < canvasH) {
            int dx = offsetX + 1 + cx * zoom;
            int dy = offsetY + 1 + cy * zoom;

            g.setForegroundColor(TextColor.ANSI.WHITE);
            for (int zy = 0; zy < zoom; zy++)
                for (int zx= 0; zx < zoom; zx++) {
                    int px = dx + zx;
                    int py = dy + zy;
                    if (px < clipL || px >= clipR || py < clipT || py >= clipB)
                        continue;
                    g.setCharacter(px, py, 'O');
                }
        }
    }

    private void drawFrame(int x1, int x2, int y1, int y2, TextGraphics g) {
        g.setForegroundColor(TextColor.ANSI.YELLOW);
        g.setBackgroundColor(TextColor.ANSI.BLACK);

        if (x1 > x2) {
            int tmp = x1;
            x1 = x2;
            x2 = tmp;
        }
        if (y1 > y2) {
            int tmp = y1;
            y1 = y2;
            y2 = tmp;
        }
        g.setCharacter(x1, y1, '╔');
        g.setCharacter(x2, y1, '╗');
        g.setCharacter(x1, y2, '╚');
        g.setCharacter(x2, y2, '╝');
        for (int i = x1 + 1; i < x2; i++) {
            g.setCharacter(i, y1, '═');
            g.setCharacter(i, y2, '═');
        }
        for (int i = y1 + 1; i < y2; i++) {
            g.setCharacter(x1, i, '║');
            g.setCharacter(x2, i, '║');
        }
    }

    private void drawPalette(TextGraphics g) {
        int px = canvasW + 8;
        int paletteSize = PALETTE.length;
        for (int i = 0; i < paletteSize; i++) {
            g.setForegroundColor(PALETTE[i]);
            g.setCharacter(px+1, i + 1 + offsetY, '█');

            if (i == currentFG) {
                g.setForegroundColor(TextColor.ANSI.WHITE);
                g.putString(px + 3, i + offsetY + 1, "< FG");
            }
            if (i == currentBG) {
                g.setForegroundColor(TextColor.ANSI.WHITE);
                g.putString(px - 4, i + offsetY + 1, "BG >");
            }
        }
    }

    private void drawPreview(TextGraphics g) {
        int py = offsetY; //canvasH + 3 + offsetY;
        int px = canvasW + 15 + offsetX;
        g.setForegroundColor(TextColor.ANSI.WHITE);
        g.putString(px+7, py-1, "PREVIEW 1:1");
        drawFrame(px, px + canvasW + 1,
                py, py + canvasH + 1, g);
        for (int y = 0; y < sprite.height && y < canvasH; y++) {
            for (int x = 0; x < sprite.width && x < canvasW; x++) {

                char glyph = sprite.getGlyph(x, y);
                int fg = sprite.getFG(x, y);
                int bg = sprite.getBG(x, y);

                if (glyph == ' ') {
                    g.setForegroundColor(TextColor.ANSI.WHITE);
                    g.setCharacter(x + px+1, y + py+1, '.');
                } else {
                    g.setForegroundColor(PALETTE[fg]);
                    g.setBackgroundColor(PALETTE[bg]);
                    g.setCharacter(px + x+1, py + y+1, glyph);
                    //g.setBackgroundColor(TextColor.ANSI.BLACK);
                }
            }
        }
    }

    private void drawUI(TextGraphics g) {
        int y = 0;
        g.setForegroundColor(TextColor.ANSI.WHITE);

        int sx = cursorX + scrollX;
        int sy = cursorY + scrollY;

        g.putString(1, y++, "ARROWS move | SHIFT+ARROWS scroll | SPACE draw | DEL erase| TAB/Q/A FG | W/S BG | PgUp/PgDn zoom");
        g.putString(1, y++, "F1-F4 glyph | F5 new | F6 resize | F9 load | F10 save | F12 exit");

        g.putString(1, y++, String.format(
                "Glyph:%c FG:%d BG:%d Zoom:%dx",
                currentGlyph, currentFG, currentBG, zoom));

        g.putString(1, y++, String.format(
                "Cursor(view): %d,%d  Cursor(sprite): %d,%d",
                cursorX, cursorY, sx, sy));

        g.putString(1, y++, String.format(
                "Scroll: %d,%d  View tiles: %dx%d",
                scrollX, scrollY, tilesX(), tilesY()));

        g.putString(1, y++, String.format(
                "Sprite: %dx%d  File: %s",
                sprite.width, sprite.height, currentFile));

        if (resizing) {
            g.setForegroundColor(TextColor.ANSI.YELLOW);
            g.putString(1, y++, "RESIZE MODE: type WxH and press ENTER");
            g.putString(1, y++, "Input: " + resizeInput);
        }
    }

    /* ******************** */
    /*         UTILS        */
    /* ******************** */

    private short packColor(int fg, int bg) {
        //return (short) ((bg << 4) | (fg & 0x0F));
        return (short) ((bg << 8) | (fg & 0xFF));
    }

    private int clamp(int v, int min, int max) {
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    private int tilesX() {return canvasW / zoom; }
    private int tilesY() {return canvasH / zoom; }

    public static void main(String[] args) throws Exception {
        new SpriteEditor().run();
    }

    public static TextColor mapColor(int color) {
        return PALETTE[color];
    }
}