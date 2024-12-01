package calculator.strategy;

public class MultiplicationStrategy implements OperationStrategy {
    @Override
    public double execute(double a, double b) {
        return a * b;
    }
}