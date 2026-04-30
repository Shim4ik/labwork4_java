/**
 * Клас повного робочого дня (Full-time Employee).
 * Успадковується від базового класу Employee.
 */
public class FullTimeEmployee extends Employee {

    private final double bonusPercentage;   // відсоток щорічної премії

    /**
     * Створює контрактного працівника.
     *
     * @param bonusPercentage премія у відсотках (1%–50%)
     * @throws InvalidEmployeeDataException якщо премія поза діапазоном
     */
    public FullTimeEmployee(String name, int age, double salary, int experience,
                            Position position, Department department,
                            double bonusPercentage) {
        super(name, age, salary, experience, position, department);

        if (bonusPercentage < 0 || bonusPercentage > 50) {
            throw new InvalidEmployeeDataException(
                    "Відсоток премії повинен бути в межах від 0 до 50%. Отримано: " + bonusPercentage);
        }
        this.bonusPercentage = bonusPercentage;
    }

    /** @return премію у відсотках */
    public double getBonusPercentage() {
        return bonusPercentage;
    }

    /** @return рядкове представлення об'єкта */
    @Override
    public String toString() {
        return String.format(
                "{ПІБ='%s', вік=%d, зарплата=%.2f грн, стаж=%d р., посада='%s', відділ='%s', премія=%.1f%%}",
                getName(), getAge(), getSalary(), getExperience(), getPosition(), getDepartment(), bonusPercentage);
    }
}