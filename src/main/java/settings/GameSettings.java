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
    public static final int MAX_HEALTH = 100;
    public static final int INITIAL_AGILITY = 20;
    public static final int INITIAL_STRENGTH = 20;

    // уровни / отображение
    public static final int MAX_LEVELS = 21;
    public static final int FOG_RADIUS = 10;

    public static final boolean ENABLE_FOG_OF_WAR = true; // вкл/выкл "Туман войны"

    //public static final boolean ICON_MODE = false;    // отображение текстовых символов / иконок

    public static final int STATUS_LOG_SIZE = 8;

    private GameSettings() {}
}
