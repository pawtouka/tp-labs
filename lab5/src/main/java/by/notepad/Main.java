package by.notepad;

import by.notepad.factory.DefaultNoteFactory;
import by.notepad.model.Notepad;
import by.notepad.service.NotepadService;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        if (args.length == 0 || (args.length == 1 && "help".equals(args[0]))) {
            System.out.println("""
                    Команды блокнота (перед командой можно указать --file путь):
                      add ГГГГ-ММ-ДД "Заголовок" "Текст"
                      remind ГГГГ-ММ-ДД ЧЧ:ММ true|false "Заголовок" "Текст"
                      list [ГГГГ-ММ-ДД]
                      search "Часть заголовка"
                    """);
            return;
        }
        try {
            if ("--file".equals(args[0])) {
                if (args.length < 3) {
                    throw new IllegalArgumentException("После --file укажите путь и команду");
                }
                execute(new Notepad(Path.of(args[1])), Arrays.copyOfRange(args, 2, args.length));
            } else {
                execute(new Notepad(), args);
            }
        } catch (IllegalArgumentException | DateTimeParseException | UncheckedIOException ex) {
            System.err.println("Ошибка: " + ex.getMessage());
            System.exit(1);
        }
    }

    private static void execute(Notepad notepad, String[] args) {
        var factory = new DefaultNoteFactory();
        var service = new NotepadService(notepad);
        switch (args[0]) {
            case "add" -> {
                requireLength(args, 4);
                notepad.addNote(factory.create("NOTE", LocalDate.parse(args[1]),
                        args[2], args[3], null, false));
            }
            case "remind" -> {
                requireLength(args, 6);
                if (!"true".equals(args[3]) && !"false".equals(args[3])) {
                    throw new IllegalArgumentException("Активность напоминания должна быть true или false");
                }
                notepad.addNote(factory.create("REMINDER", LocalDate.parse(args[1]),
                        args[4], args[5], LocalTime.parse(args[2]), Boolean.parseBoolean(args[3])));
            }
            case "list" -> {
                if (args.length == 1) {
                    service.getAllSorted().forEach(System.out::println);
                } else {
                    requireLength(args, 2);
                    service.getNotesByDate(LocalDate.parse(args[1])).forEach(System.out::println);
                }
            }
            case "search" -> {
                requireLength(args, 2);
                service.findByTitle(args[1]).forEach(System.out::println);
            }
            default -> throw new IllegalArgumentException("Неизвестная команда: " + args[0]);
        }
    }

    private static void requireLength(String[] args, int length) {
        if (args.length != length) {
            throw new IllegalArgumentException("Неверное число аргументов команды " + args[0]);
        }
    }
}
