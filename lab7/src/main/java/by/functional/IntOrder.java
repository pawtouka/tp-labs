package by.functional;

/** Comparison contract: negative means the left value comes first. */
@FunctionalInterface
public interface IntOrder {
    int compare(int left, int right);
}
