package tests.scope.block;

import tests.FrendliTestExpectSuccess;
import tests.FrendliTestExpectError;

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
        void itCanShadowParameterViaAnotherParameter() {
            String sourceFile = "scope/block/shadow-parameter-via-parameter.frendli";
            String actual = run(sourceFile);
            String expected = """
                    2
                    1
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

        @Test
        void itCannotAccessGlobalFunctionBeforeDeclaration() {
            String sourceFile = "scope/block/error-access-global-function-before-declaration.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 3 at 'myFunction2'
                      > Message:
                         > 'myFunction2' has not been created or defined. To create it, use 'create', or define it using 'define'.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotEndBlockWithoutDedentation() {
            String sourceFile = "scope/block/error-end-block-without-dedentation.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 3 at the end of the file
                      > Message:
                         > Expected a new line.

                    Error
                      > Where:
                         > Line 3 at the end of the file
                      > Message:
                         > Blocks must be dedented at the end. Add a new line and decrease the indentation level.
                    """;
            assertEquals(expected, actual);
        }
    }
}
