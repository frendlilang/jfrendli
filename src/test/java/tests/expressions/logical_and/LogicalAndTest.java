package tests.expressions.logical_and;

import tests.FrendliTestExpectSuccess;
import tests.FrendliTestExpectError;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogicalAndTest {
    @Nested
    public class LogicalAndTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itIsTrueIfAllAreTrue() {
            String sourceFile = "expressions/logical_and/all-true.frendli";
            String actual = run(sourceFile);
            String expected = """
                    true
                    true
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itIsFalseIfOneIsFalse() {
            String sourceFile = "expressions/logical_and/one-false.frendli";
            String actual = run(sourceFile);
            String expected = """
                    false
                    false
                    false
                    false
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itEvaluatesAllIfAllAreTrue() {
            String sourceFile = "expressions/logical_and/operand-updates-state.frendli";
            String actual = run(sourceFile);
            String expected = """
                    updatedA
                    updatedB
                    updatedC
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itJumpsExecutionAfterFirstFalse() {
            String sourceFile = "expressions/logical_and/operand-updates-state-until-false.frendli";
            String actual = run(sourceFile);
            String expected = """
                    updatedA
                    originalB
                    originalA
                    originalB
                    originalC
                    updatedA
                    updatedB
                    originalC
                    """.trim();
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class LogicalAndTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotAcceptNonBooleans() {
            String sourceFile = "expressions/logical_and/error-non-boolean-operand.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'and'
                      > Message:
                         > The operand must be a boolean ('true' or 'false').
                    """;
            assertEquals(expected, actual);
        }
    }
}
