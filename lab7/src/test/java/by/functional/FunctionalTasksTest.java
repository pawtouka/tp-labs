package by.functional;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.PatternSyntaxException;

/** Explicit checks, independent of the JVM assertion flag. */
public final class FunctionalTasksTest {
    private static int checks;

    private FunctionalTasksTest() { }

    public static void main(String[] args) {
        currying();
        sorting();
        pangrams();
        System.out.println("PASS: " + checks + " checks");
    }

    private static void currying() {
        var custom = CustomTasks.addition().apply(7);
        var standard = StandardTasks.addition().apply(7);
        for (int value : new int[]{-7, 0, 5, 100}) {
            equal(7 + value, custom.apply(value));
            equal(7 + value, standard.applyAsInt(value));
        }
        equal(Integer.MIN_VALUE, CustomTasks.addition().apply(Integer.MIN_VALUE).apply(0));
        equal(Integer.MIN_VALUE, StandardTasks.addition().apply(Integer.MIN_VALUE).applyAsInt(0));
        expect(ArithmeticException.class, () -> CustomTasks.addition().apply(Integer.MAX_VALUE).apply(1));
        expect(ArithmeticException.class, () -> StandardTasks.addition().apply(Integer.MAX_VALUE).applyAsInt(1));
        expect(ArithmeticException.class, () -> CustomTasks.addition().apply(Integer.MIN_VALUE).apply(-1));
        expect(ArithmeticException.class, () -> StandardTasks.addition().apply(Integer.MIN_VALUE).applyAsInt(-1));

        var customMatch = CustomTasks.matches().apply("[a-z]+");
        var standardMatch = StandardTasks.matches().apply("[a-z]+");
        for (String value : new String[]{"abc", "a", "", "123", "abc!", "ABC"}) {
            boolean expected = value.equals("abc") || value.equals("a");
            equal(expected, customMatch.apply(value));
            equal(expected, standardMatch.test(value));
        }
        equal(true, CustomTasks.matches().apply("").apply(""));
        equal(true, StandardTasks.matches().apply("").test(""));
        expect(PatternSyntaxException.class, () -> CustomTasks.matches().apply("["));
        expect(PatternSyntaxException.class, () -> StandardTasks.matches().apply("["));
        expect(PatternSyntaxException.class, () -> CustomTasks.split().apply("["));
        expect(PatternSyntaxException.class, () -> StandardTasks.split().apply("["));
        expect(NullPointerException.class, () -> CustomTasks.matches().apply(null));
        expect(NullPointerException.class, () -> StandardTasks.matches().apply(null));
        expect(NullPointerException.class, () -> customMatch.apply(null));
        expect(NullPointerException.class, () -> standardMatch.test(null));

        var customSplit = CustomTasks.split().apply("[,;]\\s*");
        var standardSplit = StandardTasks.split().apply("[,;]\\s*");
        String[][] inputs = {{"Java, Kotlin; Scala", "Java", "Kotlin", "Scala"},
                {"", ""}, {"a,,b,", "a", "", "b"}, {",a", "", "a"}, {",,,"}};
        for (String[] row : inputs) {
            String[] expected = Arrays.copyOfRange(row, 1, row.length);
            array(expected, customSplit.apply(row[0]));
            array(expected, standardSplit.apply(row[0]));
        }
        array(new String[]{"a", "b"}, CustomTasks.split().apply("").apply("ab"));
        array(new String[]{"a", "b"}, StandardTasks.split().apply("").apply("ab"));
        expect(NullPointerException.class, () -> customSplit.apply(null));
        expect(NullPointerException.class, () -> standardSplit.apply(null));
        expect(NullPointerException.class, () -> CustomTasks.split().apply(null));
        expect(NullPointerException.class, () -> StandardTasks.split().apply(null));
    }

