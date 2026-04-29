/**
 * Перерахування відділів підприємства.
 */
public enum Department {
    IT("ІТ"),
    HR("HR"),
    FINANCE("Фінанси"),
    ANALYTICS("Аналітика"),
    UX("UX"),
    ENGINEERING("Інженерія"),
    MARKETING("Маркетинг");

    private final String displayName;

    /**
     * @param displayName назва відділу
     */
    Department(String displayName) {
        this.displayName = displayName;
    }

    /** @return назва відділу для відображення */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}