package tests.scope.closure;

import tests.FrendliTestExpectSuccess;
import tests.FrendliTestExpectError;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClosureTest {
    @Nested
    public class ClosureTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanCloseOverLocalVariable() {
            String sourceFile = "scope/closure/close-over-local-variable.frendli";
            String actual = run(sourceFile);
            String expected = "local";
            assertEquals(expected, actual);
        }

        @Test
        void itCanCloseOverAndUpdateLocalVariable() {
            String sourceFile = "scope/closure/close-over-and-update-local-variable.frendli";
            String actual = run(sourceFile);
            String expected = """
                    1
                    2
                    3
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanCloseOverAndUpdateGlobalVariable() {
            String sourceFile = "scope/closure/close-over-and-update-global-variable.frendli";
            String actual = run(sourceFile);
            String expected = """
                    1
                    2
                    3
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanCloseOverParameter() {
            String sourceFile = "scope/closure/close-over-parameter.frendli";
            String actual = run(sourceFile);
            String expected = "argument";
            assertEquals(expected, actual);
        }

        @Test
        void itCanCloseOverNestedVariables() {
            String sourceFile = "scope/closure/close-over-nested-variables.frendli";
            String actual = run(sourceFile);
            String expected = """
                    globalA
                    localB
                    localC
                    localD
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanResolveToSameVariableInStaticLexicalScope() {
            String sourceFile = "scope/closure/resolve-to-same-variable-in-static-lexical-scope.frendli";
            String actual = run(sourceFile);
            String expected = """
                    global
                    global
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanChangeGlobalVariableToClosure() {
            String sourceFile = "scope/closure/change-global-variable-to-closure.frendli";
            String actual = run(sourceFile);
            String expected = """
                    empty
                    local
                    """.trim();
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class ClosureTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotAccessClosedOverVariableInDifferentScope() {
            String sourceFile = "scope/closure/error-access-closed-over-variable-in-different-scope.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 11 at 'a'
                      > Message:
                         > 'a' has not been created or defined. To create it, use 'create', or define it using 'define'.
                    """;
            assertEquals(expected, actual);
        }
    }
}
