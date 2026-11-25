package by.numbers;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static by.numbers.NumbersException.Kind.*;

public final class NumberFileTest {
    private static final NumberFile READER = new NumberFile();

    private NumberFileTest() { }

    public static void main(String[] args) throws Exception {
        var directory = Files.createTempDirectory("lab10-");
        var input = directory.resolve("numbers.txt");
        try {
            valid(input, "3\nen-US\t1,000.5\nde-DE\t-200,25\nar-EG\t٢٫٧٥\n", "803", "267.6666666666666666666666666666667");
            valid(input, "3\nfr-FR\t1\u202f234,5\nen-US\t+.5E+2\nen-US\t-1,234.5\n", "50", "16.66666666666666666666666666666667");
            valid(input, "4\nen-US\t1e20\nen-US\t0.1\nen-US\t-1e20\nen-US\t0.2", "0.3", "0.075");
            valid(input, "1\nar-EG\t\u061c-٢٫٥\n", "-2.5", "-2.5");
            valid(input, "1\nen-US\t4.9e-324", "4.9e-324", "4.9e-324");
            String max = new BigDecimal(Double.MAX_VALUE).toPlainString();
            valid(input, "2\nen-US\t" + max + "\nen-US\t" + max, new BigDecimal(max).multiply(BigDecimal.valueOf(2)).toString(),
                    new BigDecimal(max).round(java.math.MathContext.DECIMAL128).toString());

            rejected(directory.resolve("absent.txt"), FILE_NOT_FOUND, 0);
            rejected(directory, READ_ERROR, 0);
            broken(input, "", INVALID_COUNT, 1);
            broken(input, "0\n", INVALID_COUNT, 1);
            broken(input, "999999999999\n", INVALID_COUNT, 1);
            broken(input, "2\nen-US\t1\n", MISSING_RECORD, 3);
            broken(input, "1\nen-US\t1\n\n", EXTRA_RECORD, 3);
            broken(input, "1\nen-US\n", INVALID_RECORD, 2);
            broken(input, "1\nen-US\t1\t2\n", INVALID_RECORD, 2);
            broken(input, "1\nen_US\t1\n", INVALID_LOCALE, 2);
            broken(input, "1\nzz-ZZ\t1\n", INVALID_LOCALE, 2);
            for (String invalid : new String[]{"1,23.4", "1.2tail", "1e", "NaN", "∞", "1.2,3", "--1"}) {
                broken(input, "1\nen-US\t" + invalid, INVALID_NUMBER, 2);
            }
            broken(input, "1\nen-US\t1.8e308", OUT_OF_RANGE, 2);
            broken(input, "1\nen-US\t1e-400", OUT_OF_RANGE, 2);
            broken(input, "1\nen-US\t1e2147483648", OUT_OF_RANGE, 2);
            Files.write(input, new byte[]{(byte) 0xc3, (byte) 0x28});
            rejected(input, READ_ERROR, 0);
        } finally {
            Files.deleteIfExists(input);
            Files.delete(directory);
        }
        System.out.println("28 проверок пройдены.");
    }

    private static void valid(Path path, String contents, String sum, String mean) throws Exception {
        Files.writeString(path, contents);
        var result = READER.read(path);
        int count = Integer.parseInt(contents.substring(0, contents.indexOf('\n')));
        if (result.count() != count || result.sum().compareTo(new BigDecimal(sum)) != 0
                || result.mean().compareTo(new BigDecimal(mean)) != 0) {
            throw new AssertionError("Неверный результат: " + result);
        }
    }

    private static void broken(Path path, String contents, NumbersException.Kind kind, long line)
            throws IOException {
        Files.writeString(path, contents);
        rejected(path, kind, line);
    }

    private static void rejected(Path path, NumbersException.Kind kind, long line) {
        try {
            READER.read(path);
            throw new AssertionError("Ожидалось исключение " + kind);
        } catch (NumbersException ex) {
            if (ex.kind() != kind || ex.line() != line) {
                throw new AssertionError("Ожидалось " + kind + " на строке " + line, ex);
            }
        }
    }
}
