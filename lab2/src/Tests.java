import java.util.Arrays;

public class Tests {
    public static void runAllTests() {
        System.out.println("TESTS for T1\n");

        testCaseFromTaskDescription();
        testCaseStandard();
        testCaseEdgeCases();

        System.out.println("END TESTS for T1");
        System.out.println("TESTS for T2\n");

        testCaseRemoveEvenColumnsStandard();
        testCaseRemoveEvenColumnsAllEven();
        testCaseRemoveEvenColumnsNoEven();

        System.out.println("END TESTS for T2");
    }

    private static void testCaseFromTaskDescription() {
        System.out.println("T1.1");

        int[][] matrix = {
                {1, 1, 3, 1, 10, 2, 3, 3}, 
                {1, 1, 3, 10, 1, 1, 5, 6}   
        };
        int k = 0;

        executeAndPrint(matrix, k);
    }

    private static void testCaseStandard() {
        System.out.println("1.2");
        int[][] matrix = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {1, 3, 9, 10},
                {1, 2, 3, 9}
        };
        int k = 0;

        executeAndPrint(matrix, k);
    }

    private static void testCaseEdgeCases() {
        System.out.println("T1.3");
        int[][] matrix = {
                {5, 5, 5, 5},
                {1, 2, 8, 8}, 
                {2, 3, 8, 9}, 
                {2, 4, 7, 8}
        };
        int k = 3;

        executeAndPrint(matrix, k);
    }

    private static void executeAndPrint(int[][] matrix, int k) {
        System.out.println("BegM:");
        Utils.printMatrix(matrix);
        System.out.println("Целевая k-строка (индекс " + k + "): " + Arrays.toString(matrix[k]));
        System.out.println();

        int[][] classicResult = Utils.sortBySimilarityClassic(matrix, k);
        System.out.println("ResCls:");
        Utils.printMatrix(classicResult);
        System.out.println();

        int[][] streamResult = Utils.sortBySimilarityStream(matrix, k);
        System.out.println("ResSAPI:");
        Utils.printMatrix(streamResult);
    }

    private static void testCaseRemoveEvenColumnsStandard() {
        System.out.println("T2.1");
        int[][] matrix = {
                {1,  2,  3,  4,  5},
                {7,  6,  9,  8,  0}
        };
        executeAndPrintCompression(matrix);
    }

    private static void testCaseRemoveEvenColumnsAllEven() {
        System.out.println("T2.2");
        int[][] matrix = {
                {2, 4, 6},
                {8, 0, 2}
        };
        executeAndPrintCompression(matrix);
    }

    private static void testCaseRemoveEvenColumnsNoEven() {
        System.out.println("T2.3");
        int[][] matrix = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };
        executeAndPrintCompression(matrix);
    }

    private static void executeAndPrintCompression(int[][] matrix) {
        System.out.println("BegM:");
        Utils.printMatrix(matrix);
        System.out.println();

        System.out.println("ResCls:");
        int[][] classicResult = Utils.removeEvenColumnsClassic(matrix);
        Utils.printMatrix(classicResult);
        System.out.println();

        System.out.println("ResSAPI:");
        int[][] streamResult = Utils.removeEvenColumnsStream(matrix);
        Utils.printMatrix(streamResult);
    }
}
