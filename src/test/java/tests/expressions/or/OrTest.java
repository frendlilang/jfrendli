package tests.expressions.or;

import tests.FrendliTestExpectSuccess;
import tests.FrendliTestExpectError;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrTest {
    @Nested
    public class OrTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itIsFalseIfAllAreFalse() {
            String sourceFile = "expressions/or/all-false.frendli";
            String actual = run(sourceFile);
            String expected = """
                    false
                    false
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itIsTrueIfOneIsTrue() {
            String sourceFile = "expressions/or/one-true.frendli";
            String actual = run(sourceFile);
            String expected = """
                    true
                    true
                    true
                    true
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itEvaluatesAllIfAllAreFalse() {
            String sourceFile = "expressions/or/operand-updates-state.frendli";
            String actual = run(sourceFile);
            String expected = """
                    updatedA
                    updatedB
                    updatedC
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itJumpsExecutionAfterFirstTrue() {
            String sourceFile = "expressions/or/operand-updates-state-until-true.frendli";
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
    public class OrTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotAcceptNonBooleans() {
            String sourceFile = "expressions/or/error-non-boolean-operand.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'or'
                      > Message:
                         > The operand must be a boolean ('true' or 'false').
                    """;
            assertEquals(expected, actual);
        }
    }
}
