import java.util.Scanner;
import java.util.List;

public class Task1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Task 1");
        System.out.println("Enter number p");
        int p = scanner.nextInt();

        List<Integer> numbers = utils.readSequence(scanner);

        long counter = numbers.stream().filter(num -> utils.gcd(num, p) == 1).
                peek(num -> System.out.printf("Co-prime found: %d%n", num)).count();

        System.out.printf("Number of co-prime for %d is %d%n", p, counter);
        scanner.close();
    }
}
