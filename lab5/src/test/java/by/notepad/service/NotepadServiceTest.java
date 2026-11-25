package by.notepad.service;

import by.notepad.logging.TestLoggingExtension;
import by.notepad.model.Note;
import by.notepad.model.Notepad;
import by.notepad.model.ReminderNote;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TestLoggingExtension.class)
class NotepadServiceTest {

    private static final Logger LOGGER =
            LogManager.getLogger(NotepadServiceTest.class);

    private Notepad notepad;
    private NotepadService service;

    @TempDir
    Path directory;

    @BeforeEach
    void setUp() {
        notepad = new Notepad(directory.resolve("notes.txt"));

        service = new NotepadService(notepad);

        LOGGER.info("Созданы блокнот и сервис");
    }

    @Test
    void shouldFindNotesByTitle() {
        LOGGER.info("Проверка поиска заметок по заголовку");

        LocalDate date = LocalDate.of(2026, 10, 28);

        notepad.addNote(
                new Note(date, "Покупки", "Купить продукты")
        );

        notepad.addNote(
                new Note(date, "Учёба", "Подготовить конспект")
        );

        notepad.addNote(
                new Note(date, "План покупок", "Купить тетради")
        );

        List<Note> result = service.findByTitle("ПОКУП");

        assertEquals(2, result.size());

        assertTrue(
                result.stream()
                        .allMatch(note ->
                                note.getTitle()
                                        .toLowerCase()
                                        .contains("покуп"))
        );

        LOGGER.info("Поиск по заголовку вернул ожидаемые результаты");
    }

    @Test
    void shouldReturnEmptyListWhenNothingFound() {
        LOGGER.info("Проверка поиска отсутствующей заметки");

        LocalDate date = LocalDate.of(2026, 10, 28);

        notepad.addNote(
                new Note(date, "Покупки", "Купить продукты")
        );

        List<Note> result = service.findByTitle("несуществующее");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        LOGGER.info("Поиск несуществующей заметки вернул пустой список");
    }

    @Test
    void shouldSortNotesWithSameDateByTitleIgnoringCase() {
        LocalDate date = LocalDate.of(2026, 10, 28);
        notepad.addNote(new Note(date, "Учёба", "Подготовиться"));
        notepad.addNote(new Note(date, "английский", "Выучить слова"));
        notepad.addNote(new Note(date, "Покупки", "Купить продукты"));

        assertEquals(List.of("английский", "Покупки", "Учёба"),
                service.getAllSorted().stream().map(Note::getTitle).toList());
    }

    @Test
    void shouldSortNotesByDate() {
        LOGGER.info("Проверка сортировки заметок по дате");

        LocalDate laterDate = LocalDate.of(2026, 10, 30);
        LocalDate earlierDate = LocalDate.of(2026, 10, 28);

        notepad.addNote(
                new Note(laterDate, "Позже", "Текст")
        );

        notepad.addNote(
                new Note(earlierDate, "Раньше", "Текст")
        );

        List<Note> sorted = service.getAllSorted();

        assertEquals(2, sorted.size());
        assertEquals(earlierDate, sorted.get(0).getDate());
        assertEquals(laterDate, sorted.get(1).getDate());

        LOGGER.info("Сортировка по дате проверена");
    }

    @Test
    void shouldReturnEmptySortedListForEmptyNotepad() {
        assertTrue(service.getAllSorted().isEmpty());
    }

    @Test
    void shouldFindReminderAmongNotes() {
        LOGGER.info("Проверка работы с напоминаниями");

        LocalDate date = LocalDate.of(2026, 10, 28);

        ReminderNote reminder = new ReminderNote(
                date,
                "Встреча",
                "Подготовиться",
                LocalTime.of(9, 0),
                true
        );

        notepad.addNote(reminder);

        List<Note> result = service.findByTitle("Встреча");

        assertEquals(1, result.size());
        assertInstanceOf(ReminderNote.class, result.get(0));

        LOGGER.info("Напоминание найдено среди заметок");
    }

    @Test
    void shouldReturnEmptyListForNullOrBlankSearchText() {
        notepad.addNote(new Note(LocalDate.of(2026, 10, 28), "Покупки", "Текст"));

        assertTrue(service.findByTitle(null).isEmpty());
        assertTrue(service.findByTitle(" \t").isEmpty());
    }
}
