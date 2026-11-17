package by.text;

public record Word(String text) implements Lexeme {
    public Word { java.util.Objects.requireNonNull(text); }
}
