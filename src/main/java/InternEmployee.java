/**
 * Клас працівника-стажера (Intern Employee).
 * Успадковується від базового класу {@link Employee}.
 * Додатково зберігає назву університету та тривалість стажування в місяцях.
 */
public class InternEmployee extends Employee {

    private String university;          // назва університету
    private int internshipDuration;     // тривалість стажування в місяцях (1–12)

    /**
     * Створює працівника-стажера з перевіркою всіх параметрів.
     *
     * @param name               ПІБ (не порожній)
     * @param age                вік (18–65)
     * @param salary             зарплата (> 0)
     * @param experience         стаж (>= 0, не більше age - 18)
     * @param position           посада (не null)
     * @param department         відділ (не null)
     * @param university         назва університету (не порожня)
     * @param internshipDuration тривалість стажування в місяцях (1–12)
     * @throws InvalidEmployeeDataException якщо будь-який параметр некоректний
     */
    public InternEmployee(String name, int age, double salary, int experience,
                          Position position, Department department,
                          String university, int internshipDuration) {
        super(name, age, salary, experience, position, department);
        setUniversity(university);
        setInternshipDuration(internshipDuration);
    }

    /** @return назва університету */
    public String getUniversity() {
        return university;
    }

    /** @return тривалість стажування в місяцях */
    public int getInternshipDuration() {
        return internshipDuration;
    }

    /**
     * Встановлює назву університету.
     *
     * @param university непорожній рядок
     * @throws InvalidEmployeeDataException якщо university порожній або null
     */
    public void setUniversity(String university) {
        if (university == null || university.isBlank()) {
            throw new InvalidEmployeeDataException("Назва університету не може бути порожньою.");
        }
        this.university = university.trim();
    }

    /**
     * Встановлює тривалість стажування.
     *
     * @param internshipDuration ціле число від 1 до 12
     * @throws InvalidEmployeeDataException якщо тривалість поза діапазоном
     */
    public void setInternshipDuration(int internshipDuration) {
        if (internshipDuration < 1 || internshipDuration > 12) {
            throw new InvalidEmployeeDataException(
                    "Тривалість стажування має бути від 1 до 12 місяців. Отримано: " + internshipDuration);
        }
        this.internshipDuration = internshipDuration;
    }

    /** @return рядкове представлення об'єкта */
    @Override
    public String toString() {
        return String.format(
                "{ПІБ='%s', вік=%d, зарплата=%.2f грн, стаж=%d р., посада='%s', відділ='%s', університет='%s', стажування=%d міс.}",
                getName(), getAge(), getSalary(), getExperience(),
                getPosition(), getDepartment(), university, internshipDuration);
    }
}