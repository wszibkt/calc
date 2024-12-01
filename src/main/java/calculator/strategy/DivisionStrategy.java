package calculator.strategy;

public class DivisionStrategy implements OperationStrategy {
    @Override
    public double execute(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("Dzielenie przez zero!");
        }
        return a / b;
    }
}