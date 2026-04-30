/**
 * Клас контрактного працівника (Contract Employee).
 * Успадковується від базового класу Employee.
 */
public class ContractEmployee extends Employee {

    private final int contractDurationMonths;   // тривалість контракту в місяцях

    /**
     * Створює контрактного працівника.
     *
     * @param contractDurationMonths тривалість контракту (1–60 місяців)
     * @throws InvalidEmployeeDataException якщо тривалість поза діапазоном
     */
    public ContractEmployee(String name, int age, double salary, int experience,
                            Position position, Department department,
                            int contractDurationMonths) {
        super(name, age, salary, experience, position, department);

        if (contractDurationMonths < 1 || contractDurationMonths > 60) {
            throw new InvalidEmployeeDataException(
                    "Тривалість контракту має бути від 1 до 60 місяців. Отримано: " + contractDurationMonths);
        }
        this.contractDurationMonths = contractDurationMonths;
    }

    /** @return тривалість контракту в місяцях */
    public int getContractDurationMonths() {
        return contractDurationMonths;
    }

    /** @return рядкове представлення об'єкта */
    @Override
    public String toString() {
        return String.format(
                "{ПІБ='%s', вік=%d, зарплата=%.2f грн, стаж=%d р., посада='%s', відділ='%s', премія=%d}",
                getName(), getAge(), getSalary(), getExperience(), getPosition(), getDepartment(), contractDurationMonths);
    }
}
