package by.text;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Main {
    private Main() { }
    public static void main(String[] args) throws java.io.IOException {
        if (args.length != 2) throw new IllegalArgumentException("Usage: input.txt output.txt");
        Path input = Path.of(args[0]);
        Path output = Path.of(args[1]);
        if (input.toAbsolutePath().normalize().equals(output.toAbsolutePath().normalize())
                || Files.exists(output) && Files.isSameFile(input, output))
            throw new IllegalArgumentException("Input and output must differ");
        Text text = new TextParser().parse(Files.readString(input, StandardCharsets.UTF_8));
        Files.writeString(output, new CommonWords().report(text), StandardCharsets.UTF_8);
    }
}
