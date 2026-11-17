package by.text;

public final class Main {
    private Main() { }
    public static void main(String[] args) {
        if (args.length < 2) throw new IllegalArgumentException("Usage: a1 text k symbol | a13 text | c1 password");
        for (boolean stream : new boolean[]{false, true}) {
            Object result = switch (args[0]) {
                case "a1" -> {
                    if (args.length != 4 || args[3].codePointCount(0, args[3].length()) != 1)
                        throw new IllegalArgumentException("a1 requires text, k and one symbol");
                    yield Tasks.replaceKth(args[1], Integer.parseInt(args[2]), args[3].codePointAt(0), stream);
                }
                case "a13" -> Tasks.matchingWords(args[1], stream);
                case "c1" -> Tasks.strongPassword(args[1], stream);
                default -> throw new IllegalArgumentException("Unknown task");
            };
            System.out.println((stream ? "Stream: " : "String: ") + result);
        }
    }
}
