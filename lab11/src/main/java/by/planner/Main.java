package by.planner;

import by.planner.controller.PlannerController;
import by.planner.model.PlannerService;
import by.planner.storage.CsvTaskRepository;
import by.planner.view.PlannerView;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> start(args));
    }

    private static void start(String[] args) {
        Logger log = Logger.getLogger("by.planner");
        log.setLevel(Level.FINE);
        log.setUseParentHandlers(false);
        configure(log, new ConsoleHandler());

        try {
            var file = new FileHandler("lab11.log", true);
            file.setEncoding(StandardCharsets.UTF_8.name());
            configure(log, file);
        } catch (IOException | SecurityException ex) {
            startupError(log, "Не удалось открыть lab11.log. Проверьте доступ к текущему каталогу.", ex);
            return;
        }

        if (args.length > 1) {
            startupError(log, "Передайте путь к CSV одним аргументом.", null);
            return;
        }

        try {
            Path path = Path.of(args.length == 0 ? "tasks.csv" : args[0]);
            log.log(Level.FINE, "Чтение файла {0}", path);
            var repository = new CsvTaskRepository(path);
            try {
                var planner = new PlannerService(repository);
                log.log(Level.INFO, "Загружено задач: {0}", planner.summary().total());
                var view = new PlannerView();
                new PlannerController(planner, view, log);
                view.open(() -> {
                    closeRepository(repository, log);
                    for (Handler handler : log.getHandlers()) {
                        log.removeHandler(handler);
                        handler.close();
                    }
                });
            } catch (IOException | RuntimeException ex) {
                closeRepository(repository, log);
                throw ex;
            }
        } catch (IOException | InvalidPathException | SecurityException ex) {
            startupError(log, "Не удалось открыть список задач. Проверьте путь, содержимое CSV и другие окна приложения.", ex);
        }
    }

    private static void closeRepository(CsvTaskRepository repository, Logger log) {
        try {
            repository.close();
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Не удалось закрыть файл задач", ex);
        }
    }

    private static void configure(Logger log, Handler handler) {
        handler.setLevel(Level.FINE);
        handler.setFormatter(new LogFormatter());
        log.addHandler(handler);
    }

    private static void startupError(Logger log, String message, Throwable cause) {
        log.log(Level.SEVERE, message, cause);
        JOptionPane.showMessageDialog(null, message, "Ошибка запуска", JOptionPane.ERROR_MESSAGE);
    }

    private static final class LogFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            String level = switch (record.getLevel().getName()) {
                case "FINE" -> "DEBUG";
                case "WARNING" -> "WARN";
                case "SEVERE" -> "ERROR";
                default -> record.getLevel().getName();
            };
            var output = new StringBuilder().append(Instant.ofEpochMilli(record.getMillis()))
                    .append(' ').append(level).append(' ').append(formatMessage(record)).append('\n');
            if (record.getThrown() != null) {
                var trace = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(trace));
                output.append(trace);
            }
            return output.toString();
        }
    }
}
