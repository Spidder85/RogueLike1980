package settings;

public final class GameSettings {
    // === размеры карты ===
    public static final int GAME_WIDTH = 80;
    public static final int GAME_HEIGHT = 40;

    // === генерация комнат ===
    public static final int COUNT_ROOMS = 9;
    public static final int MIN_ROOM_DISTANCE = 3;
    public static final int MIN_ROOM_WIDTH = 8;
    public static final int MIN_ROOM_HEIGHT = 5;

    // === наполнение ===
    public static final int MAX_COUNT_ITEM_IN_ROOM = 3;
    public static final int MAX_COUNT_ENEMIES_IN_ROOM = 3;

    // === игрок ===
    public static final int MAX_HEALTH = 80;
    public static final int INITIAL_AGILITY = 12;
    public static final int INITIAL_STRENGTH = 10;

    // уровни / отображение
    public static final int MAX_LEVELS = 21;
    public static final int FOG_RADIUS = 10;

    public static boolean ENABLE_FOG_OF_WAR = true; // вкл/выкл "Туман войны"

    //public static final boolean ICON_MODE = false;    // отображение текстовых символов / иконок

    public static final int STATUS_LOG_SIZE = 10;
    public static final int STATS_PANEL_WIDTH = 40;

    public static final boolean ENABLE_KEY = true;

    // настройки для FIRST_PERSON_VIEW
    public static final double FP_STEP = 0.1;          // шаг луча // был 0,5 в тумане
    public static final double FP_ROTATION = 11.25; // угол поворота (в градусах) луча - 32 шага полный оборот
    public static final double FP_FOV = Math.PI / 3;  // угол обзора - 60°
    public static final double MINIMAP_SCALE = 0.5;    // масштаб карты - 1/3 от оригинала

    public static final double COLLISION_BOX_SIZE = 0.5;

    private GameSettings() {}

}
