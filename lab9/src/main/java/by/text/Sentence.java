package by.text;

public record Sentence(java.util.List<Lexeme> lexemes) {
    public Sentence { lexemes = java.util.List.copyOf(lexemes); }
    public String text() {
        return lexemes.stream().map(Lexeme::text).collect(java.util.stream.Collectors.joining()).strip();
    }
    public java.util.List<Word> words() {
        return lexemes.stream().filter(Word.class::isInstance).map(Word.class::cast).toList();
    }
}
