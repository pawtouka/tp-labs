package by.text;

import java.util.*;

public final class CommonWords {
    public record Match(String word, List<Integer> sentenceNumbers) {
        public Match { sentenceNumbers = List.copyOf(sentenceNumbers); }
    }
    public record Result(int maximum, List<Match> matches) {
        public Result { matches = List.copyOf(matches); }
    }
    public Result find(Text text) {
        var index = new TreeMap<String, List<Integer>>();
        var sentences = text.sentences();
        for (int i = 0; i < sentences.size(); i++) {
            var seen = new HashSet<String>();
            for (Word word : sentences.get(i).words()) {
                String key = word.text().toLowerCase(Locale.ROOT);
                if (seen.add(key)) index.computeIfAbsent(key, ignored -> new ArrayList<>()).add(i + 1);
            }
        }
        int max = index.values().stream().mapToInt(List::size).max().orElse(0);
        var matches = index.entrySet().stream().filter(e -> e.getValue().size() == max)
            .map(e -> new Match(e.getKey(), e.getValue())).toList();
        return new Result(max, matches);
    }
    public String report(Text text) {
        Result result = find(text);
        var output = new StringBuilder("Максимум предложений: ").append(result.maximum()).append('\n');
        var sentences = text.sentences();
        for (Match match : result.matches()) {
            output.append("Слово: ").append(match.word()).append('\n');
            for (int number : match.sentenceNumbers())
                output.append(number).append(": ").append(sentences.get(number - 1).text()).append('\n');
        }
        return output.toString();
    }
}
