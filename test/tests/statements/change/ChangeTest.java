package tests.statements.change;

import tests.FrendliTestExpectError;
import tests.FrendliTestExpectSuccess;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChangeTest {
    @Nested
    public class ChangeTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanChangeGlobalVariableNumberToNumber() {
            String sourceFile = "statements/change/change-global-variable-number-number.frendli";
            String actual = run(sourceFile);
            String expected = """
                    12
                    34
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanChangeGlobalVariableNumberToText() {
            String sourceFile = "statements/change/change-global-variable-number-text.frendli";
            String actual = run(sourceFile);
            String expected = """
                    12
                    one
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanChangeGlobalVariableNumberToBoolean() {
            String sourceFile = "statements/change/change-global-variable-number-boolean.frendli";
            String actual = run(sourceFile);
            String expected = """
                    12
                    true
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanChangeGlobalVariableNumberToEmpty() {
            String sourceFile = "statements/change/change-global-variable-number-empty.frendli";
            String actual = run(sourceFile);
            String expected = """
                    12
                    empty
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanChangeGlobalVariableEmptyToText() {
            String sourceFile = "statements/change/change-global-variable-empty-text.frendli";
            String actual = run(sourceFile);
            String expected = """
                    empty
                    one
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanChangeLocalVariableNumberToText() {
            String sourceFile = "statements/change/change-local-variable-number-text.frendli";
            String actual = run(sourceFile);
            String expected = """
                    12
                    one
                    """.trim();
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class ChangeTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotChangeUndeclaredGlobalVariable() {
            String sourceFile = "statements/change/error-change-undeclared-global-variable.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'a'
                      > Message:
                         > 'a' has not been created or defined. To create it, use 'create', or define it using 'define'.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseGroupResultAsTarget() {
            String sourceFile = "statements/change/error-use-group-result-target.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 3 at '='
                      > Message:
                         > Values cannot be assigned to that target.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseInfixResultAsTarget() {
            String sourceFile = "statements/change/error-use-infix-result-target.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 5 at '='
                      > Message:
                         > Values cannot be assigned to that target.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUsePrefixResultAsTarget() {
            String sourceFile = "statements/change/error-use-prefix-result-target.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 3 at '='
                      > Message:
                         > Values cannot be assigned to that target.
                    """;
            assertEquals(expected, actual);
        }
    }
}
