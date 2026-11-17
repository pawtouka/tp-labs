package by.text;

import java.util.ArrayList;
import java.util.regex.Pattern;

public final class TextParser {
    private static final Pattern SENTENCE = Pattern.compile("[^.!?]+(?:[.!?]+[\"»”’')\\]]*)?|[.!?]+[\"»”’')\\]]*");
    private static final Pattern TOKEN = Pattern.compile("\\p{L}+|\\p{P}|[^\\p{L}\\p{P}]+");
    public Text parse(String source) {
        var paragraphs = new ArrayList<Paragraph>();
        for (String line : source.split("\\R")) {
            String normalized = line.replaceAll("[\\t ]+", " ").strip();
            if (normalized.isEmpty()) continue;
            var sentences = new ArrayList<Sentence>();
            var matches = SENTENCE.matcher(normalized);
            while (matches.find()) {
                var lexemes = new ArrayList<Lexeme>();
                var tokens = TOKEN.matcher(matches.group().strip());
                while (tokens.find()) {
                    String token = tokens.group();
                    lexemes.add(token.matches("\\p{L}+") ? new Word(token)
                        : token.matches("\\p{P}") ? new Punctuation(token) : new OtherLexeme(token));
                }
                sentences.add(new Sentence(lexemes));
            }
            paragraphs.add(new Paragraph(sentences));
        }
        return new Text(paragraphs);
    }
}
