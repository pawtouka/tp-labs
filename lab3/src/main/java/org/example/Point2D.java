package org.example;

public record Point2D(RationalFraction x, RationalFraction y) {
    @Override
    public String toString() {
        return "(" + x + "; " + y + ")";
    }
}
