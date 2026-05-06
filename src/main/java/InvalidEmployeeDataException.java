/**
 * Виняток, що виникає при передачі некоректного значення поля
 * у сеттери або конструктори класів предметної області.
 */
public class InvalidEmployeeDataException extends IllegalArgumentException {

    /** Назва поля, яке містить некоректне значення. */
    private final String fieldName;

    /**
     * Створює виняток із назвою поля та повідомленням про помилку.
     *
     * @param fieldName назва некоректного поля
     * @param message   опис помилки валідації
     */
    public InvalidEmployeeDataException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    /** @return назва поля, що спричинило виняток */
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getMessage() {
        return "[Поле: " + fieldName + "] " + super.getMessage();
    }
}