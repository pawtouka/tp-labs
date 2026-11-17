package by.functional;

import java.util.Objects;
import java.util.regex.Pattern;

/** Assignment solutions using our own functional interfaces. */
public final class CustomTasks {
    private CustomTasks() { }

    public static TaskFunction<Integer, TaskFunction<Integer, Integer>> addition() {
        return left -> right -> Math.addExact(left, right);
    }

    public static TaskFunction<String, TaskFunction<String, Boolean>> matches() {
        return regex -> {
            Pattern pattern = Pattern.compile(regex);
            return text -> pattern.matcher(text).matches();
        };
    }

    public static TaskFunction<String, TaskFunction<String, String[]>> split() {
        return regex -> {
            Pattern pattern = Pattern.compile(regex);
            return text -> pattern.split(text);
        };
    }

    public static TaskFunction<int[], int[]> descending() {
        return values -> {
            int[] result = Objects.requireNonNull(values, "values").clone();
            IntOrder order = (left, right) -> Integer.compare(right, left);
            // Insertion sort permits direct use of our own comparison interface.
            for (int i = 1; i < result.length; i++) {
                int value = result[i];
                int j = i - 1;
                while (j >= 0 && order.compare(result[j], value) > 0) {
                    result[j + 1] = result[j];
                    j--;
                }
                result[j + 1] = value;
            }
            return result;
        };
    }

    public static TaskFunction<String, Boolean> pangram(Alphabet alphabet) {
        Objects.requireNonNull(alphabet, "alphabet");
        return text -> alphabet.containsAllIn(text);
    }
}
