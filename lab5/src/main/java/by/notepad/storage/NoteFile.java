package by.notepad.storage;

import by.notepad.factory.DefaultNoteFactory;
import by.notepad.model.Note;
import by.notepad.model.ReminderNote;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class NoteFile {
    private static final Object WRITE_MONITOR = new Object();
    private static final String HEADER = "notepad-v1";
    private final DefaultNoteFactory factory = new DefaultNoteFactory();
    private final Path path;

    public NoteFile(Path path) {
        this.path = Objects.requireNonNull(path, "Укажите файл заметок").toAbsolutePath();
        if (this.path.getParent() == null) {
            throw new IllegalArgumentException("Укажите путь к файлу заметок");
        }
    }

    public List<Note> load() {
        try {
            return read(path);
        } catch (IOException ex) {
            throw new UncheckedIOException("Не удалось прочитать файл заметок " + path, ex);
        }
    }

    public void update(Consumer<List<Note>> change) {
        // FileLock coordinates processes. The monitor also coordinates threads in this JVM.
        synchronized (WRITE_MONITOR) {
            try {
                Path target = resolveTarget();
                Path lockPath = target.resolveSibling(target.getFileName() + ".lck");
                try (FileChannel channel = FileChannel.open(lockPath,
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                    FileLock lock = channel.lock();
                    try {
                        List<Note> notes = read(target);
                        change.accept(notes);
                        save(target, notes);
                    } finally {
                        lock.release();
                    }
                }
            } catch (IOException ex) {
                throw new UncheckedIOException("Не удалось обновить файл заметок " + path, ex);
            }
        }
    }

    private Path resolveTarget() throws IOException {
        try {
            return path.toRealPath();
        } catch (NoSuchFileException ex) {
            return path.getParent().toRealPath().resolve(path.getFileName());
        }
    }

    private List<Note> read(Path source) throws IOException {
        List<String> lines;
        try {
            lines = Files.readAllLines(source, StandardCharsets.UTF_8);
        } catch (NoSuchFileException ex) {
            return new ArrayList<>();
        }
        List<Note> notes = new ArrayList<>();
        if (lines.isEmpty()) {
            return notes;
        }
        if (!HEADER.equals(lines.get(0))) {
            throw new IOException("Некорректный заголовок файла заметок");
        }
        for (int index = 1; index < lines.size(); index++) {
            try {
                notes.add(parse(lines.get(index)));
            } catch (IllegalArgumentException | DateTimeParseException ex) {
                throw new IOException("Некорректная заметка в строке " + (index + 1), ex);
            }
        }
        return notes;
    }

    private Note parse(String line) {
        String[] values = line.split("\t", -1);
        if (values.length == 4 && "NOTE".equals(values[0])) {
            return factory.create(values[0], LocalDate.parse(values[1]),
                    decode(values[2]), decode(values[3]), null, false);
        }
        if (values.length == 6 && "REMINDER".equals(values[0])
                && ("true".equals(values[5]) || "false".equals(values[5]))) {
            return factory.create(values[0], LocalDate.parse(values[1]),
                    decode(values[2]), decode(values[3]), LocalTime.parse(values[4]),
                    Boolean.parseBoolean(values[5]));
        }
        throw new IllegalArgumentException("Некорректные поля заметки");
    }

    private static void save(Path target, List<Note> notes) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Note note : notes) {
            lines.add(format(note));
        }

        Path temporary = Files.createTempFile(target.getParent(), ".notepad-", ".tmp");
        try {
            Files.write(temporary, lines, StandardCharsets.UTF_8);
            Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | RuntimeException ex) {
            try {
                Files.deleteIfExists(temporary);
            } catch (IOException cleanup) {
                ex.addSuppressed(cleanup);
            }
            throw ex;
        }
    }

    private static String format(Note note) {
        String fields = note.getDate() + "\t" + encode(note.getTitle()) + "\t" + encode(note.getText());
        if (note instanceof ReminderNote reminder) {
            return "REMINDER\t" + fields + "\t" + reminder.getReminderTime() + "\t" + reminder.isActive();
        }
        return "NOTE\t" + fields;
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
