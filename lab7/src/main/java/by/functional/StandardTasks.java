package by.functional;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/** Assignment solutions using JDK functional interfaces. */
public final class StandardTasks {
    private StandardTasks() { }

    public static Function<Integer, IntUnaryOperator> addition() {
        return left -> right -> Math.addExact(left, right);
    }

    public static Function<String, Predicate<String>> matches() {
        return regex -> {
            Pattern pattern = Pattern.compile(regex);
            return text -> pattern.matcher(text).matches();
        };
    }

    public static Function<String, Function<String, String[]>> split() {
        return regex -> {
            Pattern pattern = Pattern.compile(regex);
            return text -> pattern.split(text);
        };
    }

    public static Function<int[], int[]> descending() {
        return values -> {
            Comparator<Integer> order = (left, right) -> Integer.compare(right, left);
            Integer[] result = Arrays.stream(Objects.requireNonNull(values, "values")).boxed()
                    .toArray(Integer[]::new);
            Arrays.sort(result, order);
            return Arrays.stream(result).mapToInt(Integer::intValue).toArray();
        };
    }

    public static Predicate<String> pangram(Alphabet alphabet) {
        Objects.requireNonNull(alphabet, "alphabet");
        return text -> alphabet.containsAllIn(text);
    }
}
