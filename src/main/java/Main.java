import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.*;

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

        loadEmployeesFromFile();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Ваш вибір: ");
            switch (choice) {
                case 1 -> createObject();
                case 2 -> listEmployees();
                case 3 -> { saveEmployeesToFile(); System.out.println("До побачення!");
                    running = false;}
                default -> System.out.println("[!] Невірний пункт. Введіть 1, 2 або 3.");
            }
        }
    }

    /** Виводить пункти головного меню. */
    private static void printMenu() {
        System.out.println("\n--- Меню ---");
        System.out.println("1. Створити новий об'єкт");
        System.out.println("2. Вивести інформацію про всі об'єкти");
        System.out.println("3. Завершити роботу програми");
    }

    /**
     * Показує підменю вибору типу об'єкта та делегує створення
     * відповідному методу. Надає можливість повернення до головного меню.
     */
    private static void createObject() {
        System.out.println("\n--- Оберіть тип об'єкта ---");
        System.out.println("1. Employee (звичайний працівник)");
        System.out.println("2. FullTimeEmployee (працівник на повний день)");
        System.out.println("3. ContractEmployee (контрактний працівник)");
        System.out.println("4. RemoteEmployee (віддалений працівник)");
        System.out.println("5. InternEmployee (стажер)");
        System.out.println("0. Повернутися до головного меню");

        int choice = readInt("Ваш вибір: ");
        switch (choice) {
            case 1 -> createEmployee();
            case 2 -> createFullTimeEmployee();
            case 3 -> createContractEmployee();
            case 4 -> createRemoteEmployee();
            case 5 -> createInternEmployee();
            case 0 -> System.out.println("[*] Повернення до головного меню.");
            default -> System.out.println("[!] Невірний тип. Повернення до головного меню.");
        }
    }

    /**
     * Завантажує працівників з файлу input.txt при запуску програми.
     * Ігнорує некоректні рядки з виведенням повідомлення.
     */
    private static void loadEmployeesFromFile() {
        File file = new File("input.txt");
        if (!file.exists()) {
            System.out.println("[*] Файл input.txt не знайдено. Починаємо з порожньої колекції.");
            return;
        }

        System.out.println("[*] Завантаження даних з input.txt...");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                try {
                    Employee emp = parseEmployee(line);
                    if (emp != null) {
                        employees.add(emp);
                        System.out.println("  [+] Завантажено: " + emp.getClass().getSimpleName() + " - " + emp.getName());
                    }
                } catch (Exception e) {
                    System.out.println("[!] Помилка при парсингу рядка " + lineNum + ": " + e.getMessage());
                    System.out.println("    Рядок: " + line);
                }
            }
            System.out.println("[*] Завантажено " + employees.size() + " працівників.");
        } catch (IOException e) {
            System.out.println("[!] Помилка читання файлу input.txt: " + e.getMessage());
        }
    }

    /**
     * Зберігає всіх працівників у файл input.txt при завершенні програми.
     */
    private static void saveEmployeesToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("input.txt"))) {
            for (Employee emp : employees) {
                writer.println(toFileString(emp));
            }
            System.out.println("[*] Дані збережено у файл input.txt (" + employees.size() + " записів).");
        } catch (IOException e) {
            System.out.println("[!] Помилка запису у файл input.txt: " + e.getMessage());
        }
    }

    /**
     * Парсить рядок з файлу та створює відповідний об'єкт працівника.
     * Підтримує як англійські, так і українські назви посад та відділів.
     */
    private static Employee parseEmployee(String line) throws InvalidEmployeeDataException {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 7) {
            throw new IllegalArgumentException("Недостатньо полів у рядку. Отримано: " + parts.length);
        }

        String type       = parts[0].trim();
        String name       = parts[1].trim();
        int age           = Integer.parseInt(parts[2].trim());
        double salary     = Double.parseDouble(parts[3].trim());
        int experience    = Integer.parseInt(parts[4].trim());

        String posStr     = parts[5].trim();
        String depStr     = parts[6].trim();

        // Конвертуємо українські назви в enum
        Position position = parsePosition(posStr);
        Department department = parseDepartment(depStr);

        switch (type) {
            case "Employee":
                return new Employee(name, age, salary, experience, position, department);

            case "FullTimeEmployee":
                if (parts.length < 8) throw new IllegalArgumentException("Недостатньо полів для FullTimeEmployee");
                double bonus = Double.parseDouble(parts[7].trim());
                return new FullTimeEmployee(name, age, salary, experience, position, department, bonus);

            case "ContractEmployee":
                if (parts.length < 8) throw new IllegalArgumentException("Недостатньо полів для ContractEmployee");
                int months = Integer.parseInt(parts[7].trim());
                return new ContractEmployee(name, age, salary, experience, position, department, months);

            case "RemoteEmployee":
                if (parts.length < 9) throw new IllegalArgumentException("Недостатньо полів для RemoteEmployee");
                String country = parts[7].trim();
                double hourlyRate = Double.parseDouble(parts[8].trim());
                return new RemoteEmployee(name, age, salary, experience, position, department, country, hourlyRate);

            case "InternEmployee":
                if (parts.length < 9) throw new IllegalArgumentException("Недостатньо полів для InternEmployee");
                String university = parts[7].trim();
                int duration = Integer.parseInt(parts[8].trim());
                return new InternEmployee(name, age, salary, experience, position, department, university, duration);

            default:
                throw new IllegalArgumentException("Невідомий тип працівника: " + type);
        }
    }

    /**
     * Конвертує назву посади (українською або англійською) в enum Position
     */
    private static Position parsePosition(String str) {
        str = str.trim();
        // Спочатку пробуємо як enum-константу
        try {
            return Position.valueOf(str);
        } catch (IllegalArgumentException ignored) {}

        // Якщо не вдалося — шукаємо за displayName
        for (Position p : Position.values()) {
            if (p.getDisplayName().equalsIgnoreCase(str) ||
                    p.name().equalsIgnoreCase(str)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Невідома посада: " + str);
    }

    /**
     * Конвертує назву відділу (українською або англійською) в enum Department
     */
    private static Department parseDepartment(String str) {
        str = str.trim();
        try {
            return Department.valueOf(str);
        } catch (IllegalArgumentException ignored) {}

        for (Department d : Department.values()) {
            if (d.getDisplayName().equalsIgnoreCase(str) ||
                    d.name().equalsIgnoreCase(str)) {
                return d;
            }
        }
        throw new IllegalArgumentException("Невідомий відділ: " + str);
    }

    /**
     * Перетворює об'єкт Employee (будь-якого підкласу) у рядок для збереження у файл.
     */
    private static String toFileString(Employee emp) {
        StringBuilder sb = new StringBuilder();
        sb.append(emp.getClass().getSimpleName()).append("|")
                .append(emp.getName()).append("|")
                .append(emp.getAge()).append("|")
                .append(emp.getSalary()).append("|")
                .append(emp.getExperience()).append("|")
                .append(emp.getPosition().name()).append("|")
                .append(emp.getDepartment().name());

        if (emp instanceof FullTimeEmployee fte) {
            sb.append("|").append(fte.getBonusPercentage());
        } else if (emp instanceof ContractEmployee ce) {
            sb.append("|").append(ce.getContractDurationMonths());
        } else if (emp instanceof RemoteEmployee re) {
            sb.append("|").append(re.getCountry())
                    .append("|").append(re.getHourlyRate());
        } else if (emp instanceof InternEmployee ie) {
            sb.append("|").append(ie.getUniversity())
                    .append("|").append(ie.getInternshipDuration());
        }

        return sb.toString();
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
     * Зчитує дані з клавіатури та створює віддаленого працівника.
     * Додатково запитує країну проживання та погодинну ставку.
     * При помилці валідації виводить повідомлення і повертається до меню.
     */
    private static void createRemoteEmployee() {
        System.out.println("\n--- Новий Remote працівник ---");
        try {
            String     name        = readNonBlank("ПІБ: ");
            int        age         = readInt("Вік (18–65): ");
            double     salary      = readDouble("Зарплата (грн): ");
            int        experience  = readInt("Стаж (років): ");
            Position   position    = readPosition();
            Department dept        = readDepartment();
            String     country     = readNonBlank("Країна проживання: ");
            double     hourlyRate  = readDouble("Погодинна ставка ($/год): ");

            RemoteEmployee emp = new RemoteEmployee(name, age, salary, experience,
                    position, dept, country, hourlyRate);
            employees.add(emp);
            System.out.println("[+] Remote працівника додано: " + emp);
        } catch (InvalidEmployeeDataException e) {
            System.out.println("[!] Помилка: " + e.getMessage());
        }
    }

    /**
     * Зчитує дані з клавіатури та створює працівника-стажера.
     * Додатково запитує назву університету та тривалість стажування.
     * При помилці валідації виводить повідомлення і повертається до меню.
     */
    private static void createInternEmployee() {
        System.out.println("\n--- Новий Intern працівник ---");
        try {
            String     name      = readNonBlank("ПІБ: ");
            int        age       = readInt("Вік (18–65): ");
            double     salary    = readDouble("Зарплата (грн): ");
            int        experience = readInt("Стаж (років): ");
            Position   position  = readPosition();
            Department dept      = readDepartment();
            String     university = readNonBlank("Університет: ");
            int        duration  = readInt("Тривалість стажування (місяців, 1–12): ");

            InternEmployee emp = new InternEmployee(name, age, salary, experience,
                    position, dept, university, duration);
            employees.add(emp);
            System.out.println("[+] Intern працівника додано: " + emp);
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