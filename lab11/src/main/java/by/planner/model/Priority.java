package by.planner.model;

public enum Priority {
    LOW("Низкий"), NORMAL("Обычный"), HIGH("Высокий");

    private final String label;

    Priority(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
