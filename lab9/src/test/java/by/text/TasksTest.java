package by.text;

import java.nio.file.*;
import java.util.*;

public final class TasksTest {
    private TasksTest() { }
    private static void eq(Object a, Object b) {
        if (!Objects.equals(a, b)) throw new AssertionError(a + " != " + b);
    }
    public static void main(String[] args) throws Exception {
        var parser = new TextParser();
        var finder = new CommonWords();
        Text text = parser.parse("Java java код! Код полезен. JAVA нужен?\n\tНет   кода.");
        var result = finder.find(text);
        eq(2, result.maximum());
        eq(List.of(new CommonWords.Match("java", List.of(1, 3)),
            new CommonWords.Match("код", List.of(1, 2))), result.matches());
        eq(2, text.paragraphs().size());
        eq(4, text.sentences().size());
        eq("Нет кода.", text.sentences().get(3).text());
        eq(true, text.sentences().get(0).lexemes().get(0) instanceof Word);
        eq(true, text.sentences().get(0).lexemes().get(5) instanceof Punctuation);
        eq(4, text.sentences().get(0).words().get(0).symbols().size());
        eq(0, finder.find(parser.parse(" \t\n")).maximum());
        eq(0, finder.find(parser.parse("!!! 123")).maximum());
        eq(1, finder.find(parser.parse("да да да")).maximum());
        eq(2, finder.find(parser.parse("Ёж. ёж!")).maximum());
        eq(1, finder.find(parser.parse("Еж. Ёж!")).maximum());
        eq(2, parser.parse("«Код!» Потом.").sentences().size());
        eq("«Код!»", parser.parse("«Код!» Потом.").sentences().get(0).text());
        Path dir = Files.createTempDirectory("lab9-test-");
        Path in = dir.resolve("in.txt"), out = dir.resolve("out.txt");
        try {
            Files.writeString(in, "Код\t  полезен. Код работает!");
            Main.main(new String[]{in.toString(), out.toString()});
            eq("Максимум предложений: 2\nСлово: код\n1: Код полезен.\n2: Код работает!\n", Files.readString(out));
            try { Main.main(new String[]{in.toString(), in.toString()}); throw new AssertionError(); }
            catch (IllegalArgumentException expected) { }
            try { Main.main(new String[0]); throw new AssertionError(); }
            catch (IllegalArgumentException expected) { }
        } finally { Files.deleteIfExists(out); Files.deleteIfExists(in); Files.delete(dir); }
        System.out.println("All lab9 checks passed");
    }
}
