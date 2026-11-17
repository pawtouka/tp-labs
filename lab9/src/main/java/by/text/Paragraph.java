package by.text;

public record Paragraph(java.util.List<Sentence> sentences) {
    public Paragraph { sentences = java.util.List.copyOf(sentences); }
}
