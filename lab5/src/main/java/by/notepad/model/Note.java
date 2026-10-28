package by.notepad.model;

import java.time.LocalDate;
import java.util.Objects;

public class Note {
    private final LocalDate date;
    private final String title;
    private final String text;

    public Note(LocalDate date, String title, String text) {
        this.date = Objects.requireNonNull(date, "Date cannot be null");

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be empty");
        }

        this.title = title;
        this.text = text;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return date + " | " + title + " | " + text;
    }
}
