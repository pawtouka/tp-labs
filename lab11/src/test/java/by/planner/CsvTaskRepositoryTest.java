package by.planner;

import by.planner.model.PlannerService;
import by.planner.model.Priority;
import by.planner.model.SortOrder;
import by.planner.model.StudyTask;
import by.planner.model.TaskType;
import by.planner.storage.CsvTaskRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class CsvTaskRepositoryTest {
    @TempDir
    Path directory;

    @Test
    void roundTripsCsvAcrossReopensWithUnicodeCommasAndQuotes() throws IOException {
        Path path = directory.resolve("tasks.csv");
        var details = new StudyTask.Details("Читать \"Java\", глава 1", LocalDate.of(2026, 12, 1), Priority.HIGH, 5);
        UUID id;
        try (var repository = new CsvTaskRepository(path)) {
            var planner = new PlannerService(repository);
            id = planner.add(TaskType.READING, details);
            planner.toggle(id);
        }
        assertTrue(Files.readString(path).contains("\"Читать \"\"Java\"\", глава 1\""));

        try (var repository = new CsvTaskRepository(path)) {
            var planner = new PlannerService(repository);
            var task = planner.list(SortOrder.DEADLINE).get(0);
            assertEquals(id, task.id());
            assertEquals(details, task.details());
            assertEquals(TaskType.READING, task.type());
            assertTrue(task.completed());
        }
    }

    @Test
    void returnsEmptyForMissingData() throws IOException {
        Path path = directory.resolve("absent.csv");
        try (var repository = new CsvTaskRepository(path)) {
            assertEquals(List.of(), repository.load());
        }
        assertFalse(Files.exists(path));
    }

    @Test
    void refusesSecondWriterUntilFirstCloses() throws IOException {
        Path path = directory.resolve("tasks.csv");
        try (var repository = new CsvTaskRepository(path)) {
            assertThrows(IOException.class, () -> {
                try (var second = new CsvTaskRepository(path)) {
                    second.load();
                }
            });
        }
        try (var reopened = new CsvTaskRepository(path)) {
            assertEquals(List.of(), reopened.load());
        }
    }

    @Test
    void rejectsMalformedCsvWithoutOverwritingIt() throws IOException {
        Path path = directory.resolve("broken.csv");
        String contents = "id,type,dueDate,priority,units,completed,title\n"
                + "52d92879-af36-4518-8d42-71ca069922cf,READING,2026-12-01,HIGH,4,maybe,\"Текст\"\n";
        Files.writeString(path, contents);
        try (var repository = new CsvTaskRepository(path)) {
            assertTrue(assertThrows(IOException.class, repository::load).getMessage().contains("строке 2"));
        }
        assertEquals(contents, Files.readString(path));
    }

    @Test
    void failedAtomicReplaceCleansUpTemporaryFile() throws IOException {
        Path path = directory.resolve("tasks.csv");
        try (var repository = new CsvTaskRepository(path)) {
            Files.createDirectory(path);
            Files.writeString(path.resolve("marker"), "сохранить");
            assertThrows(IOException.class, () -> repository.save(List.of()));
            assertEquals("сохранить", Files.readString(path.resolve("marker")));
            try (var entries = Files.list(directory)) {
                assertTrue(entries.noneMatch(entry -> entry.getFileName().toString().startsWith(".planner-")));
            }
        }
    }
}
