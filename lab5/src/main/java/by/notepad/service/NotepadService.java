package by.notepad.service;

import by.notepad.model.Note;
import by.notepad.model.Notepad;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class NotepadService {

    private static final Comparator<Note> BY_DATE_AND_TITLE = new Comparator<>() {
        @Override
        public int compare(Note first, Note second) {
            int byDate = first.getDate().compareTo(second.getDate());
            if (byDate != 0) {
                return byDate;
            }
            return String.CASE_INSENSITIVE_ORDER.compare(first.getTitle(), second.getTitle());
        }
    };

    private final Notepad notepad;

    public NotepadService(Notepad notepad) {
        if (notepad == null) {
            throw new IllegalArgumentException("Notepad cannot be null");
        }

        this.notepad = notepad;
    }

    public List<Note> findByTitle(String query) {
        if (query == null || query.isBlank()) {
            return new ArrayList<>();
        }

        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        return notepad.getAllNotes().stream()
                .filter(note -> note.getTitle()
                        .toLowerCase(Locale.ROOT)
                        .contains(normalizedQuery))
                .collect(Collectors.toList());
    }

    public List<Note> getAllSorted() {
        List<Note> notes = new ArrayList<>(notepad.getAllNotes());
        notes.sort(BY_DATE_AND_TITLE);

        return notes;
    }

    public List<Note> getNotesByDate(LocalDate date) {
        return notepad.getNotesByDate(date);
    }
}
