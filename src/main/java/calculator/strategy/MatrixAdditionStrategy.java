package calculator.strategy;

public class MatrixAdditionStrategy implements MatrixOperationStrategy {
    @Override
    public double[][] execute(double[][] a, double[][] b) {
        if (a.length == 0 || b.length == 0) {
            throw new IllegalArgumentException("Nie można wykonać operacji na pustych macierzach.");
        }

        int rows = a.length;
        int cols = a[0].length;

        if (rows != b.length || cols != b[0].length) {
            throw new IllegalArgumentException("Macierze muszą mieć te same wymiary.");
        }

        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }
}