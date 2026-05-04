import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.*;
import java.util.Comparator;
import java.text.Collator;
import java.util.Locale;


/**
 * Головний клас програми. Реалізує консольне меню для керування компанією та її працівниками.
 */
public class Main {

    // Поточна компанія (завантажується з файлу або створюється вручну)
    private static Company company = null;

    // Сканер для зчитування введення з клавіатури
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Точка входу в програму. Показує меню до вибору «Вийти».
     *
     * @param args аргументи командного рядка (не використовуються)
     */
    public static void main(String[] args) {
        System.out.println("=== Система управління компанією ===");

        loadFromFile();

        // Якщо компанію не вдалося завантажити — просимо створити вручну
        if (company == null) {
            System.out.println("[*] Компанія не знайдена у файлі. Створіть нову компанію.");
            company = createCompanyInteractive();
        }

        System.out.println("[*] Поточна компанія: " + company);

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Ваш вибір: ");
            switch (choice) {
                case 1 -> runSearchMenu();
                case 2 -> createObject();
                case 3 -> listEmployees();
                case 4 -> listEmployeesSorted();
                case 5 -> showCompanyInfo();
                case 6 -> { saveToFile(); System.out.println("До побачення!"); running = false; }
                default -> System.out.println("[!] Невірний пункт. Введіть 1-5.");
            }
        }
    }

    /** Виводить пункти головного меню. */
    private static void printMenu() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║           ГОЛОВНЕ МЕНЮ                ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║  1. Пошук об'єкта                     ║");
        System.out.println("║  2. Створити новий об'єкт             ║");
        System.out.println("║  3. Вивести інформацію про всі об'єкти║");
        System.out.println("║  4. Вивести відсортовану інформацію   ║");
        System.out.println("║  5. Інформація про компанію           ║");
        System.out.println("║  6. Зберегти та завершити             ║");
        System.out.println("╚═══════════════════════════════════════╝");
    }

    /** Виводить підменю пошуку та обробляє вибір користувача. */
    private static void runSearchMenu() {
        boolean inSearch = true;
        while (inSearch) {
            System.out.println("\n--- Пошук об'єкта ---");
            System.out.println("1. Пошук за діапазоном зарплати");
            System.out.println("2. Пошук запосадою");
            System.out.println("3. Пошук за відділом");
            System.out.println("0. Повернутися до головного меню");

            int choice = readInt("Ваш вибір: ");
            switch (choice) {
                case 1 -> searchBySalaryRange();
                case 2 -> searchByPosition();
                case 3 -> searchByDepartment();
                case 0 -> inSearch = false;
                default -> System.out.println("[!] Невірний пункт. Спробуйте ще раз.");
            }
        }
    }

    /**
     * Зчитує діапазон зарплати та виводить усіх працівників,
     * чия зарплата потрапляє в цей діапазон.
     */
    private static void searchBySalaryRange() {
        System.out.println("\n--- Пошук за діапазоном зарплати ---");
        double min = readDouble("Мінімальна зарплата (грн): ");
        double max = readDouble("Максимальна зарплата (грн): ");

        if (min > max) {
            System.out.println("[!] Мінімальна зарплата не може перевищувати максимальну.");
            return;
        }

        List<Company.EmployeeRecord> results = company.findBySalaryRange(min, max);
        printSearchResults(results,
                String.format("зарплата від %.2f до %.2f грн", min, max));
    }

    /**
     * Пропонує обрати посаду та виводить усіх працівників з цією посадою.
     */
    private static void searchByPosition() {
        System.out.println("\n--- Пошук за посадою ---");
        Position position = readPosition();

        List<Company.EmployeeRecord> results = company.findByPosition(position);
        printSearchResults(results, "посада: " + position.getDisplayName());
    }

    /**
     * Пропонує обрати відділ та виводить усіх працівників цього відділу.
     */
    private static void searchByDepartment() {
        System.out.println("\n--- Пошук за відділом ---");
        Department department = readDepartment();

        List<Company.EmployeeRecord> results = company.findByDepartment(department);
        printSearchResults(results, "відділ: " + department.getDisplayName());
    }

    /**
     * Виводить результати пошуку або повідомлення про відсутність збігів.
     *
     * @param results  знайдені записи
     * @param criteria опис критерію пошуку для виведення
     */
    private static void printSearchResults(List<Company.EmployeeRecord> results, String criteria) {
        System.out.println("\n--- Результати пошуку (" + criteria + ") ---");
        if (results.isEmpty()) {
            System.out.println("[!] Жоден працівник не відповідає умовам пошуку.");
            return;
        }
        System.out.println("Знайдено: " + results.size() + " запис(ів)");
        for (int i = 0; i < results.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, results.get(i));
        }
    }

    /**
     * Показує підменю вибору типу об'єкта та делегує створення
     * відповідному методу. Надає можливість повернення до головного меню.
     */
    private static void createObject() {
        System.out.println("\n--- Оберіть тип об'єкта ---");
        System.out.println("1. FullTimeEmployee (працівник на повний день)");
        System.out.println("2. ContractEmployee (контрактний працівник)");
        System.out.println("3. RemoteEmployee (віддалений працівник)");
        System.out.println("4. InternEmployee (стажер)");
        System.out.println("0. Повернутися до головного меню");

        int choice = readInt("Ваш вибір: ");
        Employee emp = null;

        try {
            switch (choice) {
                case 1 -> emp = createFullTimeEmployee();
                case 2 -> emp = createContractEmployee();
                case 3 -> emp = createRemoteEmployee();
                case 4 -> emp = createInternEmployee();
                case 0 -> { System.out.println("[*] Повернення до головного меню."); return; }
                default -> { System.out.println("[!] Невірний тип. Повернення до головного меню."); return; }
            }
        } catch (InvalidEmployeeDataException e) {
            System.out.println("[!] Помилка валідації: " + e.getMessage());
            return;
        }

        int quantity = readPositiveInt("Кількість таких працівників: ");
        company.addNewEmployee(emp, quantity);
        System.out.println("[+] Додано (кількість: " + quantity + "): " + emp);
    }

    /**
     * Виводить список усіх збережених працівників.
     * Якщо список порожній — повідомляє про це.
     */
    private static void listEmployees() {
        System.out.println("\n--- Список працівників (поліморфний вивід) ---");
        List<Company.EmployeeRecord> records = company.getRecords();
        if (records.isEmpty()) {
            System.out.println("[!] Колекція порожня.");
            return;
        }
        for (int i = 0; i < records.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, records.get(i));
        }
        System.out.println("Всього записів: " + records.size()
                + " | Всього працівників: " + company.getTotalEmployeeCount());
    }

    /**
     * Показує підменю вибору критерію сортування та виводить відсортований список.
     * Для кожного критерію використовується анонімний внутрішній клас Comparator.
     */
    private static void listEmployeesSorted() {
        List<Company.EmployeeRecord> records = company.getRecords();
        if (records.isEmpty()) {
            System.out.println("[!] Колекція порожня — нема чого сортувати.");
            return;
        }

        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║       ОБЕРІТЬ КРИТЕРІЙ СОРТУВАННЯ     ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║  1. Сортувати за ПІБ (А→Я)            ║");
        System.out.println("║  2. Сортувати за зарплатою (↑)        ║");
        System.out.println("║  3. Сортувати за стажем (↓)           ║");
        System.out.println("║  0. Повернутися до головного меню     ║");
        System.out.println("╚═══════════════════════════════════════╝");

        int choice = readInt("Ваш вибір: ");

        if (choice == 0) {
            System.out.println("[*] Повернення до головного меню.");
            return;
        }

        // Анонімний внутрішній клас — сортування за ПІБ
        Comparator<Company.EmployeeRecord> byName = new Comparator<Company.EmployeeRecord>() {
            // Створюємо екземпляр Collator для української мови
            private final Collator collator = Collator.getInstance(new Locale("uk", "UA"));

            @Override
            public int compare(Company.EmployeeRecord r1, Company.EmployeeRecord r2) {
                collator.setStrength(Collator.TERTIARY);
                return collator.compare(r1.getEmployee().getName(), r2.getEmployee().getName());
            }
        };

        // Анонімний внутрішній клас — сортування за зарплатою за зростанням
        Comparator<Company.EmployeeRecord> bySalaryAsc = new Comparator<Company.EmployeeRecord>() {
            @Override
            public int compare(Company.EmployeeRecord r1, Company.EmployeeRecord r2) {
                return Double.compare(r1.getEmployee().getSalary(), r2.getEmployee().getSalary());
            }
        };

        // Анонімний внутрішній клас — сортування за стажем за спаданням
        Comparator<Company.EmployeeRecord> byExperienceDesc = new Comparator<Company.EmployeeRecord>() {
            @Override
            public int compare(Company.EmployeeRecord r1, Company.EmployeeRecord r2) {
                return Integer.compare(r2.getEmployee().getExperience(), r1.getEmployee().getExperience());
            }
        };

        Comparator<Company.EmployeeRecord> comparator;
        String criteriaLabel;

        switch (choice) {
            case 1:
                comparator    = byName;
                criteriaLabel = "за ПІБ (А→Я)";
                break;
            case 2:
                comparator    = bySalaryAsc;
                criteriaLabel = "за зарплатою (від меншої до більшої)";
                break;
            case 3:
                comparator    = byExperienceDesc;
                criteriaLabel = "за стажем (від більшого до меншого)";
                break;
            default:
                System.out.println("[!] Невірний пункт. Повернення до головного меню.");
                return;
        }

        // Копіюємо, щоб не змінювати оригінальний порядок у компанії
        List<Company.EmployeeRecord> sorted = new ArrayList<>(records);
        java.util.Collections.sort(sorted, comparator);

        System.out.println("\n--- Відсортований список працівників (" + criteriaLabel + ") ---");
        for (int i = 0; i < sorted.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, sorted.get(i));
        }
        System.out.println("Всього записів: " + sorted.size()
                + " | Всього працівників: " + company.getTotalEmployeeCount());
    }

    /** Виводить інформацію про компанію. */
    private static void showCompanyInfo() {
        System.out.println("\n--- Інформація про компанію ---");
        System.out.println(company);
    }

    // =========================================================================
    // Збереження / завантаження
    // =========================================================================

    /**
     * Завантажує компанію та її працівників з файлу input.txt при запуску програми.
     * Ігнорує некоректні рядки з виведенням повідомлення.
     */
    private static void loadFromFile() {
        File file = new File("input.txt");
        if (!file.exists()) {
            System.out.println("[*] Файл input.txt не знайдено. Починаємо з порожньої компанії.");
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

                if (line.startsWith("Company|")) {
                    // Розбираємо опис компанії
                    try {
                        company = parseCompany(line);
                        System.out.println("[*] Компанію завантажено: " + company.getName());
                    } catch (Exception e) {
                        System.out.println("[!] Помилка при парсингу компанії (рядок " + lineNum + "): " + e.getMessage());
                    }
                } else {
                    // Рядки з працівниками розбираємо лише після ініціалізації компанії
                    if (company == null) {
                        System.out.println("[!] Рядок " + lineNum + " пропущено: компанія ще не завантажена.");
                        continue;
                    }
                    try {
                        parseEmployeeRecord(line);
                    } catch (Exception e) {
                        System.out.println("[!] Помилка при парсингу рядка " + lineNum + ": " + e.getMessage());
                        System.out.println("    Рядок: " + line);
                    }
                }
            }

            if (company != null) {
                System.out.println("[*] Завантажено записів: " + company.getRecords().size()
                        + " | Всього працівників: " + company.getTotalEmployeeCount());
            }
        } catch (IOException e) {
            System.out.println("[!] Помилка читання файлу input.txt: " + e.getMessage());
        }
    }

    /**
     * Парсить рядок з описом компанії.
     * Формат: Company|назва|сфера|рікЗаснування
     */
    private static Company parseCompany(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 4)
            throw new IllegalArgumentException("Неповний рядок компанії. Отримано: " + parts.length + " полів.");
        String companyName = parts[1].trim();
        String industry    = parts[2].trim();
        int    foundedYear = Integer.parseInt(parts[3].trim());
        return new Company(companyName, industry, foundedYear);
    }

    /**
     * Парсить рядок з описом запису працівника та додає його до company.
     * Формат: тип|поля...|quantity
     * (quantity — останнє поле)
     */
    private static void parseEmployeeRecord(String line) {
        String[] parts = line.split("\\|", -1);
        // Мінімум: тип|ім'я|вік|зарплата|стаж|посада|відділ|quantity = 8 полів
        if (parts.length < 8)
            throw new IllegalArgumentException("Недостатньо полів у рядку. Отримано: " + parts.length);

        // Кількість — останній токен
        int quantity;
        try {
            quantity = Integer.parseInt(parts[parts.length - 1].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неправильна кількість у рядку: " + parts[parts.length - 1]);
        }

        // Відновлюємо рядок без останнього поля (quantity) і парсимо Employee
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (i > 0) sb.append("|");
            sb.append(parts[i]);
        }

        Employee emp = parseEmployee(sb.toString());
        if (emp != null) {
            company.addNewEmployee(emp, quantity);
            System.out.println("  [+] Завантажено: " + emp.getClass().getSimpleName()
                    + " - " + emp.getName() + " (кількість: " + quantity + ")");
        }
    }

    /**
     * Зберігає всіх працівників у файл input.txt при завершенні програми.
     */
    private static void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("input.txt"))) {
            // Перший рядок — опис компанії
            writer.println(companyToFileString(company));

            // Далі — записи працівників
            for (Company.EmployeeRecord record : company.getRecords()) {
                writer.println(employeeRecordToFileString(record));
            }
            System.out.println("[*] Дані збережено у файл input.txt ("
                    + company.getRecords().size() + " записів).");
        } catch (IOException e) {
            System.out.println("[!] Помилка запису у файл input.txt: " + e.getMessage());
        }
    }

    /** Формує рядок-запис компанії для файлу. */
    private static String companyToFileString(Company c) {
        return "Company|" + c.getName() + "|" + c.getIndustry() + "|" + c.getFoundedYear();
    }

    /**
     * Перетворює об'єкт Employee (будь-якого підкласу) у рядок для збереження у файл.
     */
    private static String employeeRecordToFileString(Company.EmployeeRecord record) {
        Employee emp = record.getEmployee();
        StringBuilder sb = new StringBuilder();
        sb.append(emp.getClass().getSimpleName()).append("|")
                .append(emp.getName()).append("|")
                .append(emp.getAge()).append("|")
                .append(String.format(java.util.Locale.US, "%.2f", emp.getSalary())).append("|")
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

        sb.append("|").append(record.getQuantity());
        return sb.toString();
    }

    // =========================================================================
    // Парсинг Employee (без кількості)
    // =========================================================================

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
                // Employee тепер абстрактний — конвертуємо у FullTimeEmployee з нульовою премією
                return new FullTimeEmployee(name, age, salary, experience, position, department, 0);

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

    // =========================================================================
    // Інтерактивне введення об'єктів
    // =========================================================================

    /** Інтерактивне створення компанії. */
    private static Company createCompanyInteractive() {
        System.out.println("\n--- Введіть дані компанії ---");
        while (true) {
            try {
                String companyName = readNonBlank("Назва компанії: ");
                String industry    = readNonBlank("Сфера діяльності: ");
                int    foundedYear = readInt("Рік заснування: ");
                return new Company(companyName, industry, foundedYear);
            } catch (InvalidEmployeeDataException e) {
                System.out.println("[!] Помилка: " + e.getMessage() + ". Спробуйте ще раз.");
            }
        }
    }

    /**
     * Зчитує дані з клавіатури та створює нового працівника на повний робочий день.
     * Додатково запитує відсоток щорічної премії.
     * При помилці валідації виводить повідомлення і повертається до меню.
     */
    private static Employee createFullTimeEmployee() {
        System.out.println("\n--- Новий Full-time працівник ---");
        String name       = readNonBlank("ПІБ: ");
        int    age        = readInt("Вік (18–65): ");
        double salary     = readDouble("Зарплата (грн): ");
        int    experience = readInt("Стаж (років): ");
        Position   position = readPosition();
        Department dept     = readDepartment();
        double     bonus    = readDouble("Відсоток премії (0-50): ");
        return new FullTimeEmployee(name, age, salary, experience, position, dept, bonus);
    }

    /**
     * Зчитує дані з клавіатури та створює нового контрактного працівника.
     * Додатково запитує тривалість контракту в місяцях.
     * При помилці валідації виводить повідомлення і повертається до меню.
     */
    private static Employee createContractEmployee() {
        System.out.println("\n--- Новий Contract працівник ---");
        String name       = readNonBlank("ПІБ: ");
        int    age        = readInt("Вік (18–65): ");
        double salary     = readDouble("Зарплата (грн): ");
        int    experience = readInt("Стаж (років): ");
        Position   position = readPosition();
        Department dept     = readDepartment();
        int        months   = readInt("Тривалість контракту (місяців, 1-60): ");
        return new ContractEmployee(name, age, salary, experience, position, dept, months);
    }

    /**
     * Зчитує дані з клавіатури та створює віддаленого працівника.
     * Додатково запитує країну проживання та погодинну ставку.
     * При помилці валідації виводить повідомлення і повертається до меню.
     */
    private static Employee createRemoteEmployee() {
        System.out.println("\n--- Новий Remote працівник ---");
        String     name       = readNonBlank("ПІБ: ");
        int        age        = readInt("Вік (18–65): ");
        double     salary     = readDouble("Зарплата (грн): ");
        int        experience = readInt("Стаж (років): ");
        Position   position   = readPosition();
        Department dept       = readDepartment();
        String     country    = readNonBlank("Країна проживання: ");
        double     hourlyRate = readDouble("Погодинна ставка ($/год): ");
        return new RemoteEmployee(name, age, salary, experience, position, dept, country, hourlyRate);
    }

    /**
     * Зчитує дані з клавіатури та створює працівника-стажера.
     * Додатково запитує назву університету та тривалість стажування.
     * При помилці валідації виводить повідомлення і повертається до меню.
     */
    private static Employee createInternEmployee() {
        System.out.println("\n--- Новий Intern працівник ---");
        String     name       = readNonBlank("ПІБ: ");
        int        age        = readInt("Вік (18–65): ");
        double     salary     = readDouble("Зарплата (грн): ");
        int        experience = readInt("Стаж (років): ");
        Position   position   = readPosition();
        Department dept       = readDepartment();
        String     university = readNonBlank("Університет: ");
        int        duration   = readInt("Тривалість стажування (місяців, 1–12): ");
        return new InternEmployee(name, age, salary, experience, position, dept, university, duration);
    }

    // =========================================================================
    // Допоміжні методи зчитування
    // =========================================================================

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
     * Зчитує ціле число >= 1. Повторює запит при некоректному введенні.
     *
     * @param prompt підказка для користувача
     * @return введене ціле число >= 1
     */
    private static int readPositiveInt(String prompt) {
        while (true) {
            int val = readInt(prompt);
            if (val >= 1) return val;
            System.out.println("[!] Введіть число >= 1.");
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