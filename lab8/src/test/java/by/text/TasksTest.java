package by.text;

import java.util.List;
import java.util.Objects;

public final class TasksTest {
    private TasksTest() { }
    private static void eq(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) throw new AssertionError(expected + " != " + actual);
    }
    public static void main(String[] args) {
        for (boolean stream : new boolean[]{false, true}) {
            eq("к*т, а!\nд*м", Tasks.replaceKth("кот, а!\nдом", 2, '*', stream));
            eq("кот", Tasks.replaceKth("кот", 4, '*', stream));
            eq("*от", Tasks.replaceKth("кот", 1, '*', stream));
            eq("ко$", Tasks.replaceKth("кот", 3, '$', stream));
            eq("𐐀*", Tasks.replaceKth("𐐀𐐁", 2, '*', stream));
            eq("", Tasks.replaceKth("", 1, '*', stream));
            try { Tasks.replaceKth("кот", 0, '*', stream); throw new AssertionError(); }
            catch (IllegalArgumentException expected) { }
            eq(List.of("топот", "я", "𐐀a𐐀"), Tasks.matchingWords("топот, я топот Anna кот 𐐀a𐐀", stream));
            eq(List.of(), Tasks.matchingWords("123 !", stream));
            for (String good : List.of("Abcdefghi1", "A________1a")) eq(true, Tasks.strongPassword(good, stream));
            for (String bad : List.of("", "Abcdefgh1", "abcdefgh12", "ABCDEFGHI1", "Abcdefghij", "Abcdefghi1!", "Абвdefghi1", "Abcdefghi1\n"))
                eq(false, Tasks.strongPassword(bad, stream));
        }
        System.out.println("All lab8 checks passed");
    }
}
