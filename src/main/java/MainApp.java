import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.*;
import java.util.UUID;

public class MainApp extends Application {

    private static final String DATA_FILE = "input.txt";

    private Company company;
    private final ObservableList<String> employeeShortList = FXCollections.observableArrayList();

    private ComboBox<String>     typeCombo;
    private TextField            nameField, ageField, salaryField, experienceField;
    private ComboBox<Position>   positionCombo;
    private ComboBox<Department> departmentCombo;
    private TextField            extraField1, extraField2;
    private Label                extraLabel1, extraLabel2, addStatusLabel, statusBar;
    private TextField            uuidField;
    private TextArea             uuidResultArea;

    @Override
    public void start(Stage primaryStage) {
        company = loadFromFile();
        primaryStage.setTitle("Система управління компанією — " + company.getName());

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(
                new Tab("➕ Додати працівника", buildAddTab()),
                new Tab("🔍 Пошук за UUID",    buildSearchTab())
        );

        HBox bottomBar = buildBottomBar(primaryStage);
        VBox root = new VBox(tabPane, bottomBar);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Scene scene = new Scene(root, 920, 700);
        applyStyles(scene);
        primaryStage.setOnCloseRequest(e -> saveToFile());
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(780);
        primaryStage.setMinHeight(580);
        primaryStage.show();

        refreshList();
        setStatus("Завантажено з " + DATA_FILE + ": " + company.getRecords().size()
                + " записів | Всього працівників: " + company.getTotalEmployeeCount(), false);
    }

    // ── Нижня панель ──────────────────────────────────────────────────────────

    private HBox buildBottomBar(Stage stage) {
        statusBar = new Label("Готово");
        statusBar.setStyle("-fx-text-fill: #555; -fx-font-size: 12px;");
        HBox.setHgrow(statusBar, Priority.ALWAYS);

        Button exitBtn = new Button("💾  Зберегти та завершити");
        exitBtn.getStyleClass().add("btn-exit");
        exitBtn.setOnAction(e -> { saveToFile(); stage.close(); Platform.exit(); });

        HBox bar = new HBox(16, statusBar, exitBtn);
        bar.setPadding(new Insets(8, 14, 8, 14));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1 0 0 0;");
        return bar;
    }

    // ── Вкладка 1: Форма + список ─────────────────────────────────────────────

