import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * Клас для роботи з базою даних PostgreSQL.
 * Відповідає за підключення та збереження об'єктів Employee у таблицю employees.
 * Параметри підключення зчитуються з файлу конфігурації (app.properties).
 * Шлях до файлу передається через аргументи командного рядка.
 */
public class DatabaseManager {

    /** SQL-запит для вставки запису працівника. */
    private static final String INSERT_SQL =
            "INSERT INTO employees " +
            "(type, name, age, salary, experience, position, department, quantity, " +
            " bonus_percentage, contract_months, country, hourly_rate, university, intern_duration) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final String url;
    private final String user;
    private final String password;

    /**
     * Створює менеджер БД, зчитуючи параметри підключення з файлу конфігурації.
     *
     * @param propertiesFilePath шлях до файлу app.properties
     * @throws IOException якщо файл не знайдено або не вдається прочитати
     */
    public DatabaseManager(String propertiesFilePath) throws IOException {
        Properties props = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(propertiesFilePath);
            props.load(fis);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignored) {
                }
            }
        }

        this.url      = props.getProperty("db.url");
        this.user     = props.getProperty("db.user");
        this.password = props.getProperty("db.password");

        if (url == null || url.isBlank()) {
            throw new IOException("Параметр db.url відсутній у файлі конфігурації: " + propertiesFilePath);
        }
        if (this.user == null || this.user.isBlank()) {
            throw new IOException("Параметр db.user відсутній у файлі конфігурації: " + propertiesFilePath);
        }
        if (password == null) {
            throw new IOException("Параметр db.password відсутній у файлі конфігурації: " + propertiesFilePath);
        }
    }

    /**
     * Відкриває з'єднання з базою даних.
     *
     * @return об'єкт {@link Connection}
     * @throws SQLException якщо підключення не вдалося
     */
    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Зберігає один запис Employee (разом із кількістю) у таблицю employees.
     * Тип об'єкта визначається через {@code getClass().getSimpleName()}.
     *
     * @param record запис із об'єктом Employee та кількістю
     * @throws SQLException якщо виникла помилка під час виконання SQL
     */
    public void saveEmployee(Company.EmployeeRecord record) throws SQLException {
        Employee emp      = record.getEmployee();
        int      quantity = record.getQuantity();

        String type = emp.getClass().getSimpleName();

        // Специфічні поля підкласів (null для тих, що не підходять)
        Double  bonusPercentage = null;
        Integer contractMonths  = null;
        String  country         = null;
        Double  hourlyRate      = null;
        String  university      = null;
        Integer internDuration  = null;

        if (emp instanceof FullTimeEmployee) {
            bonusPercentage = ((FullTimeEmployee) emp).getBonusPercentage();
        } else if (emp instanceof ContractEmployee) {
            contractMonths = ((ContractEmployee) emp).getContractDurationMonths();
        } else if (emp instanceof RemoteEmployee) {
            country    = ((RemoteEmployee) emp).getCountry();
            hourlyRate = ((RemoteEmployee) emp).getHourlyRate();
        } else if (emp instanceof InternEmployee) {
            university     = ((InternEmployee) emp).getUniversity();
            internDuration = ((InternEmployee) emp).getInternshipDuration();
        }

        Connection        conn = null;
        PreparedStatement stmt = null;
        try {
            conn = openConnection();
            stmt = conn.prepareStatement(INSERT_SQL);

            stmt.setString(1,  type);
            stmt.setString(2,  emp.getName());
            stmt.setInt(3,     emp.getAge());
            stmt.setDouble(4,  emp.getSalary());
            stmt.setInt(5,     emp.getExperience());
            stmt.setString(6,  emp.getPosition().name());
            stmt.setString(7,  emp.getDepartment().name());
            stmt.setInt(8,     quantity);

            // Nullable поля підкласів
            if (bonusPercentage != null) {
                stmt.setDouble(9, bonusPercentage);
            } else {
                stmt.setNull(9, java.sql.Types.NUMERIC);
            }

            if (contractMonths != null) {
                stmt.setInt(10, contractMonths);
            } else {
                stmt.setNull(10, java.sql.Types.INTEGER);
            }

            if (country != null) {
                stmt.setString(11, country);
            } else {
                stmt.setNull(11, java.sql.Types.VARCHAR);
            }

            if (hourlyRate != null) {
                stmt.setDouble(12, hourlyRate);
            } else {
                stmt.setNull(12, java.sql.Types.NUMERIC);
            }

            if (university != null) {
                stmt.setString(13, university);
            } else {
                stmt.setNull(13, java.sql.Types.VARCHAR);
            }

            if (internDuration != null) {
                stmt.setInt(14, internDuration);
            } else {
                stmt.setNull(14, java.sql.Types.INTEGER);
            }

            stmt.executeUpdate();

        } finally {
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException ignored) {}
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Зберігає всі записи компанії у базу даних.
     * Виводить повідомлення про кожен збережений запис та помилки.
     *
     * @param records список записів для збереження
     */
    public void saveAll(List<Company.EmployeeRecord> records) {
        int saved  = 0;
        int failed = 0;

        for (Company.EmployeeRecord record : records) {
            try {
                saveEmployee(record);
                System.out.println("[DB] Збережено: " + record.getEmployee().getName()
                        + " (" + record.getEmployee().getClass().getSimpleName() + ")");
                saved++;
            } catch (SQLException e) {
                System.err.println("[DB] Помилка збереження '"
                        + record.getEmployee().getName() + "': " + e.getMessage());
                failed++;
            }
        }

        System.out.println("[DB] Результат: збережено=" + saved + ", помилок=" + failed);
    }

    /**
     * Перевіряє з'єднання з БД (при старті програми).
     *
     * @return {@code true} якщо підключення успішне
     */
    public boolean testConnection() {
        Connection conn = null;
        try {
            conn = openConnection();
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] Помилка підключення: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }
}
