package by.notepad.model;

import by.notepad.logging.TestLoggingExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TestLoggingExtension.class)
class PersistentNotepadTest {
    private static final LocalDate DATE = LocalDate.of(2026, 10, 28);

    @TempDir
    Path directory;

    @Test
    void shouldRestoreBothNoteTypesOnSameDate() {
        Path file = directory.resolve("notes.txt");
        Notepad first = new Notepad(file);
        String title = "План\tпокупок";
        String text = "Купить тетради\nИ бумагу 📒";
        LocalTime time = LocalTime.of(9, 15, 2, 123);
        first.addNote(new Note(DATE, title, text));
        first.addNote(new ReminderNote(DATE, "Встреча", "Подготовиться", time, false));
        first.addNote(new Note(DATE.plusDays(1), "Учёба", "Подготовить конспект"));

        Notepad reopened = new Notepad(file);
        assertEquals(3, reopened.size());
        assertEquals(List.of(DATE, DATE.plusDays(1)), reopened.getDates());
        List<Note> notes = reopened.getNotesByDate(DATE);
        assertEquals(2, notes.size());
        assertEquals(Note.class, notes.get(0).getClass());
        assertEquals(title, notes.get(0).getTitle());
        assertEquals(text, notes.get(0).getText());
        ReminderNote reminder = assertInstanceOf(ReminderNote.class, notes.get(1));
        assertEquals(time, reminder.getReminderTime());
        assertFalse(reminder.isActive());
    }

    @Test
    void shouldPersistClearAndAcceptNewNotesAfterward() {
        Path file = directory.resolve("notes.txt");
        Notepad notepad = new Notepad(file);
        notepad.addNote(new Note(DATE, "Покупки", "Текст"));
        notepad.clear();

        Notepad reopened = new Notepad(file);
        assertEquals(0, reopened.size());
        assertTrue(reopened.getDates().isEmpty());
        reopened.addNote(new Note(DATE, "Учёба", "Текст"));
        assertEquals("Учёба", notepad.getNotesByDate(DATE).get(0).getTitle());
    }

    @Test
    void shouldAppendToAnEmptyExistingFile() throws IOException {
        Path file = Files.createFile(directory.resolve("notes.txt"));
        Notepad notepad = new Notepad(file);
        assertEquals(0, notepad.size());
        notepad.addNote(new Note(DATE, "Встреча", "Текст"));

        assertEquals(1, new Notepad(file).size());
    }

    @Test
    void shouldReportCorruptRecordAndPreserveExistingFile() throws IOException {
        Path file = directory.resolve("notes.txt");
        String original = "notepad-v1\nNOTE\tinvalid\tdGVzdA==\tdGVzdA==\n";
        Files.writeString(file, original);
        Notepad notepad = new Notepad(file);

        UncheckedIOException failure = assertThrows(UncheckedIOException.class, notepad::size);
        assertTrue(failure.getCause().getMessage().contains("строке 2"));
        assertThrows(UncheckedIOException.class,
                () -> notepad.addNote(new Note(DATE, "Покупки", "Текст")));
        assertEquals(original, Files.readString(file));
    }

    @Test
    void shouldKeepUpdatesFromConcurrentInstances() throws Exception {
        Path file = directory.resolve("notes.txt");
        var workers = Executors.newFixedThreadPool(2);
        try {
            var first = workers.submit(() -> new Notepad(file).addNote(new Note(DATE, "Первая", "Текст")));
            var second = workers.submit(() -> new Notepad(file).addNote(new Note(DATE, "Вторая", "Текст")));
            first.get(5, TimeUnit.SECONDS);
            second.get(5, TimeUnit.SECONDS);
        } finally {
            workers.shutdownNow();
        }

        assertEquals(List.of("Вторая", "Первая"), new Notepad(file).getNotesByDate(DATE).stream()
                .map(Note::getTitle).sorted().toList());
    }
}
