package by.notepad.factory;

import by.notepad.model.Note;

import java.time.LocalDate;
import java.time.LocalTime;

public abstract class NoteFactory {

    public final Note create(
            String type,
            LocalDate date,
            String title,
            String text,
            LocalTime reminderTime,
            boolean active) {

        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Type cannot be empty");
        }

        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        return createNote(
                type.trim().toUpperCase(),
                date,
                title,
                text,
                reminderTime,
                active);
    }

    protected abstract Note createNote(
            String type,
            LocalDate date,
            String title,
            String text,
            LocalTime reminderTime,
            boolean active);
}
