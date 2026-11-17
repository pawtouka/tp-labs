package by.text;

public sealed interface Lexeme permits Word, Punctuation, OtherLexeme {
    String text();
    default java.util.List<Symbol> symbols() {
        return text().codePoints().mapToObj(Symbol::new).toList();
    }
}
