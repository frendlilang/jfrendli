package tests.expressions.less_than;

import tests.FrendliTestExpectSuccess;
import tests.FrendliTestExpectError;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LessThanTest {
    @Nested
    public class LessThanTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanCompareNumbers() {
            String sourceFile = "expressions/less_than/compare-numbers.frendli";
            String actual = run(sourceFile);
            String expected = """
                    true
                    false
                    false
                    false
                    true
                    false
                    """.trim();
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class LessThanTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotCompareNumberAndNonNumber() {
            String sourceFile = "expressions/less_than/error-compare-number-non-number.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '<'
                      > Message:
                         > The operands must be numbers.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotCompareNonNumberAndNumber() {
            String sourceFile = "expressions/less_than/error-compare-non-number-number.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '<'
                      > Message:
                         > The operands must be numbers.
                    """;
            assertEquals(expected, actual);
        }
    }
}
