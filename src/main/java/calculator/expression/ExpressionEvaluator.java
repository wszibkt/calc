package calculator.expression;

import calculator.strategy.OperationStrategy;
import calculator.factory.OperationFactory;

import java.util.*;

public class ExpressionEvaluator {
    // priorytety operatorów
    private final Map<String, Integer> _operatorPrecedence;

    // fabryka operacji
    private final OperationFactory factory;

    public ExpressionEvaluator(OperationFactory factory) {
        this.factory = factory;
        this._operatorPrecedence = new HashMap<>();
        this._operatorPrecedence.put("+", 1);
        this._operatorPrecedence.put("-", 1);
        this._operatorPrecedence.put("*", 2);
        this._operatorPrecedence.put("/", 2);
        this._operatorPrecedence.put("u-", 3);
    }

    public double evaluate(String expression) {
        // tokenizacja wyrażenia za pomocą prostego lexera
        List<String> tokens = tokenize(expression);

        // konwersja do Odwrotnej Notacji Polskiej w celu rozwiązania problemu z handlowaniem nawiasów
        List<String> rpn = toRPN(tokens);

        // obliczanie wyniku
        return computeRPN(rpn);
    }

    /**
     * zamienia wyrażenie na ciąg tokenów
     * obsługa licz w zapisie dziesiętnym oraz formacie naukowym
     * wsparcie operacji jednoargumentowych (np. 2---2=0 i 2--2=4)
     */
    private List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        int index = 0;
        int length = expression.length();

        while (index < length) {
            char currentChar = expression.charAt(index);

            if (Character.isWhitespace(currentChar)) {
                // białe znaki są ignorowane
                index++;
            } else if (currentChar == '(' || currentChar == ')' || currentChar == '+'  || currentChar == '*' || currentChar == '/') {
                // operatory i nawiasy
                tokens.add(String.valueOf(currentChar));
                index++;
            }
            else if (currentChar == '-') {
                if (tokens.isEmpty()) {
                    // unarny minus ponieważ jest to start wyrażenia
                    tokens.add("u-");
                } else {
                    // sprawdzamy ostatni token,
                    // jeśli jest to operator lub otwarcie nawiasu jest to unarny minus,
                    // w przeciwnym wypadku binarny czyli odejmowanie
                    // nie jest to logika idealna, ale w tym wypadku wystarczająca
                    String last = tokens.get(tokens.size() - 1);

                    if (_operatorPrecedence.containsKey(last) || last.equals("(")) {
                        // unarny minus
                        tokens.add("u-");
                    } else {
                        // binarny minus
                        tokens.add("-");
                    }
                }
                index++;
            } else if (Character.isDigit(currentChar) || currentChar == '.') {
                // liczba
                // obsługiwana jest tylko stanrdowa notacja dziesiętna oraz notacja naukowa
                StringBuilder number = new StringBuilder();

                boolean hasDecimalPoint = false;
                boolean hasExponent = false;

                while (index < length) {
                    char c = expression.charAt(index);

                    if (Character.isDigit(c)) {
                        number.append(c);
                        index++;
                    } else if (c == '.' && !hasDecimalPoint && !hasExponent) {
                        hasDecimalPoint = true;
                        number.append(c);
                        index++;
                    } else if ((c == 'e' || c == 'E') && !hasExponent) {
                        hasExponent = true;
                        number.append(c);
                        index++;
                        if (index < length && (expression.charAt(index) == '+' || expression.charAt(index) == '-')) {
                            number.append(expression.charAt(index));
                            index++;
                        }
                    } else {
                        break;
                    }
                }
                tokens.add(number.toString());
            } else {
                // nieobsługiwany znak
                throw new IllegalArgumentException("Nieznany znak: " + currentChar);
            }
        }

        return tokens;
    }

    /**
     * konwertuje listę tokenów do odwrotnej notacji polskiej
     */
    private List<String> toRPN(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Deque<String> operatorStack = new ArrayDeque<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                output.add(token);
            } else if (_operatorPrecedence.containsKey(token)) {
                while(!operatorStack.isEmpty()
                        && _operatorPrecedence.containsKey(operatorStack.peek())
                        && _operatorPrecedence.get(token) <= _operatorPrecedence.get(operatorStack.peek())) {
                    output.add(operatorStack.pop());
                }
                operatorStack.push(token);
            } else if (token.equals("(")) {
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    output.add(operatorStack.pop());
                }
                if (!operatorStack.isEmpty() && operatorStack.peek().equals("(")) {
                    operatorStack.pop();
                } else {
                    throw new IllegalArgumentException("Niezgodność nawiasów w wyrażeniu.");
                }
            } else {
                throw new IllegalArgumentException("Nieznany token: " + token);
            }
        }

        while (!operatorStack.isEmpty()) {
            String op = operatorStack.pop();
            if (op.equals("(") || op.equals(")")) {
                throw new IllegalArgumentException("Niezgodność nawiasów w wyrażeniu.");
            }
            output.add(op);
        }

        return output;
    }

    /**
     * oblicza wartość wyrażenia w odwrotnej notacji polskiej
     */
    private double computeRPN(List<String> rpn) {
        Deque<Double> stack = new ArrayDeque<>();

        for (String token : rpn) {
            if (isNumber(token)) {
                // jeśli token jest liczbą, parsujemy go i umieszczamy na stosie
                stack.push(Double.parseDouble(token));
            } else if (token.equals("u-")) {
                // unary minus, zmieniamy znak wyrażenia na szczycie stosu
                double a = stack.pop();
                stack.push(-a);
            } else if (_operatorPrecedence.containsKey(token)) {
                // operator binarny, pobieramy dwie wartości ze stosu i wykonujemy operację
                if (stack.size() < 2)
                    throw new IllegalArgumentException("Nieoczekiwany token: " + token);

                double b = stack.pop();
                double a = stack.pop();
                double result = applyOperator(a, b, token);

                stack.push(result);
            } else {
                throw new IllegalArgumentException("Nieznany operator: " + token);
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Błąd w obliczeniach.");
        }

        return stack.pop();
    }

    /**
     * zastosowuje operator do dwóch operandów korzystając z fabryki i strategii
     */
    private double applyOperator(double a, double b, String operator) {
        try {
            // uzyskanie strategii z fabryki
            OperationStrategy strategy = factory.createOperation(operator);

            // wykonanie operacji
            return strategy.execute(a, b);
        } catch (UnsupportedOperationException e) {
            throw new IllegalArgumentException("Nieznany operator: " + operator);
        }
    }

    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
