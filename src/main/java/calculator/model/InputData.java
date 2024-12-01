package calculator.model;

import java.util.List;

public class InputData {
    private String operation;
    private List<Double> numbers;
    private List<List<Double>> matrixA;
    private List<List<Double>> matrixB;
    private String expression;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public List<Double> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<Double> numbers) {
        this.numbers = numbers;
    }

    public List<List<Double>> getMatrixA() {
        return matrixA;
    }

    public void setMatrixA(List<List<Double>> matrixA) {
        this.matrixA = matrixA;
    }

    public List<List<Double>> getMatrixB() {
        return matrixB;
    }

    public void setMatrixB(List<List<Double>> matrixB) {
        this.matrixB = matrixB;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
