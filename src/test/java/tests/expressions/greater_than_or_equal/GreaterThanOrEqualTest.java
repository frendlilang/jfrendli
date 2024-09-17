package tests.expressions.greater_than_or_equal;

import tests.FrendliTestExpectSuccess;
import tests.FrendliTestExpectError;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GreaterThanOrEqualTest {
    @Nested
    public class GreaterThanOrEqualTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanCompareNumbers() {
            String sourceFile = "expressions/greater_than_or_equal/compare-numbers.frendli";
            String actual = run(sourceFile);
            String expected = """
                    false
                    true
                    true
                    true
                    false
                    true
                    """.trim();
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class GreaterThanOrEqualTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotCompareNumberAndNonNumber() {
            String sourceFile = "expressions/greater_than_or_equal/error-compare-number-non-number.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '>='
                      > Message:
                         > The operands must be numbers.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotCompareNonNumberAndNumber() {
            String sourceFile = "expressions/greater_than_or_equal/error-compare-non-number-number.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '>='
                      > Message:
                         > The operands must be numbers.
                    """;
            assertEquals(expected, actual);
        }
    }
}
