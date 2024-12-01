package calculator;

import calculator.factory.OperationFactory;
import calculator.factory.OperationFactoryImpl;
import calculator.strategy.MatrixOperationStrategy;
import calculator.strategy.OperationStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import calculator.expression.ExpressionEvaluator;
import calculator.factory.*;
import calculator.model.InputData;
import calculator.strategy.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * W poniższym rozwiązaniu zastosowane są zgodnie z poleceniem zadania dwa wzorce projektowe: Strategii oraz Fabryki Abstrakcyjnej.
 *
 * Wzorzec strategii oddziela algorytmy operacji od warstwy kodu który z nich korzysta.
 *   - Dzięki temu mamy nieogarniczoną możliwość rozbudowywania fabryki o kolejne operacje matematyczne
 *   - W związku z tym że kod danej operacji (strategii) jest oddzielony od innych wszystkie błędy jakie w sobie zawiera nie wpływają na inne strategie
 *   - Minusem jest duża ilość klas i potencjalnie lekki spadek wydajności jeśli kompilator nie zinlinuje tych wywołań, plusem łatwość w zarządzaniu
 *
 * Wzorzec Fabryki Abstrakcyjnej został zastosowany do tworzenia obiektów strategii na podstawie typu operacji.
 * Dzięki użyciu Fabryki:
 *   - Oddzielamy logikę decydującą o tym która strategia ma zostać użyta od reszty programu,
 *     decyzja ta jest skupiona w jednym miejscu co ułatwia zarządzanie kodem.
 *   - Dodanie nowej operacji wymaga tylko aktualizacji fabryki
 *
 * Wykorzystanie wzorców projektowych zwiększa czytelność kodu poprzez separację odpowiedzialności.
 * Możemy zatem testować każdą strategię niezależnie, a w przypadku zmiany wymagań możemy szybko
 * dostosować aplikację, modyfikując lub dodając odpowiednie strategie i aktualizując fabrykę.
 * Wzorce projektowe niestety zwykle są trudne w implementacji i trudne w zaprojektowaniu,
 * często wybierając zły wzorzec utrudniamy sobie nierzadko łatwe zadanie.
 */

/*
 * Główna klasa aplikacji kalkulatora.
 */
public class Main {
    public static void main(String[] args) {
        // utworzenie fabryki operacji
        OperationFactory factory = new OperationFactoryImpl();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Wybierz tryb pracy:");
        System.out.println("1. Klawiatura");
        System.out.println("2. Plik JSON");
        int choice = Integer.parseInt(scanner.nextLine());

        if (choice == 1) {
            handleKeyboardInput(factory, scanner);
        } else if (choice == 2) {
            handleFileInput(factory, scanner);
        } else {
            System.out.println("Nieprawidłowy wybór.");
        }
    }

    /*
     * Obsługa wejścia z klawiatury.
     */
    private static void handleKeyboardInput(OperationFactory factory, Scanner scanner) {
        System.out.println("Wybierz typ operacji:");
        System.out.println("1. Podstawowe operacje");
        System.out.println("2. Wyrażenie złożone");
        System.out.println("3. Operacje na macierzach");
        int operationType = Integer.parseInt(scanner.nextLine());

        switch (operationType) {
            case 1:
                handleBasicOperations(factory, scanner);
                break;
            case 2:
                handleComplexExpression(factory, scanner);
                break;
            case 3:
                handleMatrixOperations(factory, scanner);
                break;
            default:
                System.out.println("Nieprawidłowy wybór.");
        }
    }

