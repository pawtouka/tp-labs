package org.example;

import java.util.Objects;

public class Line2D {
    private final RationalFraction a;
    private final RationalFraction b;
    private final RationalFraction c;

    public Line2D(RationalFraction a, RationalFraction b, RationalFraction c) {
        if (a.isZero() && b.isZero()) {
            throw new IllegalArgumentException("Коэффициенты A и B не могут быть одновременно равны нулю.");
        }
        RationalFraction leading = !a.isZero() ? a : b;
        this.a = a.divide(leading);
        this.b = b.divide(leading);
        this.c = c.divide(leading);
    }

    public RationalFraction getA() { return a; }
    public RationalFraction getB() { return b; }
    public RationalFraction getC() { return c; }

    public Point2D intersectWithOX() {
        if (a.isZero()) return null;
        RationalFraction x = c.negate().divide(a);
        return new Point2D(x, RationalFraction.of(0));
    }

    public Point2D intersectWithOY() {
        if (b.isZero()) return null;
        RationalFraction y = c.negate().divide(b);
        return new Point2D(RationalFraction.of(0), y);
    }

    public boolean isParallelTo(Line2D other) {
        RationalFraction det = this.a.multiply(other.b).subtract(this.b.multiply(other.a));
        return det.isZero();
    }

    public Point2D intersectWith(Line2D other) {
        RationalFraction det = this.a.multiply(other.b).subtract(this.b.multiply(other.a));
        if (det.isZero()) {
            return null;
        }
        RationalFraction detX = this.c.negate().multiply(other.b).subtract(this.b.multiply(other.c.negate()));
        RationalFraction detY = this.a.multiply(other.c.negate()).subtract(this.c.negate().multiply(other.a));

        return new Point2D(detX.divide(det), detY.divide(det));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line2D line2D = (Line2D) o;
        return a.equals(line2D.a) && b.equals(line2D.b) && c.equals(line2D.c);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c);
    }

    @Override
    public String toString() {
        return String.format("(%s)x + (%s)y + (%s) = 0", a, b, c);
    }
}
