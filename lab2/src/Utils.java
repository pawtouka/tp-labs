import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Utils {
    public static int getSimilarityCoefficient(int[] row1, int[] row2) {
        Set<Integer> set1 = new HashSet<>();
        for (int num : row1) {
            set1.add(num);
        }

        Set<Integer> set2 = new HashSet<>();
        for (int num : row2) {
            set2.add(num);
        }

        set1.retainAll(set2);
        return set1.size();
    }

    public static int[][] sortBySimilarityClassic(int[][] matrix, int k) {
        if (matrix == null || matrix.length == 0 || k < 0 || k >= matrix.length) {
            throw new IllegalArgumentException("Некорректная матрица или индекс k");
        }

        int[][] sortedMatrix = matrix.clone();
        int[] targetRow = matrix[k];

        Arrays.sort(sortedMatrix, new Comparator<int[]>() {
            @Override
            public int compare(int[] row1, int[] row2) {
                int sim1 = getSimilarityCoefficient(row1, targetRow);
                int sim2 = getSimilarityCoefficient(row2, targetRow);
                return Integer.compare(sim2, sim1);
            }
        });

        return sortedMatrix;
    }

    public static int[][] sortBySimilarityStream(int[][] matrix, int k) {
        if (matrix == null || matrix.length == 0 || k < 0 || k >= matrix.length) {
            throw new IllegalArgumentException("Некорректная матрица или индекс k");
        }

        int[] targetRow = matrix[k];

        return Arrays.stream(matrix)
                .sorted((row1, row2) -> {
                    int sim1 = getSimilarityCoefficient(row1, targetRow);
                    int sim2 = getSimilarityCoefficient(row2, targetRow);
                    return Integer.compare(sim2, sim1); // По убыванию
                })
                .toArray(int[][]::new);
    }

    public static int[][] removeEvenColumnsClassic(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) return matrix;

        int rows = matrix.length;
        int cols = matrix[0].length;

        boolean[] isEvenColumn = new boolean[cols];
        int remainingColsCount = 0;

        for (int j = 0; j < cols; j++) {
            boolean allEven = true;
            for (int i = 0; i < rows; i++) {
                if (matrix[i][j] % 2 != 0) {
                    allEven = false;
                    break;
                }
            }
            isEvenColumn[j] = allEven;
            if (!allEven) {
                remainingColsCount++;
            }
        }

        if (remainingColsCount == cols) return cloneMatrix(matrix);
        if (remainingColsCount == 0) return new int[rows][0];

        int[][] result = new int[rows][remainingColsCount];
        for (int i = 0; i < rows; i++) {
            int targetColIndex = 0;
            for (int j = 0; j < cols; j++) {
                if (!isEvenColumn[j]) {
                    result[i][targetColIndex] = matrix[i][j];
                    targetColIndex++;
                }
            }
        }

        return result;
    }

    public static int[][] removeEvenColumnsStream(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) return matrix;

        int rows = matrix.length;
        int cols = matrix[0].length;

        int[][] transposedAndFiltered = IntStream.range(0, cols)
                .mapToObj(j -> {
                    int[] column = new int[rows];
                    for (int i = 0; i < rows; i++) {
                        column[i] = matrix[i][j];
                    }
                    return column;
                })
                .filter(column -> !Arrays.stream(column).allMatch(num -> num % 2 == 0))
                .toArray(int[][]::new);

        if (transposedAndFiltered.length == 0) return new int[rows][0];

        int newCols = transposedAndFiltered.length;
        return IntStream.range(0, rows)
                .mapToObj(i -> IntStream.range(0, newCols)
                        .map(j -> transposedAndFiltered[j][i])
                        .toArray())
                .toArray(int[][]::new);
    }

    private static int[][] cloneMatrix(int[][] matrix) {
        int[][] clone = new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            clone[i] = matrix[i].clone();
        }
        return clone;
    }

    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }
}
