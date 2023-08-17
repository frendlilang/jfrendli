package tests.expressions.multiply;

import tests.FrendliTestExpectError;
import tests.FrendliTestExpectSuccess;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiplyTest {
    @Nested
    public class MultiplyTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanMultiplyWholeNumbers() {
            String sourceFile = "expressions/multiply/multiply-whole-numbers.frendli";
            String actual = run(sourceFile);
            String expected = "408";
            assertEquals(expected, actual);
        }

        @Test
        void itCanMultiplyFloatingPointNumbers() {
            String sourceFile = "expressions/multiply/multiply-floating-point-numbers.frendli";
            String actual = run(sourceFile);
            String expected = """
                    700.6652
                    4.1472
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanMultiplyNegativeFloatingPointNumbers() {
            String sourceFile = "expressions/multiply/multiply-negative-floating-point-numbers.frendli";
            String actual = run(sourceFile);
            String expected = """
                    -700.6652
                    4.1472
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanMultiplyWholeNumberAndFloatingPointNumber() {
            String sourceFile = "expressions/multiply/multiply-whole-number-floating-point-number.frendli";
            String actual = run(sourceFile);
            String expected = """
                    414.72
                    4.08
                    """.trim();
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class MultiplyTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotMultiplyNumberAndNonNumber() {
            String sourceFile = "expressions/multiply/error-multiply-number-non-number.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '*'
                      > Message:
                         > The operands must be numbers.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotMultiplyNonNumberAndNumber() {
            String sourceFile = "expressions/multiply/error-multiply-non-number-number.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '*'
                      > Message:
                         > The operands must be numbers.
                    """;
            assertEquals(expected, actual);
        }
    }
}
