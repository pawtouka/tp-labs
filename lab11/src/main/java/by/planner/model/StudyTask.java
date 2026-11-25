package by.planner.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public abstract sealed class StudyTask {
    public record Details(String title, LocalDate dueDate, Priority priority, int units) {
        public Details {
            title = Objects.requireNonNull(title).strip();
            Objects.requireNonNull(dueDate);
            Objects.requireNonNull(priority);
            if (title.isEmpty() || title.length() > 120 || title.codePoints().anyMatch(Character::isISOControl)) {
                throw new IllegalArgumentException("Введите однострочное название длиной от 1 до 120 символов.");
            }
            if (units < 1 || units > 999) {
                throw new IllegalArgumentException("Объём работы должен быть от 1 до 999.");
            }
        }
    }

    private final UUID id;
    private final Details details;
    private final boolean completed;

    private StudyTask(UUID id, Details details, boolean completed) {
        this.id = Objects.requireNonNull(id);
        this.details = Objects.requireNonNull(details);
        this.completed = completed;
    }

    public static StudyTask of(UUID id, TaskType type, Details details, boolean completed) {
        return switch (type) {
            case READING -> new ReadingTask(id, details, completed);
            case PRACTICE -> new PracticeTask(id, details, completed);
        };
    }

    public UUID id() { return id; }
    public Details details() { return details; }
    public boolean completed() { return completed; }
    public abstract TaskType type();
    public abstract int estimatedMinutes();

    private static final class ReadingTask extends StudyTask {
        private ReadingTask(UUID id, Details details, boolean completed) {
            super(id, details, completed);
        }

        @Override
        public TaskType type() { return TaskType.READING; }

        @Override
        public int estimatedMinutes() { return details().units() * 3; }
    }

    private static final class PracticeTask extends StudyTask {
        private PracticeTask(UUID id, Details details, boolean completed) {
            super(id, details, completed);
        }

        @Override
        public TaskType type() { return TaskType.PRACTICE; }

        @Override
        public int estimatedMinutes() { return details().units() * 12; }
    }
}
