/**
 * Клас представляє працівника підприємства.
 * Усі поля перевіряються при створенні та зміні.
 */
public class Employee {

    private String name;       // ПІБ працівника
    private int age;           // Вік (18–65)
    private double salary;     // Місячна зарплата в гривнях
    private int experience;    // Стаж роботи в роках
    private Position position;   // Посада
    private Department department; // Відділ

    /**
     * Створює нового працівника з перевіркою всіх параметрів.
     *
     * @param name       ПІБ (не порожній)
     * @param age        вік (18–65)
     * @param salary     зарплата (> 0)
     * @param experience стаж (>= 0, не більше age - 18)
     * @param position   посада (не порожня)
     * @param department відділ (не порожній)
     * @throws InvalidEmployeeDataException якщо будь-який параметр некоректний
     */
    public Employee(String name, int age, double salary, int experience,
                    Position position, Department department) {
        setName(name);
        setAge(age);
        setSalary(salary);
        setExperience(experience);
        setPosition(position);
        setDepartment(department);
    }

    // Геттери

    /** @return ПІБ працівника */
    public String getName()       { return name; }

    /** @return вік працівника */
    public int getAge()           { return age; }

    /** @return місячна зарплата */
    public double getSalary()     { return salary; }

    /** @return стаж роботи */
    public int getExperience()    { return experience; }

    /** @return посада */
    public Position getPosition()   { return position; }

    /** @return відділ */
    public Department getDepartment() { return department; }

    // Сеттери з валідацією

    /**
     * Встановлює ПІБ працівника.
     *
     * @param name непорожній рядок
     * @throws InvalidEmployeeDataException якщо name порожній або null
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidEmployeeDataException("ПІБ не може бути порожнім.");
        }
        this.name = name.trim();
    }

    /**
     * Встановлює вік працівника.
     *
     * @param age значення від 18 до 65
     * @throws InvalidEmployeeDataException якщо вік поза діапазоном
     */
    public void setAge(int age) {
        if (age < 18 || age > 65) {
            throw new InvalidEmployeeDataException(
                    "Вік повинен бути від 18 до 65. Отримано: " + age);
        }
        this.age = age;
    }

    /**
     * Встановлює місячну зарплату.
     *
     * @param salary позитивне число
     * @throws InvalidEmployeeDataException якщо зарплата <= 0
     */
    public void setSalary(double salary) {
        if (salary <= 0) {
            throw new InvalidEmployeeDataException(
                    "Зарплата повинна бути більше нуля. Отримано: " + salary);
        }
        this.salary = salary;
    }

    /**
     * Встановлює стаж роботи.
     * Стаж не може перевищувати (вік - 18).
     *
     * @param experience невід'ємне число
     * @throws InvalidEmployeeDataException якщо стаж некоректний
     */
    public void setExperience(int experience) {
        if (experience < 0) {
            throw new InvalidEmployeeDataException(
                    "Стаж не може бути від'ємним. Отримано: " + experience);
        }
        if (experience > this.age - 18) {
            throw new InvalidEmployeeDataException(
                    "Стаж (" + experience + ") перевищує можливий для віку " + this.age + ".");
        }
        this.experience = experience;
    }

    /**
     * Встановлює посаду працівника.
     *
     * @param position значення enum {@link Position} (не null)
     * @throws InvalidEmployeeDataException якщо position є null
     */
    public void setPosition(Position position) {
        if (position == null) {
            throw new InvalidEmployeeDataException("Посада не може бути порожньою.");
        }
        this.position = position;
    }

    /**
     * Встановлює відділ працівника.
     *
     * @param department значення enum {@link Department} (не null)
     * @throws InvalidEmployeeDataException якщо department є null
     */
    public void setDepartment(Department department) {
        if (department == null) {
            throw new InvalidEmployeeDataException("Відділ не може бути порожнім.");
        }
        this.department = department;
    }

    /** @return рядкове представлення об'єкта */
    @Override
    public String toString() {
        return String.format(
                "{ПІБ='%s', вік=%d, зарплата=%.2f грн, стаж=%d р., посада='%s', відділ='%s'}",
                name, age, salary, experience, position, department);
    }
}