    private BorderPane buildAddTab() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));
        VBox formBox = buildAddForm();
        formBox.setPrefWidth(410);
        HBox content = new HBox(20, formBox, buildEmployeeList());
        HBox.setHgrow(content.getChildren().get(1), Priority.ALWAYS);
        root.setCenter(content);
        return root;
    }

    private VBox buildAddForm() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(0, 10, 0, 0));
        box.getChildren().add(styledTitle("Новий працівник"));

        typeCombo = new ComboBox<>(FXCollections.observableArrayList(
                "FullTimeEmployee","ContractEmployee","RemoteEmployee","InternEmployee"));
        typeCombo.setValue("FullTimeEmployee");
        typeCombo.setMaxWidth(Double.MAX_VALUE);
        typeCombo.setOnAction(e -> updateExtraFields());

        nameField       = tf("Іваненко Іван Іванович");
        ageField        = tf("25");
        salaryField     = tf("20000");
        experienceField = tf("3");

        positionCombo   = new ComboBox<>(FXCollections.observableArrayList(Position.values()));
        positionCombo.setValue(Position.DEVELOPER);
        positionCombo.setMaxWidth(Double.MAX_VALUE);

        departmentCombo = new ComboBox<>(FXCollections.observableArrayList(Department.values()));
        departmentCombo.setValue(Department.IT);
        departmentCombo.setMaxWidth(Double.MAX_VALUE);

        extraLabel1 = new Label("Відсоток премії (0–50):");
        extraField1 = tf("10");
        extraLabel2 = new Label();
        extraField2 = tf("");
        show(extraLabel2, false); show(extraField2, false);

        addStatusLabel = new Label();
        addStatusLabel.setWrapText(true);
        addStatusLabel.setMaxWidth(390);

        Button addBtn = new Button("✔  Додати працівника");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> handleAdd());

        box.getChildren().addAll(
                label("Тип:"), typeCombo,
                label("ПІБ:"), nameField,
                label("Вік:"), ageField,
                label("Зарплата (грн):"), salaryField,
                label("Стаж (років):"), experienceField,
                label("Посада:"), positionCombo,
                label("Відділ:"), departmentCombo,
                extraLabel1, extraField1, extraLabel2, extraField2,
                addBtn, addStatusLabel
        );
        return box;
    }

    private void updateExtraFields() {
        switch (typeCombo.getValue()) {
            case "FullTimeEmployee" -> {
                extraLabel1.setText("Відсоток премії (0–50):"); extraField1.setPromptText("10");
                show(extraLabel1,true); show(extraField1,true);
                show(extraLabel2,false); show(extraField2,false);
            }
            case "ContractEmployee" -> {
                extraLabel1.setText("Тривалість контракту (міс., 1–60):"); extraField1.setPromptText("12");
                show(extraLabel1,true); show(extraField1,true);
                show(extraLabel2,false); show(extraField2,false);
            }
            case "RemoteEmployee" -> {
                extraLabel1.setText("Країна проживання:"); extraField1.setPromptText("Україна");
                extraLabel2.setText("Погодинна ставка ($/год):"); extraField2.setPromptText("15");
                show(extraLabel1,true); show(extraField1,true);
                show(extraLabel2,true); show(extraField2,true);
            }
            case "InternEmployee" -> {
                extraLabel1.setText("Університет:"); extraField1.setPromptText("КПІ ім. Ігоря Сікорського");
                extraLabel2.setText("Тривалість стажування (міс., 1–12):"); extraField2.setPromptText("6");
                show(extraLabel1,true); show(extraField1,true);
                show(extraLabel2,true); show(extraField2,true);
            }
        }
    }

    private void handleAdd() {
        addStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
        try {
            String name   = nameField.getText().trim();
            int    age    = pi(ageField, "Вік");
            double salary = pd(salaryField, "Зарплата");
            int    exp    = pi(experienceField, "Стаж");
            Position   pos = positionCombo.getValue();
            Department dep = departmentCombo.getValue();

            Employee emp = switch (typeCombo.getValue()) {
                case "FullTimeEmployee" ->
                        new FullTimeEmployee(name, age, salary, exp, pos, dep, pd(extraField1,"Відсоток премії"));
                case "ContractEmployee" ->
                        new ContractEmployee(name, age, salary, exp, pos, dep, pi(extraField1,"Тривалість контракту"));
                case "RemoteEmployee"   ->
                        new RemoteEmployee(name, age, salary, exp, pos, dep,
                                extraField1.getText().trim(), pd(extraField2,"Погодинна ставка"));
                case "InternEmployee"   ->
                        new InternEmployee(name, age, salary, exp, pos, dep,
                                extraField1.getText().trim(), pi(extraField2,"Тривалість стажування"));
                default -> throw new InvalidEmployeeDataException("", "Невідомий тип.");
            };

            company.addNewEmployee(emp, 1);
            refreshList();
            addStatusLabel.setStyle("-fx-text-fill: #27ae60;");
            addStatusLabel.setText("✔ Додано: " + emp.getName() + "\nUUID: " + emp.getUuid());
            setStatus("Додано: " + emp.getName() + " | Всього: " + company.getTotalEmployeeCount(), false);
            clearForm();
        } catch (InvalidEmployeeDataException ex) {
            addStatusLabel.setText("⚠ " + ex.getMessage());
        }
    }

    private VBox buildEmployeeList() {
        VBox box = new VBox(10);
        box.getChildren().add(styledTitle("Колекція працівників"));

        ListView<String> lv = new ListView<>(employeeShortList);
        lv.setPlaceholder(new Label("Список порожній — додайте першого працівника"));
        VBox.setVgrow(lv, Priority.ALWAYS);

        // Поле для відображення повного UUID обраного запису
        Label uuidLabel = label("UUID обраного працівника:");
        TextField selectedUuidField = new TextField();
        selectedUuidField.setEditable(false);          // лише для читання і копіювання
        selectedUuidField.setPromptText("Оберіть працівника зі списку...");
        selectedUuidField.setFont(Font.font("Monospaced", 12));
        selectedUuidField.setStyle("-fx-background-color: #f0f0f0;");

        // При кліці на рядок — показуємо повний UUID
        lv.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            int idx = lv.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= company.getRecords().size()) return;
            Company.EmployeeRecord rec = company.getRecords().get(idx);
            selectedUuidField.setText(rec.getEmployee().getUuid().toString());
        });

        box.getChildren().addAll(lv, uuidLabel, selectedUuidField);
        return box;
    }

    // ── Вкладка 2: Пошук за UUID ──────────────────────────────────────────────

    private VBox buildSearchTab() {
        VBox box = new VBox(14);
        box.setPadding(new Insets(20));
        box.setMaxWidth(720);
        box.getChildren().add(styledTitle("Пошук за UUID"));

        uuidField = new TextField();
        uuidField.setPromptText("напр. 550e8400-e29b-41d4-a716-446655440000");
        uuidField.setFont(Font.font("Monospaced", 13));
        uuidField.setOnAction(e -> handleSearch());

        Button searchBtn = new Button("🔍  Знайти");
        searchBtn.getStyleClass().add("btn-primary");
        searchBtn.setOnAction(e -> handleSearch());

        HBox row = new HBox(10, uuidField, searchBtn);
        HBox.setHgrow(uuidField, Priority.ALWAYS);
        row.setAlignment(Pos.CENTER_LEFT);

        uuidResultArea = new TextArea();
        uuidResultArea.setEditable(false);
        uuidResultArea.setWrapText(true);
        uuidResultArea.setPrefHeight(300);
        uuidResultArea.setFont(Font.font("Monospaced", 13));
        uuidResultArea.setPromptText("Тут з'явиться повна інформація...");

        box.getChildren().addAll(
                label("Введіть UUID (формат: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx):"),
                row, label("Результат:"), uuidResultArea);
        return box;
    }

    private void handleSearch() {
        String input = uuidField.getText().trim();
        if (input.isEmpty()) { searchResult("⚠ Поле порожнє.", false); return; }
        UUID uuid;
        try {
            uuid = UUID.fromString(input);
        } catch (IllegalArgumentException e) {
            searchResult("❌ Некоректний формат UUID.\n\nОчікується: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx\nВведено: \"" + input + "\"", false);
            return;
        }
        Company.EmployeeRecord rec = company.findByUuid(uuid);
        if (rec == null) {
            searchResult("🔍 Працівника з UUID\n" + uuid + "\nне знайдено.", false);
        } else {
            searchResult("✔ Знайдено!\n\n" + rec.getEmployee().toString()
                    + "\n\nКількість у реєстрі: " + rec.getQuantity(), true);
        }
    }

    private void searchResult(String text, boolean ok) {
        uuidResultArea.setText(text);
        uuidResultArea.setStyle(ok ? "-fx-text-fill: #27ae60;" : "-fx-text-fill: #c0392b;");
    }

    // ── Завантаження з файлу ──────────────────────────────────────────────────

    private Company loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("[GUI] " + DATA_FILE + " не знайдено. Порожня компанія.");
            return new Company("МояКомпанія", "ІТ", 2010);
        }
        Company loaded = null;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line; int n = 0;
            while ((line = br.readLine()) != null) {
                n++; line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                if (line.startsWith("Company|")) {
                    try { loaded = parseCompany(line); }
                    catch (Exception e) { System.out.println("[GUI] Рядок " + n + ": " + e.getMessage()); }
                } else if (loaded != null) {
                    try { parseAndAdd(line, loaded); }
                    catch (Exception e) { System.out.println("[GUI] Пропущено рядок " + n + ": " + e.getMessage()); }
                }
            }
        } catch (IOException e) {
            System.out.println("[GUI] Помилка читання: " + e.getMessage());
        }
        return loaded != null ? loaded : new Company("МояКомпанія", "ІТ", 2010);
    }

    private Company parseCompany(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 4) throw new IllegalArgumentException("Неповний рядок компанії.");
        return new Company(p[1].trim(), p[2].trim(), Integer.parseInt(p[3].trim()));
    }

    private void parseAndAdd(String line, Company target) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 8) throw new IllegalArgumentException("Недостатньо полів.");
        int qty = Integer.parseInt(parts[parts.length - 1].trim());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) { if (i > 0) sb.append("|"); sb.append(parts[i]); }
        target.addNewEmployee(parseEmp(sb.toString()), qty);
    }

    private Employee parseEmp(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 7) throw new IllegalArgumentException("Недостатньо полів.");
        String type = p[0].trim(); String name = p[1].trim();
        int age = Integer.parseInt(p[2].trim()); double salary = Double.parseDouble(p[3].trim());
        int exp = Integer.parseInt(p[4].trim());
        Position pos = parsePos(p[5].trim()); Department dep = parseDep(p[6].trim());
        return switch (type) {
            case "FullTimeEmployee" -> new FullTimeEmployee(name, age, salary, exp, pos, dep, Double.parseDouble(p[7].trim()));
            case "ContractEmployee" -> new ContractEmployee(name, age, salary, exp, pos, dep, Integer.parseInt(p[7].trim()));
            case "RemoteEmployee"   -> new RemoteEmployee(name, age, salary, exp, pos, dep, p[7].trim(), Double.parseDouble(p[8].trim()));
            case "InternEmployee"   -> new InternEmployee(name, age, salary, exp, pos, dep, p[7].trim(), Integer.parseInt(p[8].trim()));
            case "Employee"         -> new FullTimeEmployee(name, age, salary, exp, pos, dep, 0);
            default -> throw new IllegalArgumentException("Невідомий тип: " + type);
        };
    }

    private Position parsePos(String s) {
        try { return Position.valueOf(s); } catch (IllegalArgumentException ignored) {}
        for (Position p : Position.values()) if (p.getDisplayName().equalsIgnoreCase(s)) return p;
        throw new IllegalArgumentException("Невідома посада: " + s);
    }

    private Department parseDep(String s) {
        try { return Department.valueOf(s); } catch (IllegalArgumentException ignored) {}
        for (Department d : Department.values()) if (d.getDisplayName().equalsIgnoreCase(s)) return d;
        throw new IllegalArgumentException("Невідомий відділ: " + s);
    }

    // ── Збереження у файл ─────────────────────────────────────────────────────

    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(DATA_FILE), "UTF-8"))) {
            pw.println("Company|" + company.getName() + "|" + company.getIndustry() + "|" + company.getFoundedYear());
            for (Company.EmployeeRecord rec : company.getRecords()) pw.println(toLine(rec));
            int c = company.getRecords().size();
            System.out.println("[GUI] Збережено " + c + " записів у " + DATA_FILE);
            setStatus("✔ Збережено " + c + " записів у " + DATA_FILE, false);
        } catch (IOException e) {
            System.out.println("[GUI] Помилка збереження: " + e.getMessage());
            setStatus("❌ Помилка збереження: " + e.getMessage(), true);
        }
    }

    private String toLine(Company.EmployeeRecord rec) {
        Employee e = rec.getEmployee();
        StringBuilder sb = new StringBuilder();
        sb.append(e.getClass().getSimpleName()).append("|").append(e.getName()).append("|")
                .append(e.getAge()).append("|")
                .append(String.format(java.util.Locale.US, "%.2f", e.getSalary())).append("|")
                .append(e.getExperience()).append("|").append(e.getPosition().name()).append("|").append(e.getDepartment().name());
        if      (e instanceof FullTimeEmployee fte) sb.append("|").append(fte.getBonusPercentage());
        else if (e instanceof ContractEmployee ce)  sb.append("|").append(ce.getContractDurationMonths());
        else if (e instanceof RemoteEmployee re)    sb.append("|").append(re.getCountry()).append("|").append(re.getHourlyRate());
        else if (e instanceof InternEmployee ie)    sb.append("|").append(ie.getUniversity()).append("|").append(ie.getInternshipDuration());
        sb.append("|").append(rec.getQuantity());
        return sb.toString();
    }

    // ── Утиліти ───────────────────────────────────────────────────────────────

    private void refreshList() {
        employeeShortList.clear();
        for (Company.EmployeeRecord r : company.getRecords()) employeeShortList.add(r.toShortString());
    }

    private void clearForm() {
        nameField.clear(); ageField.clear(); salaryField.clear();
        experienceField.clear(); extraField1.clear(); extraField2.clear();
    }

    private void setStatus(String text, boolean error) {
        if (statusBar == null) return;
        statusBar.setText(text);
        statusBar.setStyle((error ? "-fx-text-fill:#e74c3c;" : "-fx-text-fill:#555;") + "-fx-font-size:12px;");
    }

    private void show(javafx.scene.Node n, boolean v) { n.setVisible(v); n.setManaged(v); }

    private TextField tf(String prompt) { TextField f = new TextField(); f.setPromptText(prompt); return f; }

    private int    pi(TextField f, String name) {
        try { return Integer.parseInt(f.getText().trim()); }
        catch (NumberFormatException e) { throw new InvalidEmployeeDataException("", "«"+name+"» — ціле число. Введено: \""+f.getText().trim()+"\""); }
    }
    private double pd(TextField f, String name) {
        try { return Double.parseDouble(f.getText().trim().replace(',','.')); }
        catch (NumberFormatException e) { throw new InvalidEmployeeDataException("", "«"+name+"» — число. Введено: \""+f.getText().trim()+"\""); }
    }

    private Label label(String text) {
        Label l = new Label(text); l.setStyle("-fx-font-size:12px;-fx-text-fill:#34495e;"); return l;
    }
    private Label styledTitle(String text) {
        Label l = new Label(text); l.setFont(Font.font("System", FontWeight.BOLD, 16));
        l.setStyle("-fx-text-fill:#2c3e50;"); return l;
    }

    private void applyStyles(Scene scene) {
        scene.getRoot().setStyle("-fx-background-color:#f5f6fa;");
        scene.getStylesheets().add("data:text/css,"
                + ".btn-primary{-fx-background-color:#3498db;-fx-text-fill:white;-fx-font-weight:bold;"
                + "-fx-padding:8 18 8 18;-fx-background-radius:6;-fx-cursor:hand;}"
                + ".btn-primary:hover{-fx-background-color:#2980b9;}"
                + ".btn-exit{-fx-background-color:#e74c3c;-fx-text-fill:white;-fx-font-weight:bold;"
                + "-fx-padding:6 16 6 16;-fx-background-radius:6;-fx-cursor:hand;}"
                + ".btn-exit:hover{-fx-background-color:#c0392b;}");
    }

    public static void main(String[] args) { launch(args); }
}