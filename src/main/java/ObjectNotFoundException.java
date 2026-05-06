/**
 * Виняток, що виникає при спробі виконати операцію над об'єктом,
 * якого не існує в колекції.
 */
public class ObjectNotFoundException extends RuntimeException {

    /**
     * Створює виняток із повідомленням про помилку.
     *
     * @param message опис помилки
     */
    public ObjectNotFoundException(String message) {
        super(message);
    }

}