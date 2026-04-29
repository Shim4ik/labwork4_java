/**
 * Контактна інформація працівника.
 * Агрегується класом {@link Employee}.
 */
public class ContactInfo {

    private String email;
    private String phone;

    /**
     * Створює контактну інформацію з перевіркою.
     *
     * @param email електронна пошта (не порожня, має містити @)
     * @param phone номер телефону (не порожній)
     * @throws InvalidEmployeeDataException якщо дані некоректні
     */
    public ContactInfo(String email, String phone) {
        setEmail(email);
        setPhone(phone);
    }

    /**
     * Конструктор копіювання.
     *
     * @param other об'єкт для копіювання
     */
    public ContactInfo(ContactInfo other) {
        if (other == null) {
            throw new InvalidEmployeeDataException("ContactInfo для копіювання не може бути null.");
        }
        this.email = other.email;
        this.phone = other.phone;
    }

    /** @return електронна пошта */
    public String getEmail() { return email; }

    /** @return номер телефону */
    public String getPhone() { return phone; }

    /**
     * Встановлює email.
     *
     * @param email рядок, що містить символ @
     * @throws InvalidEmployeeDataException якщо email некоректний
     */
    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidEmployeeDataException("Email не може бути порожнім.");
        }
        if (!email.contains("@")) {
            throw new InvalidEmployeeDataException("Email має містити символ '@'. Отримано: " + email);
        }
        this.email = email.trim();
    }

    /**
     * Встановлює номер телефону.
     *
     * @param phone непорожній рядок
     * @throws InvalidEmployeeDataException якщо phone порожній або null
     */
    public void setPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new InvalidEmployeeDataException("Номер телефону не може бути порожнім.");
        }
        this.phone = phone.trim();
    }

    @Override
    public String toString() {
        return String.format("email='%s', тел='%s'", email, phone);
    }
}