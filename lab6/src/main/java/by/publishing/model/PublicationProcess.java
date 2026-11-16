package by.publishing.model;

import java.math.BigDecimal;

/** Operations supported by every publication. */
public interface PublicationProcess {
    void signContract(String customer, BigDecimal price);
    void open();
    void edit(String content);
    void layout(int pages);
    void sendToPrint();
    void refuse();
    void pay(BigDecimal amount);
    void resume();
    void close();
}
