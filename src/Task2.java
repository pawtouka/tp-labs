import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class Task2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Task 2");
        List<Integer> numbers = utils.readSequence(scanner);

        for (int number : numbers) {
            int product = prodG7(Math.abs(number));
            System.out.printf("For number %d, the prod of num > 7 is %d%n", number, product);
        }
        scanner.close();
    }

    public static int prodG7(int num){
        if (num == 0) return 0;
        int prod = 1;
        boolean flag = false;

        while (num > 0) {
            int digit = num % 10;
            if (digit > 7) {
                prod *= digit;
                flag = true;
            }
            num /= 10;
        }
        return flag ? prod : 0;
    }
}
