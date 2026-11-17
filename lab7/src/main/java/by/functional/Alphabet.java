package by.functional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/** Explicit alphabet; case is ignored using Unicode simple case conversion. */
public final class Alphabet {
    public static final Alphabet ENGLISH = new Alphabet("abcdefghijklmnopqrstuvwxyz");
    public static final Alphabet RUSSIAN = new Alphabet("абвгдеёжзийклмнопрстуфхцчшщъыьэюя");
    private final Set<Integer> letters;

    public Alphabet(String letters) {
        Objects.requireNonNull(letters, "letters");
        if (letters.isEmpty() || !letters.codePoints().allMatch(Character::isLetter)) {
            throw new IllegalArgumentException("Alphabet must contain letters only and be nonempty");
        }
        this.letters = normalized(letters);
    }

    public boolean containsAllIn(String text) {
        return normalized(Objects.requireNonNull(text, "text")).containsAll(letters);
    }

    private static Set<Integer> normalized(String text) {
        return text.codePoints().map(Character::toLowerCase).boxed().collect(Collectors.toSet());
    }
}
