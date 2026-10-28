package by.notepad.factory;

import by.notepad.model.Note;
import by.notepad.model.ReminderNote;

import java.time.LocalDate;
import java.time.LocalTime;

public class DefaultNoteFactory extends NoteFactory {

    @Override
    protected Note createNote(
            String type,
            LocalDate date,
            String title,
            String text,
            LocalTime reminderTime,
            boolean active) {

        switch (type) {
            case "NOTE":
                return new Note(date, title, text);

            case "REMINDER":
                return new ReminderNote(
                        date,
                        title,
                        text,
                        reminderTime,
                        active);

            default:
                throw new IllegalArgumentException(
                        "Unknown note type: " + type);
        }
    }
}
