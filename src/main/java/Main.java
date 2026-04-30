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

        Employee emp = new Employee("Іваненко Іван", 30, 20000.0, 5,
                Position.DEVELOPER, Department.IT);
        employees.add(emp);

        Employee emp1 = new Employee("Петренко Іван", 35, 25000.0, 10,
                Position.DEVELOPER, Department.IT);
        employees.add(emp1);

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Ваш вибір: ");
            switch (choice) {
                case 1 -> createEmployee();
                case 2 -> listEmployees();
                case 3 -> createFullTimeEmployee();
                case 4 -> createContractEmployee();
                case 5 -> { System.out.println("До побачення!"); running = false; }
                default -> System.out.println("[!] Невірний пункт. Введіть 1, 2 або 3.");
            }
        }
    }

    /** Виводить пункти меню. */
    private static void printMenu() {
        System.out.println("\n--- Меню ---");
        System.out.println("1. Створити звичайного працівника");
        System.out.println("2. Показати всіх працівників");
        System.out.println("3. Створити Full-time працівника");
        System.out.println("4. Створити Contract працівника");
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

            Employee emp = new Employee(name, age, salary, experience,
                    position, department);
            employees.add(emp);
            System.out.println("[+] Працівника додано: " + emp);

        } catch (InvalidEmployeeDataException e) {
            System.out.println("[!] Помилка валідації: " + e.getMessage());
        }
    }

    /**
     * Зчитує дані з клавіатури та створює нового працівника на повний робочий день.
     * Додатково запитує відсоток щорічної премії.
     * При помилці валідації виводить повідомлення і повертається до меню.
     */
    private static void createFullTimeEmployee() {
        System.out.println("\n--- Новий Full-time працівник ---");
        try {
            String name       = readNonBlank("ПІБ: ");
            int    age        = readInt("Вік (18–65): ");
            double salary     = readDouble("Зарплата (грн): ");
            int    experience = readInt("Стаж (років): ");
            Position position = readPosition();
            Department dept   = readDepartment();

            double bonus = readDouble("Відсоток премії (0-50): ");

            FullTimeEmployee emp = new FullTimeEmployee(name, age, salary, experience,
                    position, dept, bonus);
            employees.add(emp);
            System.out.println("[+] Full-time працівника додано: " + emp);
        } catch (InvalidEmployeeDataException e) {
            System.out.println("[!] Помилка: " + e.getMessage());
        }
    }

    /**
     * Зчитує дані з клавіатури та створює нового контрактного працівника.
     * Додатково запитує тривалість контракту в місяцях.
     * При помилці валідації виводить повідомлення і повертається до меню.
     */
    private static void createContractEmployee() {
        System.out.println("\n--- Новий Contract працівник ---");
        try {
            String name       = readNonBlank("ПІБ: ");
            int    age        = readInt("Вік (18–65): ");
            double salary     = readDouble("Зарплата (грн): ");
            int    experience = readInt("Стаж (років): ");
            Position position = readPosition();
            Department dept   = readDepartment();

            int months = readInt("Тривалість контракту (місяців, 1-60): ");

            ContractEmployee emp = new ContractEmployee(name, age, salary, experience,
                    position, dept, months);
            employees.add(emp);
            System.out.println("[+] Contract працівника додано: " + emp);
        } catch (InvalidEmployeeDataException e) {
            System.out.println("[!] Помилка: " + e.getMessage());
        }
    }

    /**
     * Виводить список усіх збережених працівників.
     * Якщо список порожній — повідомляє 3про це.
     */
    private static void listEmployees() {
        System.out.println("\n--- Список працівників (поліморфний вивід) ---");
        for (int i = 0; i < employees.size(); i++) {
            Employee e = employees.get(i);
            System.out.printf("%d. [%s] %s%n", i + 1, e.getClass().getSimpleName(), e);
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