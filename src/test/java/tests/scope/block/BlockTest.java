package tests.scope.block;

import tests.FrendliTestExpectError;
import tests.FrendliTestExpectSuccess;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BlockTest {
    @Nested
    public class BlockTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanShadowGlobalVariable() {
            String sourceFile = "scope/block/shadow-global-variable.frendli";
            String actual = run(sourceFile);
            String expected = """
                    global
                    local
                    global
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanShadowLocalVariable() {
            String sourceFile = "scope/block/shadow-local-variable.frendli";
            String actual = run(sourceFile);
            String expected = """
                    local1
                    local2
                    local1
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanAccessLocalVariableInChildBlock() {
            String sourceFile = "scope/block/access-local-variable-in-child-block.frendli";
            String actual = run(sourceFile);
            String expected = """
                    parent
                    parent
                    parent
                    """.trim();
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class BlockTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotAccessVariableInDifferentScope() {
            String sourceFile = "scope/block/error-access-variable-in-different-scope.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 5 at 'a'
                      > Message:
                         > 'a' has not been created or defined. To create it, use 'create', or define it using 'define'.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotAccessVariableInSameBlockDifferentScope() {
            String sourceFile = "scope/block/error-access-variable-in-same-block-different-scope.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 3 at 'a'
                      > Message:
                         > 'a' has not been created or defined. To create it, use 'create', or define it using 'define'.
                    """;
            assertEquals(expected, actual);
        }
    }
}
