package by.planner.model;

public enum TaskType {
    READING("Чтение", "Страницы"),
    PRACTICE("Практика", "Упражнения");

    private final String label;
    private final String unit;

    TaskType(String label, String unit) {
        this.label = label;
        this.unit = unit;
    }

    public String unit() {
        return unit;
    }

    @Override
    public String toString() {
        return label;
    }
}
