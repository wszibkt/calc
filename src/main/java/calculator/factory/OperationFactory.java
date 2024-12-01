package calculator.factory;

import calculator.strategy.OperationStrategy;
import calculator.strategy.MatrixOperationStrategy;

/**
 * Interfejs Fabryki Abstrakcyjnej do tworzenia obiektów strategii operacji.
 */
public interface OperationFactory {
    OperationStrategy createOperation(String operationType);
    MatrixOperationStrategy createMatrixOperation(String operationType);
}
