import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Автотести для перевірки власних винятків у класі {@link Company}.
 * Перевіряються {@link ObjectNotFoundException} та {@link InvalidEmployeeDataException}.
 */
class CompanyExceptionTest {

    private Company company;
    private FullTimeEmployee employee;

    /**
     * Ініціалізація тестового середовища перед кожним тестом.
     * Створюємо компанію з одним доданим працівником.
     */
    @BeforeEach
    void setUp() {
        company = new Company("TechCorp", "IT", 2010);
        employee = new FullTimeEmployee(
                "Франко", 30, 50000, 5,
                Position.DEVELOPER, Department.IT, 15.0
        );
        company.addNewEmployee(employee, 1);
    }

    // Тести ObjectNotFoundException

    /**
     * delete() має кидати ObjectNotFoundException,
     * якщо працівника з таким UUID немає в колекції.
     */
    @Test
    void shouldThrowObjectNotFoundExceptionWhenDeletingNonExistingEmployee() {
        FullTimeEmployee ghost = new FullTimeEmployee(
                "Левко", 28, 45000, 3,
                Position.ANALYST, Department.ANALYTICS, 10.0
        );
        // ghost не додано до company — очікуємо ObjectNotFoundException
        assertThrows(ObjectNotFoundException.class, () -> company.delete(ghost));
    }

    /**
     * update() має кидати ObjectNotFoundException,
     * якщо працівника з таким UUID немає в колекції.
     */
    @Test
    void shouldThrowObjectNotFoundExceptionWhenUpdatingNonExistingEmployee() {
        FullTimeEmployee ghost = new FullTimeEmployee(
                "Шевченко", 35, 60000, 8,
                Position.LEAD, Department.IT, 20.0
        );
        FullTimeEmployee updated = new FullTimeEmployee(
                "Шевченко", 35, 65000, 8,
                Position.LEAD, Department.IT, 20.0
        );
        // ghost не додано до company — очікуємо ObjectNotFoundException
        assertThrows(ObjectNotFoundException.class, () -> company.update(ghost, updated));
    }

    // Тести InvalidEmployeeDataException

    /**
     * addNewEmployee() має кидати InvalidEmployeeDataException,
     * якщо кількість менша за 1.
     */
    @Test
    void shouldThrowInvalidEmployeeDataExceptionWhenQuantityIsZero() {
        FullTimeEmployee emp = new FullTimeEmployee(
                "Іванов", 25, 30000, 2,
                Position.DESIGNER, Department.UX, 5.0
        );
        InvalidEmployeeDataException ex = assertThrows(
                InvalidEmployeeDataException.class,
                () -> company.addNewEmployee(emp, 0)
        );
        assertEquals("quantity", ex.getFieldName());
    }

    /**
     * Company конструктор має кидати InvalidEmployeeDataException,
     * якщо рік заснування менший за 1800.
     */
    @Test
    void shouldThrowInvalidEmployeeDataExceptionWhenFoundedYearIsTooOld() {
        InvalidEmployeeDataException ex = assertThrows(
                InvalidEmployeeDataException.class,
                () -> new Company("OldCorp", "Trade", 1799)
        );
        assertEquals("foundedYear", ex.getFieldName());
    }

    /**
     * update() має кидати InvalidEmployeeDataException,
     * якщо типи об'єктів різняться.
     */
    @Test
    void shouldThrowInvalidEmployeeDataExceptionWhenTypeMismatchInUpdate() {
        InternEmployee intern = new InternEmployee(
                "Франко", 30, 50000, 5,
                Position.DEVELOPER, Department.IT, "КНУ", 6
        );
        // employee — FullTimeEmployee, intern — InternEmployee: типи різні
        assertThrows(InvalidEmployeeDataException.class, () -> company.update(employee, intern));
    }

    /**
     * delete() з null має кидати InvalidEmployeeDataException.
     */
    @Test
    void shouldThrowInvalidEmployeeDataExceptionWhenDeletingNull() {
        assertThrows(InvalidEmployeeDataException.class, () -> company.delete(null));
    }

    // Позитивні тести (операції мають проходити без винятків)

    /**
     * delete() для існуючого працівника має повернути true без виняків.
     */
    @Test
    void shouldDeleteExistingEmployeeSuccessfully() {
        assertDoesNotThrow(() -> company.delete(employee));
        assertEquals(0, company.getRecords().size());
    }

    /**
     * update() для існуючого працівника має повернути true без виняків.
     */
    @Test
    void shouldUpdateExistingEmployeeSuccessfully() {
        FullTimeEmployee updated = new FullTimeEmployee(
                "Франко", 30, 55000, 5,
                Position.DEVELOPER, Department.IT, 15.0
        );
        // Підміняємо UUID через збіг — потрібно передати той самий об'єкт
        boolean result = assertDoesNotThrow(() -> company.update(employee, updated));
        assertTrue(result);
    }
}