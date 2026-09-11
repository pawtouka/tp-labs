import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class utils {
    public static List<Integer> readSequence(Scanner scanner){
        System.out.println("How many numbers do u want to enter?");
        int num = scanner.nextInt();
        List<Integer> sequence = new ArrayList<>();
        if (num > 0){
            int prod = 1;

            System.out.printf("Input %d numbers:%n", num);
            for (int i = 0; i < num; i++) {
                sequence.add(scanner.nextInt());
            }
        }
        scanner.close();
        return sequence;
    }

    public static int gcd(int a, int b){
        while (b != 0){
            int temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
    }

    public static boolean isPDD(int x, int p){
        if (p < 0) return false;
        long l_bound = (long) Math.powExact(10, p - 1);
        long u_bound = (long) Math.powExact(10, p) - 1;
        return ((l_bound <= x)&(x <= u_bound));
    }
}
