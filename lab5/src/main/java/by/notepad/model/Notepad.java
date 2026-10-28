package by.notepad.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Notepad {

    private final Map<LocalDate, DatePage> pages = new TreeMap<>();

    public void addNote(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null");
        }

        DatePage page = pages.computeIfAbsent(
                note.getDate(), DatePage::new);

        page.addNote(note);
    }

    public List<Note> getNotesByDate(LocalDate date) {
        DatePage page = pages.get(date);

        if (page == null) {
            return Collections.emptyList();
        }

        return page.getNotes();
    }

    public List<LocalDate> getDates() {
        return new ArrayList<>(pages.keySet());
    }

    public int size() {
        return pages.values().stream()
                .mapToInt(page -> page.getNotes().size())
                .sum();
    }

    public void clear() {
        pages.clear();
    }

    public class DatePage {
        private final LocalDate date;
        private final List<Note> notes = new ArrayList<>();

        private DatePage(LocalDate date) {
            this.date = date;
        }

        private void addNote(Note note) {
            notes.add(note);
        }

        public LocalDate getDate() {
            return date;
        }

        public List<Note> getNotes() {
            return Collections.unmodifiableList(notes);
        }
    }
}
