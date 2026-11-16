package by.publishing.model;

import java.util.ArrayList;
import java.util.List;

public final class Magazine extends Publication {
    private final List<String> entries = new ArrayList<>();
    private int issueNumber;

    public Magazine(String title, int issueNumber) {
        super(title);
        if (issueNumber <= 0) throw new IllegalArgumentException("Issue number must be positive");
        int checked = issueNumber;
        this.issueNumber = checked;
    }

    public void setIssueNumber(int value) {
        if (value <= 0) throw new IllegalArgumentException("Issue number must be positive");
        int checked = value;
        editorialChange();
        this.issueNumber = checked;
    }

    public int getIssueNumber() { return issueNumber; }
    public void addArticle(String text) {
        String checked = requireText(text);
        editorialChange();
        entries.add(checked);
    }

    public List<String> getArticles() { return List.copyOf(entries); }
}
