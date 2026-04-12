/**
 * Виняток, що виникає при передачі некоректних даних у клас {@link Employee}.
 * Розширює {@link IllegalArgumentException}.
 */
public class InvalidEmployeeDataException extends IllegalArgumentException {

    /**
     * Створює виняток із повідомленням про помилку.
     *
     * @param message опис помилки валідації
     */
    public InvalidEmployeeDataException(String message) {
        super(message);
    }

    /**
     * Створює виняток із повідомленням і першопричиною.
     *
     * @param message опис помилки валідації
     * @param cause   початкова причина виникнення помилки
     */
    public InvalidEmployeeDataException(String message, Throwable cause) {
        super(message, cause);
    }
}