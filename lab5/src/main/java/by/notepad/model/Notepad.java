package by.notepad.model;

import by.notepad.storage.NoteFile;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class Notepad {

    private final NoteFile file;

    public Notepad() {
        this(Path.of("notes.txt"));
    }

    public Notepad(Path path) {
        file = new NoteFile(path);
    }

    public void addNote(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null");
        }

        file.update(notes -> notes.add(note));
    }

    public List<Note> getNotesByDate(LocalDate date) {
        Objects.requireNonNull(date, "Date cannot be null");
        DatePage page = readPages().get(date);

        if (page == null) {
            return Collections.emptyList();
        }

        return page.getNotes();
    }

    public List<LocalDate> getDates() {
        return new ArrayList<>(readPages().keySet());
    }

    public List<Note> getAllNotes() {
        return readPages().values().stream()
                .flatMap(page -> page.getNotes().stream())
                .toList();
    }

    public int size() {
        return file.load().size();
    }

    public void clear() {
        file.update(List::clear);
    }

    private Map<LocalDate, DatePage> readPages() {
        Map<LocalDate, DatePage> pages = new TreeMap<>();
        for (Note note : file.load()) {
            pages.computeIfAbsent(note.getDate(), DatePage::new).addNote(note);
        }
        return pages;
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
