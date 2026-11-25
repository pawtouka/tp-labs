package by.planner.storage;

import by.planner.model.Priority;
import by.planner.model.StudyTask;
import by.planner.model.TaskRepository;
import by.planner.model.TaskType;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public final class CsvTaskRepository implements TaskRepository, AutoCloseable {
    private static final String HEADER = "id,type,dueDate,priority,units,completed,title";
    private static final Pattern QUOTED_TITLE = Pattern.compile("\"(?:[^\"]++|\"\")*+\"");
    private final Path path;
    private final FileChannel lockChannel;

    public CsvTaskRepository(Path path) throws IOException {
        Path target = path.toAbsolutePath();
        if (target.getFileName() == null) {
            throw new IOException("Укажите путь к файлу задач");
        }
        this.path = resolve(target);
        this.lockChannel = lock(this.path.resolveSibling(this.path.getFileName() + ".lck"));
    }

    private static Path resolve(Path path) throws IOException {
        try {
            return path.toRealPath();
        } catch (NoSuchFileException ex) {
            return path.getParent().toRealPath().resolve(path.getFileName());
        }
    }

    private static FileChannel lock(Path path) throws IOException {
        FileChannel channel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        try {
            if (channel.tryLock() != null) {
                return channel;
            }
            throw new IOException("Файл задач уже открыт в другом экземпляре");
        } catch (OverlappingFileLockException ex) {
            var failure = new IOException("Файл задач уже открыт в другом экземпляре", ex);
            closeAfterFailure(channel, failure);
            throw failure;
        } catch (IOException | RuntimeException ex) {
            closeAfterFailure(channel, ex);
            throw ex;
        }
    }

    private static void closeAfterFailure(FileChannel channel, Exception failure) {
        try {
            channel.close();
        } catch (IOException ex) {
            failure.addSuppressed(ex);
        }
    }

    @Override
    public void close() throws IOException {
        lockChannel.close();
    }

    @Override
    public List<StudyTask> load() throws IOException {
        BufferedReader reader;
        try {
            reader = Files.newBufferedReader(path);
        } catch (NoSuchFileException ex) {
            return List.of();
        }
        try (reader) {
            if (!HEADER.equals(reader.readLine())) {
                throw new IOException("Некорректный заголовок CSV");
            }
            var tasks = new ArrayList<StudyTask>();
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    tasks.add(parse(line));
                } catch (IllegalArgumentException | DateTimeParseException ex) {
                    throw new IOException("Некорректная запись CSV в строке " + lineNumber, ex);
                }
            }
            return List.copyOf(tasks);
        }
    }

    @Override
    public void save(List<StudyTask> tasks) throws IOException {
        Path temporary = Files.createTempFile(path.getParent(), ".planner-", ".tmp");
        try {
            try (var writer = Files.newBufferedWriter(temporary)) {
                writer.write(HEADER);
                writer.newLine();
                for (StudyTask task : tasks) {
                    writer.write(format(task));
                    writer.newLine();
                }
            }
            try {
                Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ex) {
                throw new IOException("Каталог не поддерживает атомарную замену файла", ex);
            }
        } catch (IOException ex) {
            try {
                Files.deleteIfExists(temporary);
            } catch (IOException cleanup) {
                ex.addSuppressed(cleanup);
            }
            throw ex;
        }
    }

    private static StudyTask parse(String line) {
        String[] fields = line.split(",", 7);
        if (fields.length != 7 || !QUOTED_TITLE.matcher(fields[6]).matches()
                || !("true".equals(fields[5]) || "false".equals(fields[5]))) {
            throw new IllegalArgumentException("Некорректные поля CSV");
        }
        String title = fields[6].substring(1, fields[6].length() - 1).replace("\"\"", "\"");
        var details = new StudyTask.Details(title, LocalDate.parse(fields[2]), Priority.valueOf(fields[3]),
                Integer.parseInt(fields[4]));
        return StudyTask.of(UUID.fromString(fields[0]), TaskType.valueOf(fields[1]), details,
                Boolean.parseBoolean(fields[5]));
    }

    private static String format(StudyTask task) {
        var details = task.details();
        String title = "\"" + details.title().replace("\"", "\"\"") + "\"";
        return String.join(",", task.id().toString(), task.type().name(), details.dueDate().toString(),
                details.priority().name(), Integer.toString(details.units()), Boolean.toString(task.completed()), title);
    }
}
