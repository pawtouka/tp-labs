package by.functional;

import java.util.Arrays;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        int[] numbers = {3, -7, 0, 3, Integer.MAX_VALUE, Integer.MIN_VALUE};
        String text = "The quick brown fox jumps over the lazy dog.";
        var customAddTen = CustomTasks.addition().apply(10);
        var standardAddTen = StandardTasks.addition().apply(10);
        var customMatch = CustomTasks.matches().apply("[A-Za-z]+");
        var standardMatch = StandardTasks.matches().apply("[A-Za-z]+");
        var customSplit = CustomTasks.split().apply("[,;]\\s*");
        var standardSplit = StandardTasks.split().apply("[,;]\\s*");
        System.out.println("Собственные интерфейсы:");
        System.out.println("10 + 5 = " + customAddTen.apply(5));
        System.out.println("Java соответствует [A-Za-z]+: " + customMatch.apply("Java"));
        System.out.println("Разбиение: " + Arrays.toString(customSplit.apply("Java, Kotlin; Scala")));
        System.out.println("По убыванию: " + Arrays.toString(CustomTasks.descending().apply(numbers)));
        System.out.println("Английская панграмма: " + CustomTasks.pangram(Alphabet.ENGLISH).apply(text));
        System.out.println("Интерфейсы JDK:");
        System.out.println("10 + 5 = " + standardAddTen.applyAsInt(5));
        System.out.println("Java соответствует [A-Za-z]+: " + standardMatch.test("Java"));
        System.out.println("Разбиение: " + Arrays.toString(standardSplit.apply("Java, Kotlin; Scala")));
        System.out.println("По убыванию: " + Arrays.toString(StandardTasks.descending().apply(numbers)));
        System.out.println("Английская панграмма: " + StandardTasks.pangram(Alphabet.ENGLISH).test(text));
    }
}
