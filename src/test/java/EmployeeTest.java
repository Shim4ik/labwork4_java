import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тести для класу {@link Employee}.
 * Перевіряють валідацію конструктора та сеттерів.
 */
class EmployeeTest {

    /** Коректні дані — об'єкт створюється без помилок. */
    @Test
    void shouldCreateEmployeeWithValidData() {
        Employee emp = new Employee("Іваненко Іван", 30, 20000.0, 5, "Розробник", "ІТ");
        assertAll(
                () -> assertEquals("Іваненко Іван", emp.getName()),
                () -> assertEquals(30, emp.getAge()),
                () -> assertEquals(20000.0, emp.getSalary()),
                () -> assertEquals(5, emp.getExperience()),
                () -> assertEquals("Розробник", emp.getPosition()),
                () -> assertEquals("ІТ", emp.getDepartment())
        );
    }

    /** Конструктор кидає виняток, якщо ПІБ порожній. */
    @Test
    void shouldThrowWhenNameIsBlank() {
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee("   ", 30, 20000.0, 5, "Розробник", "ІТ")
        );
    }

    /** Конструктор кидає виняток, якщо ПІБ null. */
    @Test
    void shouldThrowWhenNameIsNull() {
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee(null, 30, 20000.0, 5, "Розробник", "ІТ")
        );
    }

    /** Конструктор кидає виняток, якщо вік менше 18. */
    @Test
    void shouldThrowWhenAgeTooLow() {
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee("Петренко Петро", 17, 20000.0, 0, "Стажер", "HR")
        );
    }

    /** Конструктор кидає виняток, якщо вік більше 65. */
    @Test
    void shouldThrowWhenAgeTooHigh() {
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee("Сидоренко Сидір", 66, 20000.0, 10, "Менеджер", "Фінанси")
        );
    }

    /** Конструктор кидає виняток, якщо зарплата дорівнює нулю. */
    @Test
    void shouldThrowWhenSalaryIsZero() {
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee("Коваль Олена", 25, 0.0, 2, "Аналітик", "Аналітика")
        );
    }

    /** Конструктор кидає виняток, якщо зарплата від'ємна. */
    @Test
    void shouldThrowWhenSalaryIsNegative() {
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee("Коваль Олена", 25, -500.0, 2, "Аналітик", "Аналітика")
        );
    }

    /** Конструктор кидає виняток, якщо стаж від'ємний. */
    @Test
    void shouldThrowWhenExperienceIsNegative() {
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee("Мельник Том", 28, 15000.0, -1, "Дизайнер", "UX")
        );
    }

    /** Конструктор кидає виняток, якщо стаж перевищує (вік - 18). */
    @Test
    void shouldThrowWhenExperienceExceedsAgeLimit() {
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee("Молодий Спец", 20, 10000.0, 5, "Джуніор", "ІТ")
        );
    }

    /** Конструктор кидає виняток, якщо посада порожня. */
    @Test
    void shouldThrowWhenPositionIsBlank() {
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee("Марія К.", 35, 25000.0, 10, "", "HR")
        );
    }

    /** Конструктор кидає виняток, якщо відділ null. */
    @Test
    void shouldThrowWhenDepartmentIsNull() {
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee("Марія К.", 35, 25000.0, 10, "Менеджер", null)
        );
    }

    // ==================== Тести сеттерів ====================

    /** setSalary кидає виняток при від'ємному значенні. */
    @Test
    void shouldThrowWhenSetSalaryIsNegative() {
        Employee emp = new Employee("Тест Тестович", 30, 10000.0, 5, "Dev", "ІТ");
        assertThrows(InvalidEmployeeDataException.class, () ->
                emp.setSalary(-1)
        );
    }

    /** setAge кидає виняток при некоректному віці. */
    @Test
    void shouldThrowWhenSetAgeIsInvalid() {
        Employee emp = new Employee("Тест Тестович", 30, 10000.0, 5, "Dev", "ІТ");
        assertThrows(InvalidEmployeeDataException.class, () ->
                emp.setAge(15)
        );
    }

    /** setName кидає виняток при порожньому рядку. */
    @Test
    void shouldThrowWhenSetNameIsBlank() {
        Employee emp = new Employee("Тест Тестович", 30, 10000.0, 5, "Dev", "ІТ");
        assertThrows(InvalidEmployeeDataException.class, () ->
                emp.setName("")
        );
    }

    /** setExperience кидає виняток при від'ємному стажі. */
    @Test
    void shouldThrowWhenSetExperienceIsNegative() {
        Employee emp = new Employee("Тест Тестович", 30, 10000.0, 5, "Dev", "ІТ");
        assertThrows(InvalidEmployeeDataException.class, () ->
                emp.setExperience(-3)
        );
    }

    /** setPosition кидає виняток при null. */
    @Test
    void shouldThrowWhenSetPositionIsNull() {
        Employee emp = new Employee("Тест Тестович", 30, 10000.0, 5, "Dev", "ІТ");
        assertThrows(InvalidEmployeeDataException.class, () ->
                emp.setPosition(null)
        );
    }

    /** setDepartment кидає виняток при порожньому рядку. */
    @Test
    void shouldThrowWhenSetDepartmentIsBlank() {
        Employee emp = new Employee("Тест Тестович", 30, 10000.0, 5, "Dev", "ІТ");
        assertThrows(InvalidEmployeeDataException.class, () ->
                emp.setDepartment("   ")
        );
    }

    /** Коректні значення сеттерів оновлюють поля без помилок. */
    @Test
    void shouldUpdateFieldsWithValidValues() {
        Employee emp = new Employee("Іван І.", 40, 30000.0, 10, "Лід", "Інженерія");
        emp.setSalary(35000.0);
        emp.setName("Іван Оновлений");
        assertEquals(35000.0, emp.getSalary());
        assertEquals("Іван Оновлений", emp.getName());
    }
}