package by.functional;

/** A function with one argument, including a function returned by currying. */
@FunctionalInterface
public interface TaskFunction<T, R> {
    R apply(T value);
}
