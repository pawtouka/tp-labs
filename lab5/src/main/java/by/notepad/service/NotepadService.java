package by.notepad.service;

import by.notepad.model.Note;
import by.notepad.model.Notepad;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NotepadService {

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

        return notepad.getDates().stream()
                .flatMap(date -> notepad.getNotesByDate(date).stream())
                .filter(note -> note.getTitle()
                        .toLowerCase()
                        .contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Note> getAllSorted() {
        List<Note> notes = notepad.getDates().stream()
                .flatMap(date -> notepad.getNotesByDate(date).stream())
                .collect(Collectors.toList());

        notes.sort(Comparator
                .comparing(Note::getDate)
                .thenComparing(Note::getTitle,
                        String.CASE_INSENSITIVE_ORDER));

        return notes;
    }

    public List<Note> getNotesByDate(LocalDate date) {
        return notepad.getNotesByDate(date);
    }
}
