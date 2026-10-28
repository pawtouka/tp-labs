package by.notepad.factory;

import by.notepad.logging.TestLoggingExtension;
import by.notepad.model.Note;
import by.notepad.model.ReminderNote;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TestLoggingExtension.class)
class DefaultNoteFactoryTest {

    private static final Logger LOGGER =
            LogManager.getLogger(DefaultNoteFactoryTest.class);

    private DefaultNoteFactory factory;

    @BeforeEach
    void setUp() {
        factory = new DefaultNoteFactory();

        LOGGER.info("Создана фабрика заметок");
    }

    @Test
    void shouldCreateRegularNote() {
        LOGGER.info("Проверка создания обычной заметки");

        LocalDate date = LocalDate.of(2026, 10, 28);

        Note note = factory.create(
                "NOTE",
                date,
                "Покупки",
                "Купить продукты",
                null,
                true
        );

        assertNotNull(note);
        assertEquals(Note.class, note.getClass());
        assertEquals(date, note.getDate());
        assertEquals("Покупки", note.getTitle());
        assertEquals("Купить продукты", note.getText());

        LOGGER.info("Обычная заметка создана успешно");
    }

    @Test
    void shouldCreateReminderNote() {
        LOGGER.info("Проверка создания напоминания");

        LocalDate date = LocalDate.of(2026, 10, 28);
        LocalTime reminderTime = LocalTime.of(9, 0);

        Note note = factory.create(
                "REMINDER",
                date,
                "Встреча",
                "Подготовиться к встрече",
                reminderTime,
                true
        );

        assertNotNull(note);
        assertInstanceOf(ReminderNote.class, note);

        ReminderNote reminderNote = (ReminderNote) note;

        assertEquals(date, reminderNote.getDate());
        assertEquals("Встреча", reminderNote.getTitle());
        assertEquals(reminderTime, reminderNote.getReminderTime());
        assertTrue(reminderNote.isActive());

        LOGGER.info("Напоминание создано успешно");
    }

    @Test
    void shouldRejectUnknownNoteType() {
        LOGGER.info("Проверка неизвестного типа заметки");

        LocalDate date = LocalDate.of(2026, 10, 28);

        assertThrows(
                IllegalArgumentException.class,
                () -> factory.create(
                        "UNKNOWN",
                        date,
                        "Заголовок",
                        "Текст",
                        null,
                        true
                )
        );

        LOGGER.warn("Неизвестный тип заметки отклонён");
    }

    @Test
    void shouldRejectNullNoteType() {
        LOGGER.info("Проверка null в качестве типа заметки");

        LocalDate date = LocalDate.of(2026, 10, 28);

        assertThrows(
                IllegalArgumentException.class,
                () -> factory.create(
                        null,
                        date,
                        "Заголовок",
                        "Текст",
                        null,
                        true
                )
        );

        LOGGER.warn("Пустой тип заметки отклонён");
    }

    @Test
    void shouldRejectNullDate() {
        LOGGER.info("Проверка создания заметки без даты");

        assertThrows(
                IllegalArgumentException.class,
                () -> factory.create(
                        "NOTE",
                        null,
                        "Заголовок",
                        "Текст",
                        null,
                        true
                )
        );

        LOGGER.warn("Создание заметки без даты отклонено");
    }
}