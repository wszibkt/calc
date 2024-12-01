package calculator.strategy;


/**
 *  Interfejs Strategii umożliwia implementację różnych operacji matematycznych.
 */
public interface OperationStrategy {
    double execute(double a, double b);
}
