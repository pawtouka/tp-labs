package by.text;

public record Text(java.util.List<Paragraph> paragraphs) {
    public Text { paragraphs = java.util.List.copyOf(paragraphs); }
    public java.util.List<Sentence> sentences() {
        return paragraphs.stream().flatMap(p -> p.sentences().stream()).toList();
    }
}
