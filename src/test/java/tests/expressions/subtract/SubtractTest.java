package tests.expressions.subtract;

import tests.FrendliTestExpectError;
import tests.FrendliTestExpectSuccess;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtractTest {
    @Nested
    public class SubtractTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanSubtractWholeNumbers() {
            String sourceFile = "expressions/subtract/subtract-whole-numbers.frendli";
            String actual = run(sourceFile);
            String expected = "-22";
            assertEquals(expected, actual);
        }

        @Test
        void itCanSubtractFloatingPointNumbers() {
            String sourceFile = "expressions/subtract/subtract-floating-point-numbers.frendli";
            String actual = run(sourceFile);
            String expected = """
                    -44.44
                    -34.440000000000005
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanSubtractNegativeFloatingPointNumbers() {
            String sourceFile = "expressions/subtract/subtract-negative-floating-point-numbers.frendli";
            String actual = run(sourceFile);
            String expected = """
                    -69.12
                    34.440000000000005
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanSubtractWholeNumberAndFloatingPointNumber() {
            String sourceFile = "expressions/subtract/subtract-whole-number-floating-point-number.frendli";
            String actual = run(sourceFile);
            String expected = """
                    -22.560000000000002
                    11.66
                    """.trim();
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class SubtractTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotSubtractNumberAndNonNumber() {
            String sourceFile = "expressions/subtract/error-subtract-number-non-number.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '-'
                      > Message:
                         > The operands must be numbers.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotSubtractNonNumberAndNumber() {
            String sourceFile = "expressions/subtract/error-subtract-non-number-number.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '-'
                      > Message:
                         > The operands must be numbers.
                    """;
            assertEquals(expected, actual);
        }
    }
}
