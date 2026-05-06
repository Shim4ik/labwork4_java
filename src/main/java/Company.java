import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Клас представляє компанію, яка зберігає колекцію працівників.
 * Підтримує додавання (з урахуванням дублікатів) та пошук за різними критеріями.
 */
public class Company {

    private String name;        // назва компанії
    private String industry;    // сфера діяльності
    private int foundedYear;    // рік заснування

    /**
     * Колекція записів про працівників.
     * Кожен запис містить об'єкт Employee та кількість таких працівників.
     */
    private final ArrayList<EmployeeRecord> records = new ArrayList<>();

    /**
     * Внутрішній клас-запис: пара (Employee, кількість).
     */
    public static class EmployeeRecord {
        private final Employee employee;
        private int quantity;

        public EmployeeRecord(Employee employee, int quantity) {
            this.employee = employee;
            this.quantity = quantity;
        }

        public Employee getEmployee() { return employee; }
        public int getQuantity()      { return quantity; }

        public void addQuantity(int amount) { this.quantity += amount; }

        /** Скорочений рядок для списку GUI */
        public String toShortString() {
            Employee e = employee;
            return String.format("[%s] %s | UUID: %s...",
                    e.getClass().getSimpleName(),
                    e.getName(),
                    e.getUuid().toString().substring(0, 8));
        }

        @Override
        public String toString() {
            return String.format("[%s] %s  (кількість: %d)",
                    employee.getClass().getSimpleName(), employee, quantity);
        }
    }


    /**
     * Створює компанію з базовими параметрами.
     *
     * @param name        назва компанії (не порожня)
     * @param industry    сфера діяльності (не порожня)
     * @param foundedYear рік заснування (>= 1800)
     * @throws InvalidEmployeeDataException якщо параметри некоректні
     */
    public Company(String name, String industry, int foundedYear) {
        setName(name);
        setIndustry(industry);
        setFoundedYear(foundedYear);
    }

    // --- Геттери / сеттери ---

