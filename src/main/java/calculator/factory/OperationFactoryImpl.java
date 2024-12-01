package calculator.factory;

import calculator.strategy.*;
import calculator.strategy.*;

/**
 * Implementacja Fabryki Abstrakcyjnej tworzącej konkretne obiekty strategii.
 */
public class OperationFactoryImpl implements OperationFactory {
    @Override
    public OperationStrategy createOperation(String operationType) {
        switch (operationType.toLowerCase()) {
            case "add":
            case "+":
                return new AdditionStrategy();
            case "subtract":
            case "-":
                return new SubtractionStrategy();
            case "multiply":
            case "*":
                return new MultiplicationStrategy();
            case "divide":
            case "/":
                return new DivisionStrategy();
            default:
                throw new UnsupportedOperationException("Operacja nie jest wspierana.");
        }
    }

    @Override
    public MatrixOperationStrategy createMatrixOperation(String operationType) {
        switch (operationType.toLowerCase()) {
            case "matrix_add":
            case "+":
                return new MatrixAdditionStrategy();
            case "matrix_multiply":
            case "*":
                return new MatrixMultiplicationStrategy();
            default:
                throw new UnsupportedOperationException("Operacja macierzowa nie jest wspierana.");
        }
    }
}