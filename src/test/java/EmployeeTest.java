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
        ContactInfo contact = new ContactInfo("ivan@example.com", "+380501234567");
        Employee emp = new Employee("Іваненко Іван", 30, 20000.0, 5,
                Position.DEVELOPER, Department.IT, contact);
        assertAll(
                () -> assertEquals("Іваненко Іван", emp.getName()),
                () -> assertEquals(30, emp.getAge()),
                () -> assertEquals(20000.0, emp.getSalary()),
                () -> assertEquals(5, emp.getExperience()),
                () -> assertEquals(Position.DEVELOPER, emp.getPosition()),
                () -> assertEquals(Department.IT, emp.getDepartment())
        );
    }

    /** Конструктор кидає виняток, якщо ПІБ порожній. */
    @Test
    void shouldThrowWhenNameIsBlank() {
        ContactInfo contact = new ContactInfo("ivan@example.com", "+380501234567");
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee("   ", 30, 20000.0, 5,
                        Position.DEVELOPER, Department.IT, contact)
        );
    }

    /** Конструктор кидає виняток, якщо ПІБ null. */
    @Test
    void shouldThrowWhenNameIsNull() {
        ContactInfo contact = new ContactInfo("ivan@example.com", "+380501234567");
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee(null, 30, 20000.0, 5,
                        Position.DEVELOPER, Department.IT, contact)
        );
    }

    /** Конструктор кидає виняток, якщо вік менше 18. */
    @Test
    void shouldThrowWhenAgeTooLow() {
        ContactInfo contact = new ContactInfo("petro@example.com", "+380501234567");
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee("Петренко Петро", 17, 20000.0, 0,
                        Position.INTERN, Department.HR, contact)
        );
    }

    /** Конструктор кидає виняток, якщо вік більше 65. */
    @Test
    void shouldThrowWhenAgeTooHigh() {
        ContactInfo contact = new ContactInfo("sydir@example.com", "+380501234567");
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee("Сидоренко Сидір", 66, 20000.0, 10,
                        Position.MANAGER, Department.FINANCE, contact)
        );
    }

    /** Конструктор кидає виняток, якщо зарплата дорівнює нулю. */
    @Test
    void shouldThrowWhenSalaryIsZero() {
        ContactInfo contact = new ContactInfo("olena@example.com", "+380501234567");
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee("Коваль Олена", 25, 0.0, 2,
                        Position.ANALYST, Department.ANALYTICS, contact)
        );
    }

    /** Конструктор кидає виняток, якщо зарплата від'ємна. */
    @Test
    void shouldThrowWhenSalaryIsNegative() {
        ContactInfo contact = new ContactInfo("olena@example.com", "+380501234567");
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee("Коваль Олена", 25, -500.0, 2,
                        Position.ANALYST, Department.ANALYTICS, contact)
        );
    }

    /** Конструктор кидає виняток, якщо стаж від'ємний. */
    @Test
    void shouldThrowWhenExperienceIsNegative() {
        ContactInfo contact = new ContactInfo("tom@example.com", "+380501234567");
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee("Мельник Том", 28, 15000.0, -1,
                        Position.DESIGNER, Department.UX, contact)
        );
    }

    /** Конструктор кидає виняток, якщо стаж перевищує (вік - 18). */
    @Test
    void shouldThrowWhenExperienceExceedsAgeLimit() {
        ContactInfo contact = new ContactInfo("young@example.com", "+380501234567");
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee("Молодий Спец", 20, 10000.0, 5,
                        Position.INTERN, Department.IT, contact)
        );
    }

    /** Конструктор кидає виняток, якщо посада null. */
    @Test
    void shouldThrowWhenPositionIsBlank() {
        ContactInfo contact = new ContactInfo("maria@example.com", "+380501234567");
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee("Марія К.", 35, 25000.0, 10,
                        null, Department.HR, contact)
        );
    }

    /** Конструктор кидає виняток, якщо відділ null. */
    @Test
    void shouldThrowWhenDepartmentIsNull() {
        ContactInfo contact = new ContactInfo("maria@example.com", "+380501234567");
        assertThrows(InvalidEmployeeDataException.class, () ->
                new Employee("Марія К.", 35, 25000.0, 10,
                        Position.MANAGER, null, contact)
        );
    }

    // ==================== Тести сеттерів ====================

    /** setSalary кидає виняток при від'ємному значенні. */
    @Test
    void shouldThrowWhenSetSalaryIsNegative() {
        ContactInfo contact = new ContactInfo("test@example.com", "+380501234567");
        Employee emp = new Employee("Тест Тестович", 30, 10000.0, 5,
                Position.DEVELOPER, Department.IT, contact);
        assertThrows(InvalidEmployeeDataException.class, () ->
                emp.setSalary(-1)
        );
    }

    /** setAge кидає виняток при некоректному віці. */
    @Test
    void shouldThrowWhenSetAgeIsInvalid() {
        ContactInfo contact = new ContactInfo("test@example.com", "+380501234567");
        Employee emp = new Employee("Тест Тестович", 30, 10000.0, 5,
                Position.DEVELOPER, Department.IT, contact);
        assertThrows(InvalidEmployeeDataException.class, () ->
                emp.setAge(15)
        );
    }

    /** setName кидає виняток при порожньому рядку. */
    @Test
    void shouldThrowWhenSetNameIsBlank() {
        ContactInfo contact = new ContactInfo("test@example.com", "+380501234567");
        Employee emp = new Employee("Тест Тестович", 30, 10000.0, 5,
                Position.DEVELOPER, Department.IT, contact);
        assertThrows(InvalidEmployeeDataException.class, () ->
                emp.setName("")
        );
    }

    /** setExperience кидає виняток при від'ємному стажі. */
    @Test
    void shouldThrowWhenSetExperienceIsNegative() {
        ContactInfo contact = new ContactInfo("test@example.com", "+380501234567");
        Employee emp = new Employee("Тест Тестович", 30, 10000.0, 5,
                Position.DEVELOPER, Department.IT, contact);
        assertThrows(InvalidEmployeeDataException.class, () ->
                emp.setExperience(-3)
        );
    }

    /** setPosition кидає виняток при null. */
    @Test
    void shouldThrowWhenSetPositionIsNull() {
        ContactInfo contact = new ContactInfo("test@example.com", "+380501234567");
        Employee emp = new Employee("Тест Тестович", 30, 10000.0, 5,
                Position.DEVELOPER, Department.IT, contact);
        assertThrows(InvalidEmployeeDataException.class, () ->
                emp.setPosition(null)
        );
    }

    /** setDepartment кидає виняток при порожньому рядку. */
    @Test
    void shouldThrowWhenSetDepartmentIsBlank() {
        ContactInfo contact = new ContactInfo("test@example.com", "+380501234567");
        Employee emp = new Employee("Тест Тестович", 30, 10000.0, 5,
                Position.DEVELOPER, Department.IT, contact);
        assertThrows(InvalidEmployeeDataException.class, () ->
                emp.setDepartment(null)
        );
    }

    /** Коректні значення сеттерів оновлюють поля без помилок. */
    @Test
    void shouldUpdateFieldsWithValidValues() {
        ContactInfo contact = new ContactInfo("ivan@example.com", "+380501234567");
        Employee emp = new Employee("Іван І.", 40, 30000.0, 10,
                Position.LEAD, Department.ENGINEERING, contact);
        emp.setSalary(35000.0);
        emp.setName("Іван Оновлений");
        assertEquals(35000.0, emp.getSalary());
        assertEquals("Іван Оновлений", emp.getName());
    }
}