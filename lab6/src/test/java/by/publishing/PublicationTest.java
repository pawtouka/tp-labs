package by.publishing;

import by.publishing.model.Book;
import by.publishing.model.Magazine;
import by.publishing.model.Publication;
import by.publishing.model.PublicationProcess;
import by.publishing.model.PublicationState;
import by.publishing.model.StudyGuide;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Dependency-free regression suite. Checks do not rely on JVM assertions (-ea). */
public final class PublicationTest {
    private static int checks;
    private static final BigDecimal PRICE = new BigDecimal("100.00");
    private static final List<Supplier<Publication>> TYPES = List.of(
            () -> new Book("Book", "Author"),
            () -> new Magazine("Magazine", 1),
            () -> new StudyGuide("Guide", "Java"));

    private PublicationTest() { }

    public static void main(String[] args) {
        transitions();
        lifecycle();
        validation();
        specializedOperations();
        System.out.println("PASS: " + checks + " checks");
    }

    private record Operation(Consumer<Publication> action, Set<PublicationState> allowed,
                             PublicationState target) { }

    private static Set<PublicationState> states(PublicationState... values) {
        return Set.of(values);
    }

    private static void transitions() {
        Set<PublicationState> active = states(PublicationState.CONTRACTED, PublicationState.OPEN,
                PublicationState.EDITED, PublicationState.LAID_OUT);
        List<Operation> operations = List.of(
                new Operation(p -> p.signContract("Customer", PRICE),
                        states(PublicationState.NEW), PublicationState.CONTRACTED),
                new Operation(Publication::open, states(PublicationState.CONTRACTED), PublicationState.OPEN),
                new Operation(p -> p.edit("New text"), states(PublicationState.OPEN,
                        PublicationState.EDITED, PublicationState.LAID_OUT), PublicationState.EDITED),
                new Operation(p -> p.layout(12), states(PublicationState.EDITED,
                        PublicationState.LAID_OUT), PublicationState.LAID_OUT),
                new Operation(Publication::sendToPrint, states(PublicationState.LAID_OUT), PublicationState.PRINTED),
                new Operation(Publication::refuse, active, PublicationState.REFUSED),
                new Operation(p -> p.pay(new BigDecimal("1.00")), active, null),
                new Operation(Publication::resume, states(PublicationState.REFUSED,
                        PublicationState.CLOSED), PublicationState.LAID_OUT),
                new Operation(Publication::close, states(PublicationState.CONTRACTED,
                        PublicationState.OPEN, PublicationState.EDITED, PublicationState.LAID_OUT,
                        PublicationState.PRINTED), PublicationState.CLOSED));
        for (Supplier<Publication> type : TYPES) {
            for (PublicationState state : PublicationState.values()) {
                for (Operation op : operations) {
                    Publication p = at(type, state);
                    // Printing needs payment; other operations are checked with an unpaid contract.
                    if (state == PublicationState.LAID_OUT && op.target == PublicationState.PRINTED) p.pay(PRICE);
                    String before = snapshot(p);
                    if (op.allowed.contains(state)) {
                        op.action.accept(p);
                        equal(op.target == null ? state : op.target, p.getState());
                    } else {
                        expect(IllegalStateException.class, () -> op.action.accept(p));
                        equal(before, snapshot(p));
                    }
                }
            }
        }
    }

    private static Publication at(Supplier<Publication> type, PublicationState state) {
        Publication p = type.get();
        if (state == PublicationState.NEW) return p;
        p.signContract("Customer", PRICE);
        if (state == PublicationState.CONTRACTED) return p;
        p.open();
        if (state == PublicationState.OPEN) return p;
        p.edit("Original text");
        if (state == PublicationState.EDITED) return p;
        p.layout(10);
        switch (state) {
            case PRINTED -> { p.pay(PRICE); p.sendToPrint(); }
            case REFUSED -> p.refuse();
            case CLOSED -> p.close();
            default -> { }
        }
        return p;
    }

    private static void lifecycle() {
        for (Supplier<Publication> type : TYPES) {
            Publication p = at(type, PublicationState.LAID_OUT);
            PublicationProcess process = p;
            expect(IllegalStateException.class, process::sendToPrint);
            process.pay(new BigDecimal("0.10"));
            process.pay(new BigDecimal("0.20"));
            equal(new BigDecimal("0.30"), p.getPaid());
            expect(IllegalStateException.class, process::sendToPrint);
            process.pay(new BigDecimal("99.70"));
            equal(true, p.isPaid());
            process.edit("Corrected text");
            equal(0, p.getPages());
            expect(IllegalStateException.class, process::sendToPrint);
            process.layout(20);
            process.sendToPrint();
            process.close();
            process.resume();
            equal(PublicationState.PRINTED, p.getState());
            equal("Corrected text", p.getContent());
            equal(20, p.getPages());
            for (PublicationState state : List.of(PublicationState.CONTRACTED, PublicationState.OPEN,
                    PublicationState.EDITED, PublicationState.LAID_OUT, PublicationState.PRINTED)) {
                Publication original = at(type, state);
                String before = snapshot(original);
                original.close();
                original.resume();
                equal(before, snapshot(original));
                if (state != PublicationState.PRINTED) {
                    original.refuse();
                    original.resume();
                    equal(before, snapshot(original));
                }
            }
        }
    }

