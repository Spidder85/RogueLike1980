package presentation;

import domain.Level;
import domain.character.Character;
import jcurses.system.Toolkit;
import jcurses.system.CharColor;

import java.io.File;

public class GameApplication {

    static {
        // Определяем имя библиотеки в зависимости от ОС
        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();
        String bitness = arch.contains("64") ? "64" : "32";
        String libFile;

        try {
            System.loadLibrary("jcurses" + bitness);
        } catch (UnsatisfiedLinkError e) {
            throw new RuntimeException("" +
                    "Не удалось загрузить native-библиотеку jcurses64.\n" +
                    "OS: " + os + "\n" +
                    "ARCH: " + arch + "\n" +
                    "java.library.path: " + System.getProperty("java.library.path"),
                    e
            );
        }

//        if (os.contains("win")) {
//            libFile = "jcurses" + bitness + ".dll";
//        } else if (os.contains("mac")) {
//            libFile = "libjcurses" + bitness + ".dylib";
//        } else if (os.contains("nux") || os.contains("nix")) {
//            libFile = "libjcurses" + bitness + ".so";
//        } else {
//            throw new UnsupportedOperationException("Unsupported OS: " + os);
//        }
//
//        // Получаем путь к папке с jar или классами
//        File classPath = new File(GameApplication.class
//                .getProtectionDomain()
//                .getCodeSource()
//                .getLocation()
//                .getPath())
//                .getParentFile(); // папка с jar или classes
//
//        // Формируем путь к библиотеке
//        File lib = new File(classPath, libFile);
//
//        if (!lib.exists()) {
//            throw new RuntimeException("Native library not found: " + lib.getAbsolutePath());
//        }
//        // Загружаем нативную библиотеку
//        System.load(lib.getAbsolutePath());
    }

    public static void main(String[] args) {
        // init JCurses
        try {
            System.out.println("BEFORE INIT");
            // инициализация терминала
            Toolkit.init();
            System.out.println("AFTER INIT");

            Level level = new Level(1);
            level.generate();

            Level.Position start = level.getStartPosition();

            Character player = new Character(
                    100,
                    100,
                    10,
                    10
            );
            player.setPosition(start.x, start.y);

            GameScreen screen = new GameScreen(level, player);
            screen.run();
        } finally {
            // Обязательно восстанавливаем терминал
            Toolkit.shutdown();
        }



        // create GameEngine
        // create GameScreen
        // main loop
    }
}