    public String getName()      { return name; }
    public String getIndustry()  { return industry; }
    public int getFoundedYear()  { return foundedYear; }

    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new InvalidEmployeeDataException("Назва компанії не може бути порожньою.");
        this.name = name.trim();
    }

    public void setIndustry(String industry) {
        if (industry == null || industry.isBlank())
            throw new InvalidEmployeeDataException("Сфера діяльності не може бути порожньою.");
        this.industry = industry.trim();
    }

    public void setFoundedYear(int foundedYear) {
        if (foundedYear < 1800)
            throw new InvalidEmployeeDataException(
                    "Рік заснування не може бути меншим за 1800. Отримано: " + foundedYear);
        this.foundedYear = foundedYear;
    }

    /**
     * Повертає незмінний список усіх записів.
     *
     * @return список {@link EmployeeRecord}
     */
    public List<EmployeeRecord> getRecords() {
        return new ArrayList<>(records);
    }

    /**
     * Додає працівника до колекції.
     * <p>
     * Якщо рівноцінний об'єкт (той самий тип + те саме ім'я) вже існує —
     * збільшує його кількість на {@code quantity}. Інакше створює новий запис.
     *
     * @param emp      об'єкт працівника (не null)
     * @param quantity кількість (>= 1)
     * @throws InvalidEmployeeDataException якщо параметри некоректні
     */
    public void addNewEmployee(Employee emp, int quantity) {
        if (emp == null)
            throw new InvalidEmployeeDataException("Працівник не може бути null.");
        if (quantity < 1)
            throw new InvalidEmployeeDataException(
                    "Кількість має бути >= 1. Отримано: " + quantity);

        // Шукаємо існуючий запис із таким самим типом та ім'ям
        for (EmployeeRecord record : records) {
            if (isSameEmployee(record.getEmployee(), emp)) {
                record.addQuantity(quantity);
                return;
            }
        }
        // Новий запис
        records.add(new EmployeeRecord(emp, quantity));
    }

    /**
     * Визначає, чи є два об'єкти Employee «однаковими» для цілей дедуплікації:
     * однаковий клас, однакове ім'я (без урахування регістру), посаду та відділ.
     */
    private boolean isSameEmployee(Employee a, Employee b) {
        return a.getClass().equals(b.getClass())
                && a.getName().equalsIgnoreCase(b.getName())
                && a.getPosition() == b.getPosition()
                && a.getDepartment() == b.getDepartment();
    }

    /**
     * Знаходить запис за UUID працівника.
     *
     * @param uuid UUID для пошуку
     * @return знайдений запис або null
     */
    public EmployeeRecord findByUuid(UUID uuid) {
        for (EmployeeRecord record : records) {
            if (record.getEmployee().getUuid().equals(uuid)) {
                return record;
            }
        }
        return null;
    }

    /**
     * Знаходить усіх працівників, чия зарплата знаходиться в межах [min, max].
     *
     * @param min мінімальна зарплата
     * @param max максимальна зарплата
     * @return список знайдених записів (може бути порожнім)
     */
    public List<EmployeeRecord> findBySalaryRange(double min, double max) {
        List<EmployeeRecord> result = new ArrayList<>();
        for (EmployeeRecord record : records) {
            double salary = record.getEmployee().getSalary();
            if (salary >= min && salary <= max) {
                result.add(record);
            }
        }
        return result;
    }

    /**
     * Знаходить усіх працівників із заданою посадою.
     *
     * @param position посада для фільтрації
     * @return список знайдених записів (може бути порожнім)
     */
    public List<EmployeeRecord> findByPosition(Position position) {
        List<EmployeeRecord> result = new ArrayList<>();
        for (EmployeeRecord record : records) {
            if (record.getEmployee().getPosition() == position) {
                result.add(record);
            }
        }
        return result;
    }

    /**
     * Знаходить усіх працівників із заданого відділу.
     *
     * @param department відділ для фільтрації
     * @return список знайдених записів (може бути порожнім)
     */
    public List<EmployeeRecord> findByDepartment(Department department) {
        List<EmployeeRecord> result = new ArrayList<>();
        for (EmployeeRecord record : records) {
            if (record.getEmployee().getDepartment() == department) {
                result.add(record);
            }
        }
        return result;
    }

    /**
     * Оновлює дані існуючого запису працівника.
     * Пошук виконується за UUID. Оновлюються всі поля через сеттери з валідацією,
     * а також специфічні поля підкласу (якщо типи збігаються).
     *
     * @param existingObject об'єкт, що потрібно оновити (не null)
     * @param newObject      об'єкт з новими даними (не null, той самий підклас)
     * @return {@code true} якщо оновлення виконано, {@code false} якщо не знайдено
     * @throws InvalidEmployeeDataException якщо параметри null або типи різняться
     */
    public boolean update(Employee existingObject, Employee newObject) {
        if (existingObject == null || newObject == null)
            throw new InvalidEmployeeDataException("Об'єкти не можуть бути null.");
        if (!existingObject.getClass().equals(newObject.getClass()))
            throw new InvalidEmployeeDataException(
                    "Типи об'єктів мають збігатися. Існуючий: "
                            + existingObject.getClass().getSimpleName()
                            + ", новий: " + newObject.getClass().getSimpleName());

        for (EmployeeRecord record : records) {
            if (record.getEmployee().getUuid().equals(existingObject.getUuid())) {
                Employee target = record.getEmployee();
                // Спільні поля
                target.setName(newObject.getName());
                target.setAge(newObject.getAge());
                target.setSalary(newObject.getSalary());
                target.setExperience(newObject.getExperience());
                target.setPosition(newObject.getPosition());
                target.setDepartment(newObject.getDepartment());
                // Специфічні поля підкласів
                if (target instanceof RemoteEmployee re && newObject instanceof RemoteEmployee rn) {
                    re.setCountry(rn.getCountry());
                    re.setHourlyRate(rn.getHourlyRate());
                } else if (target instanceof InternEmployee ie && newObject instanceof InternEmployee in) {
                    ie.setUniversity(in.getUniversity());
                    ie.setInternshipDuration(in.getInternshipDuration());
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Видаляє запис працівника з колекції за UUID.
     *
     * @param existingObject об'єкт для видалення (не null)
     * @return {@code true} якщо видалення виконано, {@code false} якщо не знайдено
     * @throws InvalidEmployeeDataException якщо existingObject є null
     */
    public boolean delete(Employee existingObject) {
        if (existingObject == null)
            throw new InvalidEmployeeDataException("Об'єкт для видалення не може бути null.");

        java.util.Iterator<EmployeeRecord> it = records.iterator();
        while (it.hasNext()) {
            if (it.next().getEmployee().getUuid().equals(existingObject.getUuid())) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Виведення
    // -------------------------------------------------------------------------

    /**
     * Повертає загальну кількість працівників (сума quantity всіх записів).
     */
    public int getTotalEmployeeCount() {
        int total = 0;
        for (EmployeeRecord record : records) {
            total += record.getQuantity();
        }
        return total;
    }

    @Override
    public String toString() {
        return String.format(
                "Компанія '%s' | Сфера: %s | Засновано: %d | Записів: %d | Всього працівників: %d",
                name, industry, foundedYear, records.size(), getTotalEmployeeCount());
    }
}