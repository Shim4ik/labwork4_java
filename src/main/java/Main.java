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

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Ваш вибір: ");
            switch (choice) {
                case 1  -> createEmployee();
                case 2  -> listEmployees();
                case 3  -> { System.out.println("До побачення!"); running = false; }
                default -> System.out.println("[!] Невірний пункт. Введіть 1, 2 або 3.");
            }
        }
    }

    /** Виводить пункти меню. */
    private static void printMenu() {
        System.out.println("\n--- Меню ---");
        System.out.println("1. Створити нового працівника");
        System.out.println("2. Показати всіх працівників");
        System.out.println("3. Вийти");
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
            String position   = readNonBlank("Посада: ");
            String department = readNonBlank("Відділ: ");

            Employee emp = new Employee(name, age, salary, experience, position, department);
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
            System.out.printf("%d. %s%n", i + 1, employees.get(i));
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