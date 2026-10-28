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

    @BeforeEach
    void setUp() {
        notepad = new Notepad();

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

        List<Note> result = service.findByTitle("покуп");

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

//    @Test
//    void shouldSortNotesByTitle() {
//        LOGGER.info("Проверка сортировки заметок по заголовку");
//
//        LocalDate date = LocalDate.of(2026, 10, 28);
//
//        notepad.addNote(
//                new Note(date, "Учёба", "Подготовиться")
//        );
//
//        notepad.addNote(
//                new Note(date, "Английский", "Выучить слова")
//        );
//
//        notepad.addNote(
//                new Note(date, "Покупки", "Купить продукты")
//        );
//
//        List<Note> sorted = service.sortByTitle(
//                notepad.getNotesByDate(date)
//        );
//
//        assertEquals(3, sorted.size());
//        assertEquals("Английский", sorted.get(0).getTitle());
//        assertEquals("Покупки", sorted.get(1).getTitle());
//        assertEquals("Учёба", sorted.get(2).getTitle());
//
//        LOGGER.info("Заметки отсортированы по заголовку");
//    }

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

//    @Test
//    void shouldHandleEmptyListDuringSorting() {
//        LOGGER.info("Проверка сортировки пустого списка");
//
//        List<Note> sorted = service.sortByTitle(List.of());
//
//        assertNotNull(sorted);
//        assertTrue(sorted.isEmpty());
//
//        LOGGER.info("Пустой список обработан корректно");
//    }

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
    void shouldRejectNullSearchText() {
        LOGGER.info("Проверка поиска с null");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.findByTitle(null)
        );

        LOGGER.warn("Поиск с null отклонён");
    }
}