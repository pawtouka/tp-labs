package org.example;

import java.util.Objects;

public class RationalFraction {
    private final long numerator;
    private final long denominator;

    public RationalFraction(long numerator, long denominator) {
        if (denominator == 0) {
            throw new ArithmeticException("Знаменатель не может быть равен нулю.");
        }
        long gcd = gcd(Math.abs(numerator), Math.abs(denominator));
        long sign = (numerator * denominator < 0) ? -1 : 1;
        this.numerator = sign * Math.abs(numerator) / gcd;
        this.denominator = Math.abs(denominator) / gcd;
    }

    public static RationalFraction of(long numerator) {
        return new RationalFraction(numerator, 1);
    }

    public RationalFraction add(RationalFraction other) {
        return new RationalFraction(
                this.numerator * other.denominator + other.numerator * this.denominator,
                this.denominator * other.denominator
        );
    }

    public RationalFraction subtract(RationalFraction other) {
        return new RationalFraction(
                this.numerator * other.denominator - other.numerator * this.denominator,
                this.denominator * other.denominator
        );
    }

    public RationalFraction multiply(RationalFraction other) {
        return new RationalFraction(
                this.numerator * other.numerator,
                this.denominator * other.denominator
        );
    }

    public RationalFraction divide(RationalFraction other) {
        return new RationalFraction(
                this.numerator * other.denominator,
                this.denominator * other.numerator
        );
    }

    public boolean isZero() {
        return this.numerator == 0;
    }

    private static long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public RationalFraction negate() {
        return new RationalFraction(-this.numerator, this.denominator);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RationalFraction that = (RationalFraction) o;
        return numerator == that.numerator && denominator == that.denominator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }

    @Override
    public String toString() {
        return denominator == 1 ? String.valueOf(numerator) : numerator + "/" + denominator;
    }
}

