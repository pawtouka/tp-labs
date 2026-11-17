package by.text;

public record Symbol(int codePoint) {
    public Symbol {
        if (!Character.isValidCodePoint(codePoint)) throw new IllegalArgumentException("Invalid code point");
    }
    public String text() { return new String(Character.toChars(codePoint)); }
}
