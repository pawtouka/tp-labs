package by.planner;

import by.planner.model.PlannerService;
import by.planner.model.Priority;
import by.planner.model.SortOrder;
import by.planner.model.StudyTask;
import by.planner.model.TaskRepository;
import by.planner.model.TaskType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

final class PlannerServiceTest {
    private static final LocalDate DATE = LocalDate.of(2026, 12, 1);
    private final TaskRepository repository = mock(TaskRepository.class);
    private PlannerService planner;

    @BeforeEach
    void setUp() throws IOException {
        when(repository.load()).thenReturn(List.of());
        planner = new PlannerService(repository);
    }

    @Test
    void addsValidatedTaskAndPersistsIt() throws IOException {
        UUID id = planner.add(TaskType.READING, details("  Алгебра  ", 8));
        var task = planner.list(SortOrder.DEADLINE).get(0);
        assertEquals(id, task.id());
        assertEquals("Алгебра", task.details().title());
        assertFalse(task.completed());
        verify(repository).save(List.of(task));
    }

    @Test
    void calculatesTimePolymorphically() throws IOException {
        planner.add(TaskType.READING, details("Теория", 5));
        planner.add(TaskType.PRACTICE, details("Практика", 5));
        var tasks = planner.list(SortOrder.DEADLINE);
        assertEquals(15, tasks.get(0).estimatedMinutes());
        assertEquals(60, tasks.get(1).estimatedMinutes());
    }

    @Test
    void editingCanChangeTypeAndPreservesIdAndDoneState() throws IOException {
        UUID id = planner.add(TaskType.READING, details("Теория", 2));
        planner.toggle(id);
        planner.update(id, TaskType.PRACTICE, details("Примеры", 3));
        var task = planner.list(SortOrder.DEADLINE).get(0);
        assertEquals(id, task.id());
        assertEquals(TaskType.PRACTICE, task.type());
        assertTrue(task.completed());
        assertEquals(36, task.estimatedMinutes());
    }

    @Test
    void closesAndReopensTask() throws IOException {
        UUID id = planner.add(TaskType.READING, details("Теория", 7));
        planner.toggle(id);
        assertEquals(new PlannerService.Summary(1, 1, 0), planner.summary());
        planner.toggle(id);
        assertEquals(new PlannerService.Summary(1, 0, 21), planner.summary());
    }

    @Test
    void removesAndSavesEmptyList() throws IOException {
        UUID id = planner.add(TaskType.READING, details("Теория", 2));
        planner.remove(id);
        assertTrue(planner.list(SortOrder.DEADLINE).isEmpty());
        verify(repository).save(List.of());
    }

    @Test
    void sortsByDeadlineOrPriorityWithDeadlineTieBreak() throws IOException {
        UUID low = planner.add(TaskType.READING, new StudyTask.Details("Низкий", DATE, Priority.LOW, 1));
        UUID highLate = planner.add(TaskType.READING,
                new StudyTask.Details("Поздно", DATE.plusDays(2), Priority.HIGH, 1));
        UUID highSoon = planner.add(TaskType.READING,
                new StudyTask.Details("Скоро", DATE.plusDays(1), Priority.HIGH, 1));
        assertEquals(List.of(low, highSoon, highLate), ids(SortOrder.DEADLINE));
        assertEquals(List.of(highSoon, highLate, low), ids(SortOrder.PRIORITY));
    }

    @Test
    void summaryCountsOnlyUnfinishedWorkInRemainingTime() throws IOException {
        UUID done = planner.add(TaskType.PRACTICE, details("Готово", 4));
        planner.add(TaskType.PRACTICE, details("Упражнения", 3));
        planner.add(TaskType.READING, details("Страницы", 5));
        planner.toggle(done);
        assertEquals(new PlannerService.Summary(3, 1, 51), planner.summary());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "две\nстроки", "таб\tуляция"})
    void rejectsMalformedTitles(String title) {
        assertThrows(IllegalArgumentException.class, () -> details(title, 1));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 1000})
    void rejectsUnitsOutsideRange(int units) {
        assertThrows(IllegalArgumentException.class, () -> details("Упражнения", units));
    }

    @Test
    void rejectsOverlongTitle() {
        assertThrows(IllegalArgumentException.class, () -> details("а".repeat(121), 1));
    }

    @Test
    void rejectsUnknownIdsWithoutSaving() throws IOException {
        var missing = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> planner.toggle(missing));
        assertThrows(IllegalArgumentException.class, () -> planner.remove(missing));
        assertThrows(IllegalArgumentException.class, () -> planner.update(missing, TaskType.READING, details("Чтение", 1)));
        verify(repository, never()).save(anyList());
    }

    @Test
    void leavesStateUnchangedWhenSavingFails() throws IOException {
        UUID id = planner.add(TaskType.READING, details("Исходная задача", 1));
        doThrow(new IOException("Хранилище недоступно")).when(repository).save(anyList());
        assertThrows(IOException.class, () -> planner.add(TaskType.READING, details("Новая", 2)));
        assertThrows(IOException.class, () -> planner.update(id, TaskType.PRACTICE, details("Изменённая", 3)));
        assertThrows(IOException.class, () -> planner.toggle(id));
        assertThrows(IOException.class, () -> planner.remove(id));
        assertEquals(List.of(id), ids(SortOrder.DEADLINE));
        assertEquals("Исходная задача", planner.list(SortOrder.DEADLINE).get(0).details().title());
        assertEquals(new PlannerService.Summary(1, 0, 3), planner.summary());
    }

    @Test
    void rejectsDuplicateIdsOnLoad() throws IOException {
        var task = StudyTask.of(UUID.randomUUID(), TaskType.READING, details("Книга", 2), false);
        when(repository.load()).thenReturn(List.of(task, task));
        assertThrows(IOException.class, () -> new PlannerService(repository));
        verify(repository, never()).save(anyList());
    }

    @Test
    void propagatesStoreReadError() throws IOException {
        when(repository.load()).thenThrow(new IOException("Ошибка чтения"));
        assertThrows(IOException.class, () -> new PlannerService(repository));
    }

    private List<UUID> ids(SortOrder order) {
        return planner.list(order).stream().map(StudyTask::id).toList();
    }

    private static StudyTask.Details details(String title, int units) {
        return new StudyTask.Details(title, DATE, Priority.NORMAL, units);
    }
}
