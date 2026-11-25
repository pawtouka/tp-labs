package by.notepad.model;

import by.notepad.logging.TestLoggingExtension;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TestLoggingExtension.class)
class NotepadTest {

    private static final Logger LOGGER =
            LogManager.getLogger(NotepadTest.class);

    private Notepad notepad;

    @TempDir
    Path directory;

    @BeforeEach
    void setUp() {
        notepad = new Notepad(directory.resolve("notes.txt"));

        LOGGER.info("Создан новый экземпляр Notepad");
    }

    @Test
    void shouldAddNote() {
        LOGGER.info("Проверка добавления заметки");

        LocalDate date = LocalDate.of(2026, 10, 28);
        Note note = new Note(date, "Покупки", "Купить продукты");

        notepad.addNote(note);

        List<Note> notes = notepad.getNotesByDate(date);

        assertEquals(1, notes.size());
        assertEquals("Покупки", notes.get(0).getTitle());

        LOGGER.info("Заметка успешно добавлена");
    }

    @Test
    void shouldAddSeveralNotesForSameDate() {
        LOGGER.info("Проверка добавления нескольких заметок на одну дату");

        LocalDate date = LocalDate.of(2026, 10, 28);

        Note firstNote = new Note(
                date,
                "Покупки",
                "Купить продукты"
        );

        Note secondNote = new Note(
                date,
                "Учёба",
                "Подготовиться к занятиям"
        );

        notepad.addNote(firstNote);
        notepad.addNote(secondNote);

        List<Note> notes = notepad.getNotesByDate(date);

        assertEquals(2, notes.size());

        LOGGER.info("Обе заметки сохранены");
    }

    @Test
    void shouldReturnEmptyListForDateWithoutNotes() {
        LOGGER.info("Проверка даты без заметок");

        LocalDate date = LocalDate.of(2026, 12, 31);

        List<Note> notes = notepad.getNotesByDate(date);

        assertNotNull(notes);
        assertTrue(notes.isEmpty());

        LOGGER.info("Для пустой даты возвращён пустой список");
    }

    @Test
    void shouldRejectNullNote() {
        LOGGER.info("Проверка обработки null вместо заметки");

        assertThrows(
                IllegalArgumentException.class,
                () -> notepad.addNote(null)
        );

        LOGGER.warn("Добавление null отклонено, как и ожидалось");
    }

    @Test
    void shouldRejectNoteWithNullDate() {
        LOGGER.info("Проверка заметки без даты");

        assertThrows(
                NullPointerException.class,
                () -> new Note(null, "Заголовок", "Текст заметки")
        );

        LOGGER.warn("Заметка без даты отклонена");
    }

    @Test
    void shouldClearNotepad() {
        LOGGER.info("Проверка очистки блокнота");

        LocalDate date = LocalDate.of(2026, 10, 28);

        notepad.addNote(
                new Note(date, "Заголовок", "Текст")
        );

        notepad.clear();

        assertTrue(notepad.getNotesByDate(date).isEmpty());

        LOGGER.info("Блокнот успешно очищен");
    }

    @Test
    void shouldNotAllowModificationOfReturnedNotes() {
        LOGGER.info("Проверка защиты списка заметок от изменений");

        LocalDate date = LocalDate.of(2026, 10, 28);

        notepad.addNote(
                new Note(date, "Заголовок", "Текст")
        );

        List<Note> notes = notepad.getNotesByDate(date);

        assertThrows(
                UnsupportedOperationException.class,
                () -> notes.add(
                        new Note(date, "Другая заметка", "Текст")
                )
        );

        LOGGER.warn("Изменение возвращённого списка запрещено");
    }
}
