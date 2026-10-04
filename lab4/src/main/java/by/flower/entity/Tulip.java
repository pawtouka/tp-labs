package by.flower.entity;

public class Tulip extends Flower {
    private String color;

    public Tulip(double price, int freshness, double stemLength, String color) {
        super("Тюльпан", price, freshness, stemLength);
        this.color = color;
    }

    public String getColor() { return color; }
}
