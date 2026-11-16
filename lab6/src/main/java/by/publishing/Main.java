package by.publishing;

import by.publishing.model.Book;
import by.publishing.model.Magazine;
import by.publishing.model.Publication;
import by.publishing.model.PublicationProcess;
import by.publishing.model.StudyGuide;
import java.math.BigDecimal;
import java.util.List;

/** Executable example of polymorphic publication management. */
public final class Main {
    private Main() { }

    public static void main(String[] args) {
        Book book = new Book("Основы Java", "Тимофей");
        Magazine magazine = new Magazine("Наука", 1);
        StudyGuide guide = new StudyGuide("Практикум", "Программирование");
        List<Publication> publications = List.of(book, magazine, guide);
        for (PublicationProcess process : publications) {
            process.signContract("Университет", new BigDecimal("100.00"));
            process.open();
        }
        book.addChapter("Классы и интерфейсы");
        magazine.addArticle("Новые открытия");
        guide.addExercise("Реализовать интерфейс управления изданием");
        for (PublicationProcess process : publications) {
            process.edit("Подготовленный текст издания");
            process.layout(32);
            process.refuse();
            process.resume();
            process.pay(new BigDecimal("40.00"));
            process.pay(new BigDecimal("60.00"));
            process.sendToPrint();
            process.close();
            process.resume();
        }
        publications.forEach(System.out::println);
    }
}
