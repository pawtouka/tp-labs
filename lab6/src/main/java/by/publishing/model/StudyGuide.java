package by.publishing.model;

import java.util.ArrayList;
import java.util.List;

public final class StudyGuide extends Publication {
    private final List<String> entries = new ArrayList<>();
    private String subject;

    public StudyGuide(String title, String subject) {
        super(title);
        String checked = requireText(subject);
        this.subject = checked;
    }

    public void setSubject(String value) {
        String checked = requireText(value);
        editorialChange();
        this.subject = checked;
    }

    public String getSubject() { return subject; }
    public void addExercise(String text) {
        String checked = requireText(text);
        editorialChange();
        entries.add(checked);
    }

    public List<String> getExercises() { return List.copyOf(entries); }
}
