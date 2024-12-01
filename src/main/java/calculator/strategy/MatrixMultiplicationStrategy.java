package calculator.strategy;

public class MatrixMultiplicationStrategy implements MatrixOperationStrategy {
    @Override
    public double[][] execute(double[][] a, double[][] b) {
        if (a.length == 0 || b.length == 0) {
            throw new IllegalArgumentException("Nie można wykonać operacji na pustych macierzach.");
        }

        int rowsA = a.length;
        int colsA = a[0].length;
        int rowsB = b.length;
        int colsB = b[0].length;

        if (colsA != rowsB) {
            throw new IllegalArgumentException("Liczba kolumn w macierzy A musi być równa liczbie wierszy w macierzy B.");
        }

        double[][] result = new double[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                double sum = 0.0;
                for (int k = 0; k < colsA; k++) {
                    sum += a[i][k] * b[k][j];
                }
                result[i][j] = sum;
            }
        }

        return result;
    }
}