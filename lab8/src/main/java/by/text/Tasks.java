package by.text;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Tasks {
    private static final Pattern WORD = Pattern.compile("\\p{L}+");
    private static final Pattern PASSWORD = Pattern.compile("(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])[A-Za-z0-9_]{10,}");
    private Tasks() { }

    private static String replace(String word, int k, int symbol) {
        int length = word.codePointCount(0, word.length());
        if (k > length) return word;
        int start = word.offsetByCodePoints(0, k - 1);
        int end = word.offsetByCodePoints(start, 1);
        return word.substring(0, start) + new String(Character.toChars(symbol)) + word.substring(end);
    }

    public static String replaceKth(String text, int k, int symbol, boolean stream) {
        Objects.requireNonNull(text);
        if (k <= 0 || !Character.isValidCodePoint(symbol) || symbol >= 0xD800 && symbol <= 0xDFFF)
            throw new IllegalArgumentException("Invalid position or Unicode symbol");
        if (stream) {
            // Preserve every separator as a separate token.
            return Pattern.compile("\\p{L}+|\\P{L}+").matcher(text).results()
                .map(m -> WORD.matcher(m.group()).matches() ? replace(m.group(), k, symbol) : m.group())
                .collect(Collectors.joining());
        }
        var matcher = WORD.matcher(text);
        var result = new StringBuilder();
        int previous = 0;
        while (matcher.find()) {
            result.append(text, previous, matcher.start()).append(replace(matcher.group(), k, symbol));
            previous = matcher.end();
        }
        return result.append(text.substring(previous)).toString();
    }

    private static boolean matchingEnds(String word) {
        return word.codePointAt(0) == word.codePointBefore(word.length());
    }

    public static List<String> matchingWords(String text, boolean stream) {
        Objects.requireNonNull(text);
        if (stream) return WORD.matcher(text).results().map(m -> m.group())
            .filter(Tasks::matchingEnds).distinct().toList();
        var words = new LinkedHashSet<String>();
        var matcher = WORD.matcher(text);
        while (matcher.find()) if (matchingEnds(matcher.group())) words.add(matcher.group());
        return List.copyOf(words);
    }

    public static boolean strongPassword(String text, boolean stream) {
        Objects.requireNonNull(text);
        if (!stream) return PASSWORD.matcher(text).matches();
        return text.length() >= 10 && text.matches("[A-Za-z0-9_]+")
            && text.chars().anyMatch(c -> c >= '0' && c <= '9')
            && text.chars().anyMatch(c -> c >= 'A' && c <= 'Z')
            && text.chars().anyMatch(c -> c >= 'a' && c <= 'z');
    }
}
