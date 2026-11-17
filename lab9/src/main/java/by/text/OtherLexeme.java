package by.text;

public record OtherLexeme(String text) implements Lexeme {
    public OtherLexeme { java.util.Objects.requireNonNull(text); }
}
