
/**
 * Клас-стартер для JavaFX.
 * Окремий клас потрібен, щоб fat-jar коректно запускався
 * без помилки "JavaFX runtime components are missing".
 */
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}