    private static void validation() {
        expect(IllegalArgumentException.class, () -> new Book(null, "Author"));
        expect(IllegalArgumentException.class, () -> new Book("  ", "Author"));
        expect(IllegalArgumentException.class, () -> new Book("Book", " "));
        expect(IllegalArgumentException.class, () -> new Magazine("Magazine", 0));
        expect(IllegalArgumentException.class, () -> new StudyGuide("Guide", null));
        for (Supplier<Publication> type : TYPES) {
            Publication p = type.get();
            expect(IllegalArgumentException.class, () -> p.signContract(null, PRICE));
            expect(IllegalArgumentException.class, () -> p.signContract(" ", PRICE));
            equal(PublicationState.NEW, p.getState());
            for (BigDecimal invalid : new BigDecimal[]{null, BigDecimal.ZERO,
                    new BigDecimal("-1"), new BigDecimal("0.001")}) {
                expect(IllegalArgumentException.class, () -> p.signContract("Customer", invalid));
                equal(null, p.getCustomer());
                equal(null, p.getPrice());
                equal(PublicationState.NEW, p.getState());
            }
            p.signContract("Customer", PRICE);
            for (BigDecimal invalid : new BigDecimal[]{null, BigDecimal.ZERO,
                    new BigDecimal("-1"), new BigDecimal("0.001"), new BigDecimal("100.01")}) {
                expect(IllegalArgumentException.class, () -> p.pay(invalid));
                equal(BigDecimal.ZERO, p.getPaid());
            }
            p.pay(PRICE);
            expect(IllegalArgumentException.class, () -> p.pay(BigDecimal.ONE));
            equal(PRICE, p.getPaid());
            p.open();
            p.edit("Text");
            p.layout(1);
            String before = snapshot(p);
            expect(IllegalArgumentException.class, () -> p.edit(null));
            expect(IllegalArgumentException.class, () -> p.edit(" \n "));
            expect(IllegalArgumentException.class, () -> p.layout(0));
            expect(IllegalArgumentException.class, () -> p.layout(-1));
            equal(before, snapshot(p));
        }
    }

    private static void specializedOperations() {
        Book book = (Book) at(TYPES.get(0), PublicationState.LAID_OUT);
        book.addChapter("Interfaces");
        equal(PublicationState.EDITED, book.getState());
        equal(0, book.getPages());
        equal(List.of("Interfaces"), book.getChapters());
        expect(UnsupportedOperationException.class, () -> book.getChapters().add("Other"));
        List<String> oldChapters = book.getChapters();
        book.addChapter("Classes");
        equal(1, oldChapters.size());
        book.setAuthor("New author");
        equal("New author", book.getAuthor());
        Magazine magazine = (Magazine) at(TYPES.get(1), PublicationState.LAID_OUT);
        magazine.addArticle("Science");
        magazine.setIssueNumber(2);
        equal(List.of("Science"), magazine.getArticles());
        equal(2, magazine.getIssueNumber());
        expect(IllegalArgumentException.class, () -> magazine.setIssueNumber(-1));
        StudyGuide guide = (StudyGuide) at(TYPES.get(2), PublicationState.LAID_OUT);
        guide.addExercise("Implement a class");
        guide.setSubject("OOP");
        equal(List.of("Implement a class"), guide.getExercises());
        equal("OOP", guide.getSubject());
        expect(UnsupportedOperationException.class, () -> magazine.getArticles().clear());
        expect(UnsupportedOperationException.class, () -> guide.getExercises().clear());
        for (Supplier<Publication> type : TYPES) {
            for (PublicationState state : PublicationState.values()) {
                Publication p = at(type, state);
                Consumer<String> add;
                if (p instanceof Book b) add = b::addChapter;
                else if (p instanceof Magazine m) add = m::addArticle;
                else add = ((StudyGuide) p)::addExercise;
                Runnable change;
                if (p instanceof Book b) change = () -> b.setAuthor("Updated");
                else if (p instanceof Magazine m) change = () -> m.setIssueNumber(10);
                else change = () -> ((StudyGuide) p).setSubject("Updated");
                if (states(PublicationState.OPEN, PublicationState.EDITED, PublicationState.LAID_OUT).contains(state)) {
                    String before = snapshot(p);
                    expect(IllegalArgumentException.class, () -> add.accept(" "));
                    equal(before, snapshot(p));
                    change.run();
                    add.accept("Entry");
                    equal(state == PublicationState.LAID_OUT ? PublicationState.EDITED : state, p.getState());
                } else {
                    expect(IllegalStateException.class, () -> add.accept("Entry"));
                    expect(IllegalStateException.class, change);
                }
            }
        }
    }

    private static String snapshot(Publication p) {
        return p.getState() + "|" + p.getTitle() + "|" + p.getCustomer() + "|" + p.getPrice()
                + "|" + p.getPaid() + "|" + p.getContent() + "|" + p.getPages();
    }

    private static void equal(Object expected, Object actual) {
        checks++;
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError("Expected " + expected + ", got " + actual);
        }
    }

    private static void expect(Class<? extends Throwable> type, Runnable action) {
        checks++;
        try {
            action.run();
        } catch (Throwable error) {
            if (type.isInstance(error)) return;
            throw new AssertionError("Expected " + type.getSimpleName(), error);
        }
        throw new AssertionError("Expected " + type.getSimpleName() + " but no exception was thrown");
    }
}
