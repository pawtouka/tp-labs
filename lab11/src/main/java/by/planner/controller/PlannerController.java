package by.planner.controller;

import by.planner.model.PlannerService;
import by.planner.model.StudyTask;
import by.planner.view.PlannerView;
import by.planner.view.TaskEditor;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PlannerController {
    private final PlannerService planner;
    private final PlannerView view;
    private final Logger log;

    public PlannerController(PlannerService planner, PlannerView view, Logger log) {
        this.planner = planner;
        this.view = view;
        this.log = log;
        view.bind(() -> edit(null), () -> edit(view.selected()), this::toggle, this::remove,
                () -> view.showSummary(planner.summary()), this::sort);
        refresh(null);
    }

    private void edit(StudyTask task) {
        view.showEditor(task, input -> save(task, input));
    }

    private boolean save(StudyTask task, TaskEditor.Input input) {
        try {
            var details = new StudyTask.Details(input.title(), LocalDate.parse(input.dueDate().strip()),
                    input.priority(), Integer.parseInt(input.units().strip()));
            if (task == null) {
                UUID id = planner.add(input.type(), details);
                log.log(Level.INFO, "Добавлена задача {0}", id);
                refresh(id);
            } else {
                planner.update(task.id(), input.type(), details);
                log.log(Level.INFO, "Изменена задача {0}", task.id());
                refresh(task.id());
            }
            return true;
        } catch (DateTimeParseException ex) {
            invalid("Введите дату в формате ГГГГ-ММ-ДД.");
        } catch (NumberFormatException ex) {
            invalid("Объём работы должен быть целым числом от 1 до 999.");
        } catch (IllegalArgumentException ex) {
            invalid(ex.getMessage());
        } catch (IOException ex) {
            saveFailed(ex);
        }
        return false;
    }

    private void toggle() {
        var task = view.selected();
        if (task != null) {
            change(task.id(), planner::toggle, "Изменён статус задачи {0}", task.id());
        }
    }

    private void remove() {
        var task = view.selected();
        if (task != null && view.confirmRemoval()) {
            change(task.id(), planner::remove, "Удалена задача {0}", null);
        }
    }

    private void change(UUID id, Change action, String message, UUID selection) {
        try {
            action.apply(id);
            log.log(Level.INFO, message, id);
            refresh(selection);
        } catch (IllegalArgumentException ex) {
            invalid(ex.getMessage());
        } catch (IOException ex) {
            saveFailed(ex);
        }
    }

    private void sort() {
        var task = view.selected();
        log.log(Level.FINE, "Выбрана сортировка: {0}", view.order());
        refresh(task == null ? null : task.id());
    }

    private void refresh(UUID selected) {
        view.showTasks(planner.list(view.order()), selected);
    }

    private void invalid(String message) {
        log.log(Level.WARNING, "Отклонён ввод: {0}", message);
        view.showError(message);
    }

    private void saveFailed(IOException ex) {
        log.log(Level.SEVERE, "Ошибка сохранения задач", ex);
        view.showError("Не удалось сохранить изменения. Проверьте доступ к файлу задач.");
    }

    @FunctionalInterface
    private interface Change {
        void apply(UUID id) throws IOException;
    }
}
