package tests.statements.repeat_while;

import tests.FrendliTestExpectError;
import tests.FrendliTestExpectSuccess;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RepeatWhileTest {
    @Nested
    public class RepeatWhileTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanRepeatWhileTrue() {
            String sourceFile = "statements/repeat_while/repeat-while-true.frendli";
            String actual = run(sourceFile);
            String expected = """
                    1
                    2
                    3
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itDoesNotEnterBranchWhenFalse() {
            String sourceFile = "statements/repeat_while/does-not-enter-branch-when-false.frendli";
            String actual = run(sourceFile);
            String expected = "done";
            assertEquals(expected, actual);
        }

        @Test
        void itCanCreateScope() {
            String sourceFile = "statements/repeat_while/create-scope.frendli";
            String actual = run(sourceFile);
            String expected = """
                    local
                    global
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanCreateNewScopeEachIteration() {
            String sourceFile = "statements/repeat_while/create-new-scope-each-iteration.frendli";
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
    public class RepeatWhileTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotRepeatWhileNumber() {
            String sourceFile = "statements/repeat_while/error-repeat-while-number.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'while'
                      > Message:
                         > The operand must be a boolean ('true' or 'false').
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotRepeatWhileText() {
            String sourceFile = "statements/repeat_while/error-repeat-while-text.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'while'
                      > Message:
                         > The operand must be a boolean ('true' or 'false').
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotRepeatWhileEmpty() {
            String sourceFile = "statements/repeat_while/error-repeat-while-empty.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'while'
                      > Message:
                         > The operand must be a boolean ('true' or 'false').
                    """;
            assertEquals(expected, actual);
        }
    }
}