    private static void sorting() {
        checkSort(new int[0]);
        checkSort(new int[]{1});
        checkSort(new int[]{3, -1, 3, 0, Integer.MIN_VALUE, Integer.MAX_VALUE});
        checkSort(new int[]{5, 4, 3, 2, 1});
        checkSort(new int[]{1, 2, 3, 4, 5});
        Random random = new Random(7);
        for (int i = 0; i < 100; i++) {
            int[] values = random.ints(i, -100, 101).toArray();
            checkSort(values);
        }
        expect(NullPointerException.class, () -> CustomTasks.descending().apply(null));
        expect(NullPointerException.class, () -> StandardTasks.descending().apply(null));
    }

    private static void checkSort(int[] values) {
        int[] original = values.clone();
        int[] expected = values.clone();
        Arrays.sort(expected);
        for (int left = 0, right = expected.length - 1; left < right; left++, right--) {
            int value = expected[left];
            expected[left] = expected[right];
            expected[right] = value;
        }
        array(expected, CustomTasks.descending().apply(values));
        array(original, values);
        array(expected, StandardTasks.descending().apply(values));
        array(original, values);
    }

    private static void pangrams() {
        verifyPangram(Alphabet.ENGLISH, "The quick brown fox jumps over the lazy dog.", true);
        verifyPangram(Alphabet.ENGLISH, "ABCDEFGHIJKLMNOPQRSTUVWXYZ", true);
        verifyPangram(Alphabet.ENGLISH, "The quick brown fox jumps over the lazy do.", false);
        verifyPangram(Alphabet.ENGLISH, "", false);
        verifyPangram(Alphabet.ENGLISH, "123, ! абвгдеёжзийклмнопрстуфхцчшщъыьэюя", false);
        String russian = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        verifyPangram(Alphabet.RUSSIAN, russian, true);
        verifyPangram(Alphabet.RUSSIAN, "Съешь же ещё этих мягких французских булок, да выпей чаю.", true);
        verifyPangram(Alphabet.RUSSIAN, russian.replace("Ё", "Е"), false);
        verifyPangram(new Alphabet("аАб"), "Б-а!", true);
        verifyPangram(new Alphabet("𐐀𐐁"), "𐐨𐐩", true);
        // Removing any required letter must invalidate the corresponding alphabet.
        for (String letters : new String[]{"abcdefghijklmnopqrstuvwxyz", "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"}) {
            Alphabet alphabet = new Alphabet(letters);
            for (int i = 0; i < letters.length(); i++) {
                verifyPangram(alphabet, letters.substring(0, i) + letters.substring(i + 1), false);
            }
        }
        expect(IllegalArgumentException.class, () -> new Alphabet(""));
        expect(IllegalArgumentException.class, () -> new Alphabet("abc1"));
        expect(NullPointerException.class, () -> new Alphabet(null));
        expect(NullPointerException.class, () -> CustomTasks.pangram(null));
        expect(NullPointerException.class, () -> StandardTasks.pangram(null));
        expect(NullPointerException.class, () -> CustomTasks.pangram(Alphabet.ENGLISH).apply(null));
        expect(NullPointerException.class, () -> StandardTasks.pangram(Alphabet.ENGLISH).test(null));
    }

    private static void verifyPangram(Alphabet alphabet, String text, boolean expected) {
        equal(expected, CustomTasks.pangram(alphabet).apply(text));
        equal(expected, StandardTasks.pangram(alphabet).test(text));
    }

    private static void array(int[] expected, int[] actual) {
        equal(Arrays.toString(expected), Arrays.toString(actual));
    }

    private static void array(String[] expected, String[] actual) {
        equal(Arrays.asList(expected), Arrays.asList(actual));
    }

    private static void equal(Object expected, Object actual) {
        checks++;
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError("Expected " + expected + ", got " + actual);
        }
    }

    private static void expect(Class<? extends Throwable> type, Runnable action) {
        checks++;
        try {
            action.run();
        } catch (Throwable error) {
            if (type.isInstance(error)) return;
            throw new AssertionError("Expected " + type.getSimpleName(), error);
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }
}
