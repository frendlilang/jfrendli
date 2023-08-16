package tests.statements.repeat_times;

import tests.FrendliTestExpectError;
import tests.FrendliTestExpectSuccess;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RepeatTimesTest {
    @Nested
    public class RepeatTimesTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanRepeatIntegerTimes() {
            String sourceFile = "statements/repeat_times/repeat-integer-times.frendli";
            String actual = run(sourceFile);
            String expected = """
                    1
                    2
                    3
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanRepeatFloatingPointWithZeroDecimalTimes() {
            String sourceFile = "statements/repeat_times/repeat-floating-point-zero-decimal-times.frendli";
            String actual = run(sourceFile);
            String expected = """
                    1
                    2
                    3
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanRepeatNonNumberExpressionTimes() {
            String sourceFile = "statements/repeat_times/repeat-non-number-expression.frendli";
            String actual = run(sourceFile);
            String expected = """
                    1
                    2
                    3
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanCreateScope() {
            String sourceFile = "statements/repeat_times/create-scope.frendli";
            String actual = run(sourceFile);
            String expected = """
                    local
                    global
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanCreateNewScopeEachIteration() {
            String sourceFile = "statements/repeat_times/create-new-scope-each-iteration.frendli";
            String actual = run(sourceFile);
            String expected = """
                    1
                    1
                    1
                    """.trim();
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class RepeatTimesTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotRepeatZeroTimes() {
            String sourceFile = "statements/repeat_times/error-repeat-zero-times.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'repeat'
                      > Message:
                         > The number must be a positive integer.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotRepeatNegativeTimes() {
            String sourceFile = "statements/repeat_times/error-repeat-negative-times.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'repeat'
                      > Message:
                         > The number must be a positive integer.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotRepeatNonZeroDecimalTimes() {
            String sourceFile = "statements/repeat_times/error-repeat-non-zero-decimal-times.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'repeat'
                      > Message:
                         > The number must be a positive integer.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotRepeatTextTimes() {
            String sourceFile = "statements/repeat_times/error-repeat-text-times.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'repeat'
                      > Message:
                         > The operand must be a number.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotRepeatBooleanTimes() {
            String sourceFile = "statements/repeat_times/error-repeat-boolean-times.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'repeat'
                      > Message:
                         > The operand must be a number.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotRepeatEmptyTimes() {
            String sourceFile = "statements/repeat_times/error-repeat-empty-times.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'repeat'
                      > Message:
                         > The operand must be a number.
                    """;
            assertEquals(expected, actual);
        }
    }
}
