package arrays;

/*
 * =============================================================================
 * MultiDimensionalArray.java — 2D, 3D, and Jagged Arrays
 * =============================================================================
 *
 * CONCEPT: Multi-Dimensional Arrays
 * -----------------------------------
 * A multi-dimensional array is an array of arrays.
 *
 * 2D ARRAY: Like a table/grid with rows and columns
 *   int[][] matrix = new int[rows][cols];
 *   Access: matrix[row][col]  (row first, then column)
 *
 * 3D ARRAY: Like a cube (layers × rows × cols)
 *   int[][][] cube = new int[layers][rows][cols];
 *
 * JAGGED ARRAY (Ragged Array):
 *   Each row can have a DIFFERENT number of columns.
 *   int[][] jagged = new int[3][];
 *   jagged[0] = new int[2];  // row 0 has 2 columns
 *   jagged[1] = new int[4];  // row 1 has 4 columns
 *   jagged[2] = new int[1];  // row 2 has 1 column
 *
 * HOW TO RUN:
 *  $ javac -d out src/arrays/MultiDimensionalArray.java
 *  $ java -cp out arrays.MultiDimensionalArray
 * =============================================================================
 */
public class MultiDimensionalArray {

    public static void main(String[] args) {

        // ── 2D ARRAY CREATION ────────────────────────────────────────────────
        System.out.println("=== 2D Array (3x4 matrix) ===");

        // Method 1: Declare size, fill manually
        int[][] matrix = new int[3][4];   // 3 rows, 4 columns
        int value = 1;
        for (int row = 0; row < matrix.length; row++) {           // matrix.length = rows
            for (int col = 0; col < matrix[row].length; col++) {  // matrix[row].length = cols
                matrix[row][col] = value++;
            }
        }

        // Print the matrix
        for (int[] row : matrix) {
            for (int num : row) {
                System.out.printf("%3d", num);
            }
            System.out.println();
        }

        // Method 2: Array literal
        System.out.println("\n=== 2D Array Literal ===");
        int[][] grid = {
            {1, 2, 3},    // row 0
            {4, 5, 6},    // row 1
            {7, 8, 9}     // row 2
        };

        System.out.println("grid[1][2] = " + grid[1][2]);  // row 1, col 2 → 6
        System.out.println("Rows: " + grid.length);
        System.out.println("Cols in row 0: " + grid[0].length);

        // ── MATRIX OPERATIONS ─────────────────────────────────────────────────
        System.out.println("\n=== Matrix Transpose ===");
        int[][] original = {
            {1, 2, 3},
            {4, 5, 6}
        };
        int rows = original.length;
        int cols = original[0].length;

        int[][] transposed = new int[cols][rows];  // dimensions are swapped!
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = original[i][j];
            }
        }

        System.out.println("Original (2x3):");
        printMatrix(original);
        System.out.println("Transposed (3x2):");
        printMatrix(transposed);

        // ── MATRIX MULTIPLICATION ────────────────────────────────────────────
        System.out.println("=== Matrix Multiplication ===");
        int[][] a = {{1, 2}, {3, 4}};     // 2x2 matrix
        int[][] b = {{5, 6}, {7, 8}};     // 2x2 matrix
        int n = 2;
        int[][] product = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    product[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        System.out.println("A * B:");
        printMatrix(product);

        // ── JAGGED ARRAY ─────────────────────────────────────────────────────
        System.out.println("=== Jagged (Ragged) Array ===");

        // Each row has a different number of columns
        int[][] jagged = new int[4][];     // 4 rows, column count unspecified
        jagged[0] = new int[]{1};          // row 0: 1 element
        jagged[1] = new int[]{2, 3};       // row 1: 2 elements
        jagged[2] = new int[]{4, 5, 6};    // row 2: 3 elements
        jagged[3] = new int[]{7, 8, 9, 10};// row 3: 4 elements

        for (int i = 0; i < jagged.length; i++) {
            System.out.print("Row " + i + " (" + jagged[i].length + " cols): ");
            for (int val : jagged[i]) {
                System.out.print(val + " ");
            }
            System.out.println();
        }

        // ── 3D ARRAY ─────────────────────────────────────────────────────────
        System.out.println("\n=== 3D Array (2 layers × 2 rows × 3 cols) ===");
        int[][][] cube = {
            {{1, 2, 3}, {4, 5, 6}},         // layer 0
            {{7, 8, 9}, {10, 11, 12}}        // layer 1
        };

        for (int layer = 0; layer < cube.length; layer++) {
            System.out.println("Layer " + layer + ":");
            for (int[] row : cube[layer]) {
                for (int cell : row) {
                    System.out.printf("%4d", cell);
                }
                System.out.println();
            }
        }

        // ── SUMMING ALL ELEMENTS ─────────────────────────────────────────────
        System.out.println("\n=== Sum of all elements in 2D array ===");
        int total = 0;
        for (int[] row : grid) {
            for (int num : row) {
                total += num;
            }
        }
        System.out.println("Sum of 1..9 grid = " + total);  // 45
    }

    // Helper method to print a 2D matrix neatly
    static void printMatrix(int[][] m) {
        for (int[] row : m) {
            for (int val : row) {
                System.out.printf("%4d", val);
            }
            System.out.println();
        }
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === 2D Array (3x4 matrix) ===
 *    1   2   3   4
 *    5   6   7   8
 *    9  10  11  12
 *
 * === Jagged (Ragged) Array ===
 * Row 0 (1 cols): 1
 * Row 1 (2 cols): 2 3
 * Row 2 (3 cols): 4 5 6
 * Row 3 (4 cols): 7 8 9 10
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Row-column confusion: matrix[col][row] instead of matrix[row][col]
 * 2. Using matrix.length for columns (it gives rows — use matrix[0].length for cols)
 * 3. NullPointerException in jagged array: accessing row before allocating it
 * 4. Not handling non-square matrices in algorithms
 */
