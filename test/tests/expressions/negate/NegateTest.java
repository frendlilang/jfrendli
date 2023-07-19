package tests.expressions.negate;

import tests.FrendliTestExpectError;
import tests.FrendliTestExpectSuccess;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NegateTest {
    @Nested
    public class NegateTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanNegateNumber() {
            String sourceFile = "expressions/negate/negate-number.frendli";
            String actual = run(sourceFile);
            String expected = """
                    -12
                    12
                    -12
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanNegateGroupResultNumber() {
            String sourceFile = "expressions/negate/negate-group-result-number.frendli";
            String actual = run(sourceFile);
            String expected = """
                    -12
                    46
                    -46
                    """.trim();
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class NegateTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotNegateNonNumber() {
            String sourceFile = "expressions/negate/error-negate-non-number.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '-'
                      > Message:
                         > The operand must be a number.
                    """;
            assertEquals(expected, actual);
        }
    }
}
