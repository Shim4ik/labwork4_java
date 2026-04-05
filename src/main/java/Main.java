import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
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

            System.out.print("Зарплата: ");
            double salary = scanner.nextDouble();

            System.out.print("Стаж (років): ");
            int experience = scanner.nextInt();
            scanner.nextLine();

            Employee emp = new Employee(fullName, position, salary, experience);
            employees.add(emp);
        }

        System.out.println("\nІнформація про співробітників");
        for (Employee emp : employees) {
            System.out.println(emp.toString());
        }

        scanner.close();
    }
}