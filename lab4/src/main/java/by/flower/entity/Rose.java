package by.flower.entity;

public class Rose extends Flower {
    private boolean hasThorns;

    public Rose(double price, int freshness, double stemLength, boolean hasThorns) {
        super("Роза", price, freshness, stemLength);
        this.hasThorns = hasThorns;
    }

    public boolean isHasThorns() { return hasThorns; }
}
