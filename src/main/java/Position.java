/**
 * Перерахування можливих посад працівника підприємства.
 */
public enum Position {
    DEVELOPER("Розробник"),
    MANAGER("Менеджер"),
    ANALYST("Аналітик"),
    DESIGNER("Дизайнер"),
    TESTER("Тестувальник"),
    LEAD("Тімлід"),
    HR("HR-менеджер"),
    INTERN("Стажер");

    private final String displayName;

    /**
     * @param displayName назва посади
     */
    Position(String displayName) {
        this.displayName = displayName;
    }

    /** @return назва посади для відображення */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}