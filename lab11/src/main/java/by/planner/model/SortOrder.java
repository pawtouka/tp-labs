package by.planner.model;

import java.util.Comparator;

public enum SortOrder implements TaskOrder {
    DEADLINE("По сроку", Comparator.comparing(task -> task.details().dueDate())),
    PRIORITY("По приоритету", Comparator.<StudyTask, Priority>comparing(task -> task.details().priority())
            .reversed().thenComparing(task -> task.details().dueDate()));

    private final String label;
    private final Comparator<StudyTask> comparator;

    SortOrder(String label, Comparator<StudyTask> comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    @Override
    public int compare(StudyTask first, StudyTask second) {
        return comparator.compare(first, second);
    }

    @Override
    public String toString() {
        return label;
    }
}
