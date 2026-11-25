package by.numbers;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static by.numbers.NumbersException.Kind.*;

public final class NumberFile {
    public record Statistics(int count, BigDecimal sum, BigDecimal mean) { }

    public Statistics read(Path path) throws NumbersException {
        try (var reader = Files.newBufferedReader(path)) {
            int count = readCount(reader.readLine());
            var sum = BigDecimal.ZERO;
            for (int index = 0; index < count; index++) {
                long line = index + 2L;
                String record = reader.readLine();
                if (record == null) {
                    throw new NumbersException(MISSING_RECORD, line, "Нет записи с числом");
                }
                int tab = record.indexOf('\t');
                if (tab <= 0 || tab == record.length() - 1 || record.indexOf('\t', tab + 1) != -1) {
                    throw new NumbersException(INVALID_RECORD, line, "Ожидаются локаль и число через табуляцию");
                }
                sum = sum.add(LocalizedNumber.parse(record.substring(0, tab), record.substring(tab + 1), line));
            }
            if (reader.readLine() != null) {
                throw new NumbersException(EXTRA_RECORD, count + 2L, "Лишняя запись после последнего числа");
            }
            return new Statistics(count, sum, sum.divide(BigDecimal.valueOf(count), MathContext.DECIMAL128));
        } catch (NoSuchFileException ex) {
            throw new NumbersException(FILE_NOT_FOUND, 0, "Файл не найден: " + path, ex);
        } catch (IOException | SecurityException ex) {
            throw new NumbersException(READ_ERROR, 0, "Не удалось прочитать файл: " + path, ex);
        }
    }

    private static int readCount(String header) throws NumbersException {
        if (header != null && header.matches("[0-9]+")) {
            try {
                int count = Integer.parseInt(header);
                if (count > 0) {
                    return count;
                }
            } catch (NumberFormatException ex) {
                throw new NumbersException(INVALID_COUNT, 1, "Слишком большое количество записей", ex);
            }
        }
        throw new NumbersException(INVALID_COUNT, 1, "Ожидается положительное целое N");
    }
}
