package by.numbers;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        int status = 0;
        try {
            if (args.length != 1) {
                System.err.println("Использование: by.numbers.Main <файл>");
                status = 2;
            } else {
                var result = new NumberFile().read(Path.of(args[0]));
                System.out.println("Сумма: " + result.sum().stripTrailingZeros().toPlainString());
                System.out.println("Среднее: " + result.mean().stripTrailingZeros().toPlainString());
            }
        } catch (InvalidPathException | NumbersException ex) {
            System.err.println("Ошибка: " + ex.getMessage());
            status = 1;
        } catch (OutOfMemoryError ex) {
            System.err.println("Ошибка: недостаточно памяти.");
            status = 1;
        }
        if (status != 0) {
            System.exit(status);
        }
    }
}