    /*
     * Obsługa podstawowych operacji matematycznych.
     * Wykorzystuje programowanie funkcyjne do operacji na zbiorach liczb.
     */
    private static void handleBasicOperations(OperationFactory factory, Scanner scanner) {
        // pobranie od użytkownika typu operacji
        System.out.print("Podaj operację (+, -, *, /): ");
        String operation = scanner.nextLine();

        // pobranie od użytkownika listy liczb
        System.out.print("Podaj liczby (oddzielone spacją): ");
        String[] numberStrings = scanner.nextLine().split(" ");

        // konwersja ciągów znaków na listę liczb typu double
        List<Double> numbers = Arrays.stream(numberStrings)
                .map(Double::parseDouble)
                .collect(Collectors.toList());

        try {
            // utworzenie strategii operacji za pomocą fabryki
            OperationStrategy strategy = factory.createOperation(operation);

            // wykonanie operacji na liczbach z wykorzystaniem programowania funkcyjnego
            double result = numbers.stream()
                    .reduce(strategy::execute)
                    .orElse(0.0);

            System.out.println("Wynik: " + result);
        } catch (ArithmeticException | UnsupportedOperationException e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    /*
     * Obsługa wyrażeń złożonych.
     */
    private static void handleComplexExpression(OperationFactory factory, Scanner scanner) {
        // pobranie wyrażenia od użytkownika
        System.out.print("Podaj wyrażenie: ");
        String expression = scanner.nextLine();

        // utworzenie ExpressionEvaluator z przekazaną fabryką
        ExpressionEvaluator evaluator = new ExpressionEvaluator(factory);
        try {
            double result = evaluator.evaluate(expression);
            System.out.println("Wynik: " + result);
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    /*
     * Obsługa operacji na macierzach.
     */
    private static void handleMatrixOperations(OperationFactory factory, Scanner scanner) {
        // pobranie od użytkownika typu operacji na macierzach
        System.out.print("Podaj operację na macierzach (+, *): ");
        String operation = scanner.nextLine();

        // wczytanie pierwszej macierzy
        System.out.println("Podaj elementy pierwszej macierzy wierszami. Wpisz pustą linię, aby zakończyć wprowadzanie:");
        double[][] matrixA = readMatrix(scanner);

        // wczytanie drugiej macierzy
        System.out.println("Podaj elementy drugiej macierzy wierszami. Wpisz pustą linię, aby zakończyć wprowadzanie:");
        double[][] matrixB = readMatrix(scanner);

        try {
            // utworzenie strategii operacji macierzowej za pomocą fabryki
            MatrixOperationStrategy strategy = factory.createMatrixOperation(operation);
            handleMatrixOp(matrixA, matrixB, strategy);
        } catch (UnsupportedOperationException | IllegalArgumentException e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    /*
     * Wyświetla macierz na stdout
     */
    private static void handleMatrixOp(double[][] matrixA, double[][] matrixB, MatrixOperationStrategy strategy) {
        double[][] result = strategy.execute(matrixA, matrixB);

        System.out.println("Wynik operacji na macierzach:");
        for (double[] row : result) {
            for (double elem : row) {
                System.out.print(elem + " ");
            }
            System.out.println();
        }
    }

    /*
     * Wczytuje macierz od użytkownika
     */
    private static double[][] readMatrix(Scanner scanner) {
        List<double[]> matrixList = new ArrayList<>();
        int cols = -1;  // liczba kolumn w macierzy

        while (true) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) {
                break;
            }
            String[] elements = line.trim().split("\\s+");
            if (cols == -1) {
                cols = elements.length; // ustalenie liczby kolumn na podstawie pierwszego wiersza
            } else if (elements.length != cols) {
                System.out.println("Wszystkie wiersze muszą mieć tę samą liczbę kolumn. Spróbuj ponownie.");
                continue;
            }
            double[] row = new double[elements.length];
            for (int i = 0; i < elements.length; i++) {
                row[i] = Double.parseDouble(elements[i]);
            }
            matrixList.add(row); // dodanie wiersza do listy
        }

        return matrixList.toArray(new double[matrixList.size()][]);
    }

    /*
     * Obsługa wejścia z pliku JSON.
     */
    private static void handleFileInput(OperationFactory factory, Scanner scanner) {
        Gson gson = new GsonBuilder().create();

        System.out.print("Podaj ścieżkę do pliku: ");
        String filePath = scanner.nextLine();

        try (FileReader reader = new FileReader(filePath)) {
            // wczytanie danych z pliku JSON do obiektu InputData
            InputData inputData = gson.fromJson(reader, InputData.class);

            if (inputData.getExpression() != null) {
                // wyrażenie złożone
                ExpressionEvaluator evaluator = new ExpressionEvaluator(factory);
                double result = evaluator.evaluate(inputData.getExpression());
                System.out.println("Wynik: " + result);
            } else if (inputData.getMatrixA() != null && inputData.getMatrixB() != null) {
                // operacje na macierzach
                MatrixOperationStrategy strategy = factory.createMatrixOperation(inputData.getOperation());
                double[][] matrixA = convertListToArray(inputData.getMatrixA());
                double[][] matrixB = convertListToArray(inputData.getMatrixB());
                handleMatrixOp(matrixA, matrixB, strategy);
            } else if (inputData.getNumbers() != null) {
                // operacje podstawowe
                OperationStrategy strategy = factory.createOperation(inputData.getOperation());
                double result = inputData.getNumbers().stream()
                        .reduce(strategy::execute)
                        .orElse(0.0);

                System.out.println("Wynik: " + result);
            } else {
                System.out.println("Nieprawidłowe dane wejściowe.");
            }

        } catch (IOException e) {
            System.out.println("Błąd odczytu pliku: " + e.getMessage());
        } catch (ArithmeticException | UnsupportedOperationException e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private static double[][] convertListToArray(List<List<Double>> list) {
        int rows = list.size();
        int cols = list.get(0).size();
        double[][] array = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            List<Double> row = list.get(i);
            for (int j = 0; j < cols; j++) {
                array[i][j] = row.get(j);
            }
        }
        return array;
    }
}