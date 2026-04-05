import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        ArrayList<Employee> employees = new ArrayList<>();

        System.out.print("Введіть кількість співробітників: ");
        int n = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < n; i++) {
            System.out.println("\nСпівробітник #" + (i + 1));

            System.out.print("ПІБ: ");
            String fullName = scanner.nextLine();

            System.out.print("Посада: ");
            String position = scanner.nextLine();

            double salary = getValidSalary();

            int experience = getValidExperience();

            Employee emp = new Employee(fullName, position, salary, experience);
            employees.add(emp);
        }

        System.out.println("\nІнформація про співробітників");
        for (Employee emp : employees) {
            System.out.println(emp.toString());
        }

        scanner.close();
    }

    private static double getValidSalary() {
        double salary = -1;
        boolean isValid = false;

        while (!isValid) {
            System.out.print("Зарплата: ");

            if (!scanner.hasNextDouble()) {
                System.out.println("Помилка! Введіть числове значення.");
                scanner.nextLine();
                continue;
            }

            salary = scanner.nextDouble();
            scanner.nextLine();

            if (salary < 0) {
                System.out.println("Помилка! Зарплата не може бути від'ємною.");
                continue;
            }

            if (salary == 0) {
                System.out.println("⚠️  Увага! Зарплата дорівнює нулю. Це реально?");
                System.out.print("Підтвердіть (так/ні): ");
                String confirm = scanner.nextLine().toLowerCase().trim();
                if (confirm.equals("так") || confirm.equals("yes") || confirm.equals("y")) {
                    isValid = true;
                }
                continue;
            }

            isValid = true;
        }

        return salary;
    }

    private static int getValidExperience() {
        int experience = -1;
        boolean isValid = false;
        final int MAX_EXPERIENCE = 70;

        while (!isValid) {
            System.out.print("Стаж (років): ");

            if (!scanner.hasNextInt()) {
                System.out.println("Помилка! Введіть ціле число.");
                scanner.nextLine();
                continue;
            }

            experience = scanner.nextInt();
            scanner.nextLine();

            if (experience < 0) {
                System.out.println("Помилка! Стаж не може бути від'ємним.");
                continue;
            }

            if (experience > MAX_EXPERIENCE) {
                System.out.println("Помилка! Стаж не може перевищувати " + MAX_EXPERIENCE + " років.");
                continue;
            }

            isValid = true;
        }

        return experience;
    }
}
