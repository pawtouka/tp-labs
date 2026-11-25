package by.numbers;

public final class NumbersException extends Exception {
    private static final long serialVersionUID = 1L;

    public enum Kind {
        FILE_NOT_FOUND, READ_ERROR, INVALID_COUNT, MISSING_RECORD, EXTRA_RECORD,
        INVALID_RECORD, INVALID_LOCALE, INVALID_NUMBER, OUT_OF_RANGE
    }

    private final Kind kind;
    private final long line;

    NumbersException(Kind kind, long line, String message) {
        this(kind, line, message, null);
    }

    NumbersException(Kind kind, long line, String message, Throwable cause) {
        super(line > 0 ? "Строка " + line + ": " + message : message, cause);
        this.kind = kind;
        this.line = line;
    }

    public Kind kind() {
        return kind;
    }

    public long line() {
        return line;
    }
}
