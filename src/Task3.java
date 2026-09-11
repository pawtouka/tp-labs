import java.util.Scanner;
import java.util.List;

public class Task3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Task 2");
        System.out.println("Enter number p");
        int p = scanner.nextInt();

        List<Integer> numbers = utils.readSequence(scanner);

        long counter = numbers.stream().filter(num -> isAMorph(num, p))
                .peek(num -> System.out.printf("Automorphis found: %d%n", num)).count();

            System.out.printf("Prod of numbers greater than 7 is %d%n", counter);
            scanner.close();
    }

    public static boolean isAMorph(int num, int p) {
        if (!utils.isPDD(num, p)) {
            return false;
        }
        long modulo = (long) Math.pow(10, p);

        long square = (long) num * num;

        return square % modulo == num;
    }
}
