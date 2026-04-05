public class Employee {
    private String fullName;
    private String position;
    private double salary;
    private int experienceYears;

    // Конструктор
    public Employee(String fullName, String position, double salary, int experienceYears) {
        this.fullName = fullName;
        this.position = position;
        this.salary = salary;
        this.experienceYears = experienceYears;
    }

    // Гетери
    public String getFullName() {
        return fullName;
    }

    public String getPosition() {
        return position;
    }

    public double getSalary() {
        return salary;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    // Сетери
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "fullName='" + fullName + '\'' +
                ", position='" + position + '\'' +
                ", salary=" + salary +
                ", experienceYears=" + experienceYears +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Double.compare(salary, employee.salary) == 0 &&
                experienceYears == employee.experienceYears &&
                fullName.equals(employee.fullName) &&
                position.equals(employee.position);
    }

    @Override
    public int hashCode() {
        int result = fullName.hashCode();
        result = 31 * result + position.hashCode();
        result = 31 * result + Double.hashCode(salary);
        result = 31 * result + experienceYears;
        return result;
    }
}