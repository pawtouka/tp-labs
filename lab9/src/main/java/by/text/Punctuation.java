package by.text;

public record Punctuation(String text) implements Lexeme {
    public Punctuation { java.util.Objects.requireNonNull(text); }
}
