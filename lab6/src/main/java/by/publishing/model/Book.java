package by.publishing.model;

import java.util.ArrayList;
import java.util.List;

public final class Book extends Publication {
    private final List<String> entries = new ArrayList<>();
    private String author;

    public Book(String title, String author) {
        super(title);
        String checked = requireText(author);
        this.author = checked;
    }

    public void setAuthor(String value) {
        String checked = requireText(value);
        editorialChange();
        this.author = checked;
    }

    public String getAuthor() { return author; }
    public void addChapter(String text) {
        String checked = requireText(text);
        editorialChange();
        entries.add(checked);
    }

    public List<String> getChapters() { return List.copyOf(entries); }
}
