package by.flower.entity;

public abstract class Flower {
    private String name;
    private double price;
    private int freshness; // 1 - самый свежий, 10 - увядающий
    private double stemLength;

    public Flower(String name, double price, int freshness, double stemLength) {
        this.name = name;
        this.price = price;
        this.freshness = freshness;
        this.stemLength = stemLength;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getFreshness() { return freshness; }
    public double getStemLength() { return stemLength; }

    @Override
    public String toString() {
        return String.format("%s{price=%.2f, freshness=%d, stemLength=%.1f}",
                name, price, freshness, stemLength);
    }
}
