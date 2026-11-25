package by.planner.model;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class PlannerService {
    public record Summary(int total, int completed, long remainingMinutes) { }

    private final TaskRepository repository;
    private final Map<UUID, StudyTask> tasks = new LinkedHashMap<>();

    public PlannerService(TaskRepository repository) throws IOException {
        this.repository = repository;
        for (var task : repository.load()) {
            if (tasks.putIfAbsent(task.id(), task) != null) {
                throw new IOException("Повтор идентификатора задачи");
            }
        }
    }

    public List<StudyTask> list(TaskOrder order) {
        return tasks.values().stream().sorted(order).toList();
    }

    public UUID add(TaskType type, StudyTask.Details details) throws IOException {
        UUID id = UUID.randomUUID();
        store(StudyTask.of(id, type, details, false));
        return id;
    }

    public void update(UUID id, TaskType type, StudyTask.Details details) throws IOException {
        store(StudyTask.of(id, type, details, lookup(id).completed()));
    }

    public void toggle(UUID id) throws IOException {
        var previous = lookup(id);
        store(StudyTask.of(id, previous.type(), previous.details(), !previous.completed()));
    }

    public void remove(UUID id) throws IOException {
        lookup(id);
        var changed = new LinkedHashMap<>(tasks);
        changed.remove(id);
        repository.save(List.copyOf(changed.values()));
        tasks.remove(id);
    }

    public Summary summary() {
        int completed = 0;
        long remaining = 0;
        for (StudyTask task : tasks.values()) {
            if (task.completed()) {
                completed++;
            } else {
                remaining += task.estimatedMinutes();
            }
        }
        return new Summary(tasks.size(), completed, remaining);
    }

    private StudyTask lookup(UUID id) {
        var task = tasks.get(id);
        if (task == null) {
            throw new IllegalArgumentException("Задача не найдена.");
        }
        return task;
    }

    private void store(StudyTask task) throws IOException {
        var changed = new LinkedHashMap<>(tasks);
        changed.put(task.id(), task);
        repository.save(List.copyOf(changed.values()));
        tasks.put(task.id(), task);
    }
}
