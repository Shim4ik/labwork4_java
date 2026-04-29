import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Головний клас програми. Реалізує консольне меню для керування працівниками.
 */
public class Main {

    // Список усіх створених працівників
    private static final List<Employee> employees = new ArrayList<>();

    // Сканер для зчитування введення з клавіатури
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Точка входу в програму. Показує меню до вибору «Вийти».
     *
     * @param args аргументи командного рядка (не використовуються)
     */
    public static void main(String[] args) {
        System.out.println("=== Система управління працівниками ===");

        ContactInfo contact = new ContactInfo("ivan@example.com", "+380501234567");
        Employee emp = new Employee("Іваненко Іван", 30, 20000.0, 5,
                Position.DEVELOPER, Department.IT, contact);
        employees.add(emp);
        ContactInfo contact1 = new ContactInfo("petrenkoivan@example.com", "+380501234568");
        Employee emp1 = new Employee("Петренко Іван", 35, 25000.0, 10,
                Position.DEVELOPER, Department.IT, contact1);
        employees.add(emp1);

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Ваш вибір: ");
            switch (choice) {
                case 1 -> createEmployee();
                case 2 -> listEmployees();
                case 3 -> copyEmployee();
                case 4 -> showCount();
                case 5  -> { System.out.println("До побачення!"); running = false; }
                default -> System.out.println("[!] Невірний пункт. Введіть 1, 2 або 3.");
            }
        }
    }

    /** Виводить пункти меню. */
    private static void printMenu() {
        System.out.println("\n--- Меню ---");
        System.out.println("1. Створити нового працівника");
        System.out.println("2. Показати всіх працівників");
        System.out.println("3. Копіювати працівника");
        System.out.println("4. Показати кількість створених об'єктів");
        System.out.println("5. Вийти");
    }

    /**
     * Зчитує дані з клавіатури та створює нового працівника.
     * При помилці валідації виводить повідомлення і повертається до меню.
     */
    private static void createEmployee() {
        System.out.println("\n--- Новий працівник ---");
        try {
            String name       = readNonBlank("ПІБ: ");
            int    age        = readInt("Вік (18–65): ");
            double salary     = readDouble("Місячна зарплата (грн): ");
            int    experience = readInt("Стаж роботи (років): ");
            Position   position   = readPosition();
            Department department = readDepartment();

            String email = readNonBlank("Email: ");
            String phone = readNonBlank("Телефон: ");
            ContactInfo contact = new ContactInfo(email, phone);

            Employee emp = new Employee(name, age, salary, experience,
                    position, department, contact);
            employees.add(emp);
            System.out.println("[+] Працівника додано: " + emp);

        } catch (InvalidEmployeeDataException e) {
            System.out.println("[!] Помилка валідації: " + e.getMessage());
        }
    }

    /**
     * Виводить список усіх збережених працівників.
     * Якщо список порожній — повідомляє про це.
     */
    private static void listEmployees() {
        if (employees.isEmpty()) {
            System.out.println("Список працівників порожній.");
            return;
        }
        System.out.println("\n--- Список працівників ---");
        for (int i = 0; i < employees.size(); i++) {
            Employee e = employees.get(i);
            System.out.printf("%d. %s%n", i + 1, e);
            System.out.printf("   Контакт: %s%n", e.getContact());
        }
    }

    /**
     * Зчитує непорожній рядок. Повторює запит при порожньому введенні.
     *
     * @param prompt підказка для користувача
     * @return введений непорожній рядок
     */
    private static String readNonBlank(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("[!] Поле не може бути порожнім. Спробуйте ще раз.");
        }
    }

    /**
     * Копіює існуючого працівника за допомогою конструктора копіювання.
     */
    private static void copyEmployee() {
        if (employees.isEmpty()) {
            System.out.println("[!] Список працівників порожній. Спочатку створіть працівника.");
            return;
        }
        System.out.println("\n--- Копіювання працівника ---");
        listEmployees();
        int index = readInt("Введіть номер працівника для копіювання: ") - 1;

        if (index < 0 || index >= employees.size()) {
            System.out.println("[!] Невірний номер.");
            return;
        }

        try {
            Employee original = employees.get(index);
            Employee copy     = new Employee(original); // конструктор копіювання
            employees.add(copy);
            System.out.println("[+] Копію створено: " + copy);
        } catch (InvalidEmployeeDataException e) {
            System.out.println("[!] Помилка: " + e.getMessage());
        }
    }

    /**
     * Виводить кількість створених об'єктів Employee через статичний геттер.
     */
    private static void showCount() {
        System.out.println("Всього працівників: " + Employee.getCount());
    }

    /**
     * Пропонує користувачу вибрати посаду зі списку enum.
     *
     * @return обраний {@link Position}
     */
    private static Position readPosition() {
        System.out.println("Оберіть посаду:");
        Position[] values = Position.values();
        for (int i = 0; i < values.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, values[i].getDisplayName());
        }
        while (true) {
            int idx = readInt("Номер посади: ");
            if (idx >= 1 && idx <= values.length) {
                return values[idx - 1];
            }
            System.out.println("[!] Введіть число від 1 до " + values.length + ".");
        }
    }

    /**
     * Пропонує користувачу вибрати відділ зі списку enum.
     *
     * @return обраний {@link Department}
     */
    private static Department readDepartment() {
        System.out.println("Оберіть відділ:");
        Department[] values = Department.values();
        for (int i = 0; i < values.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, values[i].getDisplayName());
        }
        while (true) {
            int idx = readInt("Номер відділу: ");
            if (idx >= 1 && idx <= values.length) {
                return values[idx - 1];
            }
            System.out.println("[!] Введіть число від 1 до " + values.length + ".");
        }
    }

    /**
     * Зчитує ціле число. Повторює запит при нечисловому введенні.
     *
     * @param prompt підказка для користувача
     * @return введене ціле число
     */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("[!] Введіть ціле число.");
            }
        }
    }

    /**
     * Зчитує дійсне число. Повторює запит при нечисловому введенні.
     *
     * @param prompt підказка для користувача
     * @return введене дійсне число
     */
    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().replace(',', '.');
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("[!] Введіть число (наприклад: 15000.50).");
            }
        }
    }
}