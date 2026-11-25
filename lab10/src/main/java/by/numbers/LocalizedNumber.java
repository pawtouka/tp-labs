package by.numbers;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.IllformedLocaleException;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import static by.numbers.NumbersException.Kind.*;

final class LocalizedNumber {
    private static final Set<Locale> LOCALES = Set.of(NumberFormat.getAvailableLocales());
    private static final BigDecimal MAX = new BigDecimal(Double.MAX_VALUE);

    private LocalizedNumber() { }

    static BigDecimal parse(String tag, String literal, long line) throws NumbersException {
        var format = (DecimalFormat) NumberFormat.getNumberInstance(locale(tag, line));
        var symbols = format.getDecimalFormatSymbols();
        char zero = symbols.getZeroDigit();
        int groupSize = format.getGroupingSize();
        String digit = "[" + zero + "-" + (char) (zero + 9) + "]";
        String grouping = Pattern.quote(String.valueOf(symbols.getGroupingSeparator()));
        String decimal = Pattern.quote(String.valueOf(symbols.getDecimalSeparator()));
        String whole = digit + "+";
        if (format.isGroupingUsed() && groupSize > 0) {
            String group = grouping + digit + "{" + groupSize + "}";
            whole = "(?:" + whole + "|" + digit + "{1," + groupSize + "}(?:" + group + ")++)";
        }
        String mantissa = "(?:" + whole + "(?:" + decimal + digit + "*)?|" + decimal + digit + "+)";
        String exponent = "(?:[eE][+-]?" + digit + "+)?";

        String negativePrefix = format.getNegativePrefix();
        boolean negative = literal.startsWith(negativePrefix);
        String magnitude = negative ? literal.substring(negativePrefix.length()) : literal;
        String negativeSuffix = format.getNegativeSuffix();
        if (negative && !negativeSuffix.isEmpty()) {
            if (!magnitude.endsWith(negativeSuffix)) {
                throw new NumbersException(INVALID_NUMBER, line, "Некорректная запись числа");
            }
            magnitude = magnitude.substring(0, magnitude.length() - negativeSuffix.length());
        }
        if (!negative && magnitude.startsWith("+")) {
            magnitude = magnitude.substring(1);
        }

        if (!Pattern.matches(mantissa + exponent, magnitude)) {
            throw new NumbersException(INVALID_NUMBER, line, "Некорректная запись числа");
        }

        var ascii = new StringBuilder(magnitude.length());
        if (negative) {
            ascii.append('-');
        }
        for (int i = 0; i < magnitude.length(); i++) {
            char c = magnitude.charAt(i);
            if (c == symbols.getDecimalSeparator()) {
                ascii.append('.');
            } else if (c >= zero && c <= zero + 9) {
                ascii.append((char) ('0' + c - zero));
            } else if (c != symbols.getGroupingSeparator()) {
                ascii.append(c);
            }
        }

        try {
            var number = new BigDecimal(ascii.toString());
            if (number.signum() == 0) {
                return BigDecimal.ZERO;
            }
            number = number.stripTrailingZeros();
            if (number.abs().compareTo(MAX) <= 0 && number.doubleValue() != 0.0) {
                return number;
            }
        } catch (NumberFormatException | ArithmeticException ex) {
            throw new NumbersException(OUT_OF_RANGE, line, "Число вне допустимого диапазона", ex);
        }
        throw new NumbersException(OUT_OF_RANGE, line, "Число вне допустимого диапазона");
    }

    private static Locale locale(String tag, long line) throws NumbersException {
        try {
            var locale = new Locale.Builder().setLanguageTag(tag).build();
            if (LOCALES.contains(locale.stripExtensions())) {
                return locale;
            }
        } catch (IllformedLocaleException ex) {
            throw new NumbersException(INVALID_LOCALE, line, "Неверный языковой тег", ex);
        }
        throw new NumbersException(INVALID_LOCALE, line, "Локаль недоступна в JDK");
    }
}
