/**
 * Клас віддаленого працівника (Remote Employee).
 * Успадковується від базового класу {@link Employee}.
 * Додатково зберігає країну проживання та погодинну ставку.
 */
public class RemoteEmployee extends Employee {

    private String country;          // країна проживання
    private double hourlyRate;       // погодинна ставка в доларах США

    /**
     * Створює віддаленого працівника з перевіркою всіх параметрів.
     *
     * @param name       ПІБ (не порожній)
     * @param age        вік (18–65)
     * @param salary     зарплата (> 0)
     * @param experience стаж (>= 0, не більше age - 18)
     * @param position   посада (не null)
     * @param department відділ (не null)
     * @param country    країна проживання (не порожня)
     * @param hourlyRate погодинна ставка у доларах (> 0)
     * @throws InvalidEmployeeDataException якщо будь-який параметр некоректний
     */
    public RemoteEmployee(String name, int age, double salary, int experience,
                          Position position, Department department,
                          String country, double hourlyRate) {
        super(name, age, salary, experience, position, department);
        setCountry(country);
        setHourlyRate(hourlyRate);
    }

    /** @return країна проживання */
    public String getCountry() {
        return country;
    }

    /** @return погодинна ставка у доларах */
    public double getHourlyRate() {
        return hourlyRate;
    }

    /**
     * Встановлює країну проживання.
     *
     * @param country непорожній рядок
     * @throws InvalidEmployeeDataException якщо country порожній або null
     */
    public void setCountry(String country) {
        if (country == null || country.isBlank()) {
            throw new InvalidEmployeeDataException("Країна проживання не може бути порожньою.");
        }
        this.country = country.trim();
    }

    /**
     * Встановлює погодинну ставку.
     *
     * @param hourlyRate позитивне число
     * @throws InvalidEmployeeDataException якщо ставка <= 0
     */
    public void setHourlyRate(double hourlyRate) {
        if (hourlyRate <= 0) {
            throw new InvalidEmployeeDataException(
                    "Погодинна ставка повинна бути більше нуля. Отримано: " + hourlyRate);
        }
        this.hourlyRate = hourlyRate;
    }

    /** @return рядкове представлення об'єкта */
    @Override
    public String toString() {
        return String.format(
                "{ПІБ='%s', вік=%d, зарплата=%.2f грн, стаж=%d р., посада='%s', відділ='%s', країна='%s', ставка=%.2f$/год}",
                getName(), getAge(), getSalary(), getExperience(),
                getPosition(), getDepartment(), country, hourlyRate);
    }
}