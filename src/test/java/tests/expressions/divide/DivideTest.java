package tests.expressions.divide;

import tests.FrendliTestExpectError;
import tests.FrendliTestExpectSuccess;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DivideTest {
    @Nested
    public class DivideTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanDivideWholeNumbers() {
            String sourceFile = "expressions/divide/divide-whole-numbers.frendli";
            String actual = run(sourceFile);
            String expected = "0.35294117647058826";
            assertEquals(expected, actual);
        }

        @Test
        void itCanDivideFloatingPointNumbers() {
            String sourceFile = "expressions/divide/divide-floating-point-numbers.frendli";
            String actual = run(sourceFile);
            String expected = """
                    0.2173300457907714
                    0.003472222222222222
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanDivideNegativeFloatingPointNumbers() {
            String sourceFile = "expressions/divide/divide-negative-floating-point-numbers.frendli";
            String actual = run(sourceFile);
            String expected = """
                    -0.2173300457907714
                    0.003472222222222222
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanDivideWholeNumberAndFloatingPointNumber() {
            String sourceFile = "expressions/divide/divide-whole-number-floating-point-number.frendli";
            String actual = run(sourceFile);
            String expected = """
                    0.3472222222222222
                    35.29411764705882
                    """.trim();
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class DivideTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotDivideNumberAndNonNumber() {
            String sourceFile = "expressions/divide/error-divide-number-non-number.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '/'
                      > Message:
                         > The operands must be numbers.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotDivideNonNumberAndNumber() {
            String sourceFile = "expressions/divide/error-divide-non-number-number.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '/'
                      > Message:
                         > The operands must be numbers.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotDivideByZero() {
            String sourceFile = "expressions/divide/error-divide-by-zero.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '/'
                      > Message:
                         > Division by zero is not allowed. The operand must be a non-zero number.
                    """;
            assertEquals(expected, actual);
        }
    }
}
