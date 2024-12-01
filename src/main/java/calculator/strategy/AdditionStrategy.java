package calculator.strategy;

public class AdditionStrategy  implements OperationStrategy {
    @Override
    public double execute(double a, double b) {
        return a + b;
    }
}