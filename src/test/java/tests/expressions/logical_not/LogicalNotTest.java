package tests.expressions.logical_not;

import tests.FrendliTestExpectSuccess;
import tests.FrendliTestExpectError;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogicalNotTest {
    @Nested
    public class LogicalNotTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanNegateTrue() {
            String sourceFile = "expressions/logical_not/not-true.frendli";
            String actual = run(sourceFile);
            String expected = "false";
            assertEquals(expected, actual);
        }

        @Test
        void itCanNegateFalse() {
            String sourceFile = "expressions/logical_not/not-false.frendli";
            String actual = run(sourceFile);
            String expected = "true";
            assertEquals(expected, actual);
        }

        @Test
        void itCanNegateGroupResultBoolean() {
            String sourceFile = "expressions/logical_not/not-group-result-boolean.frendli";
            String actual = run(sourceFile);
            String expected = """
                    false
                    true
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanRecursivelyNegateFalse() {
            String sourceFile = "expressions/logical_not/recursive-not-false.frendli";
            String actual = run(sourceFile);
            String expected = """
                    false
                    true
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanRecursivelyNegateTrue() {
            String sourceFile = "expressions/logical_not/recursive-not-true.frendli";
            String actual = run(sourceFile);
            String expected = """
                    true
                    false
                    """.trim();
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class LogicalNotTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotNegateNonBoolean() {
            String sourceFile = "expressions/logical_not/error-not-non-boolean.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'not'
                      > Message:
                         > The operand must be a boolean ('true' or 'false').
                    """;
            assertEquals(expected, actual);
        }
    }
}
