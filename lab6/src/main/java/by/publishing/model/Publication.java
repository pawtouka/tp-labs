package by.publishing.model;

import java.math.BigDecimal;
import java.util.Objects;

/** Common workflow; state changes are possible only through checked operations. */
public abstract class Publication implements PublicationProcess {
    private final String title;
    private PublicationState state = PublicationState.NEW;
    private PublicationState suspendedState;
    private String customer;
    private BigDecimal price;
    private BigDecimal paid = BigDecimal.ZERO;
    private String content = "";
    private int pages;

    protected Publication(String title) {
        this.title = requireText(title);
    }

    protected static String requireText(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Text must not be blank");
        }
        return value.strip();
    }

    protected final void requireState(PublicationState... allowed) {
        for (PublicationState candidate : allowed) {
            if (state == candidate) return;
        }
        throw new IllegalStateException("Operation is unavailable in state " + state);
    }

    /** Specialized editorial changes also invalidate an existing layout. */
    protected final void editorialChange() {
        requireState(PublicationState.OPEN, PublicationState.EDITED, PublicationState.LAID_OUT);
        if (state == PublicationState.LAID_OUT) {
            state = PublicationState.EDITED;
            pages = 0;
        }
    }

    @Override
    public final void signContract(String customer, BigDecimal price) {
        requireState(PublicationState.NEW);
        String checkedCustomer = requireText(customer);
        if (price == null || price.signum() <= 0 || price.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Price must be positive with at most two decimal places");
        }
        this.customer = checkedCustomer;
        this.price = price;
        state = PublicationState.CONTRACTED;
    }

    @Override
    public final void open() {
        requireState(PublicationState.CONTRACTED);
        state = PublicationState.OPEN;
    }

    @Override
    public final void edit(String content) {
        requireState(PublicationState.OPEN, PublicationState.EDITED, PublicationState.LAID_OUT);
        String checked = requireText(content);
        this.content = checked;
        pages = 0;
        state = PublicationState.EDITED;
    }

    @Override
    public final void layout(int pages) {
        requireState(PublicationState.EDITED, PublicationState.LAID_OUT);
        if (pages <= 0) throw new IllegalArgumentException("Pages must be positive");
        this.pages = pages;
        state = PublicationState.LAID_OUT;
    }

    @Override
    public final void sendToPrint() {
        requireState(PublicationState.LAID_OUT);
        if (!isPaid()) throw new IllegalStateException("Full payment is required before printing");
        state = PublicationState.PRINTED;
    }

    @Override
    public final void pay(BigDecimal amount) {
        requireState(PublicationState.CONTRACTED, PublicationState.OPEN,
                PublicationState.EDITED, PublicationState.LAID_OUT);
        if (amount == null || amount.signum() <= 0 || amount.stripTrailingZeros().scale() > 2
                || paid.add(amount).compareTo(price) > 0) {
            throw new IllegalArgumentException("Invalid payment or overpayment");
        }
        paid = paid.add(amount);
    }

    @Override
    public final void refuse() {
        requireState(PublicationState.CONTRACTED, PublicationState.OPEN,
                PublicationState.EDITED, PublicationState.LAID_OUT);
        suspendedState = state;
        state = PublicationState.REFUSED;
    }

    @Override
    public final void close() {
        requireState(PublicationState.CONTRACTED, PublicationState.OPEN,
                PublicationState.EDITED, PublicationState.LAID_OUT, PublicationState.PRINTED);
        suspendedState = state;
        state = PublicationState.CLOSED;
    }

    @Override
    public final void resume() {
        requireState(PublicationState.REFUSED, PublicationState.CLOSED);
        state = Objects.requireNonNull(suspendedState);
        suspendedState = null;
    }

    public final String getTitle() { return title; }
    public final PublicationState getState() { return state; }
    public final String getCustomer() { return customer; }
    public final BigDecimal getPrice() { return price; }
    public final BigDecimal getPaid() { return paid; }
    public final boolean isPaid() { return price != null && paid.compareTo(price) == 0; }
    public final String getContent() { return content; }
    public final int getPages() { return pages; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " — " + title + " | " + state + " | paid=" + paid;
    }
}
