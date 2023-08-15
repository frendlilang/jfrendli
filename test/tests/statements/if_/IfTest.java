package tests.statements.if_;

import tests.FrendliTestExpectError;
import tests.FrendliTestExpectSuccess;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IfTest {
    @Nested
    public class IfTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itEntersIfBranchWhenTrue() {
            String sourceFile = "statements/if_/enter-if-branch-when-true.frendli";
            String actual = run(sourceFile);
            String expected = "in if";
            assertEquals(expected, actual);
        }

        @Test
        void itEntersConsecutiveIfBranchesWhenTrue() {
            String sourceFile = "statements/if_/enter-consecutive-if-branches-when-true.frendli";
            String actual = run(sourceFile);
            String expected = """
                    in if 1
                    in if 2
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itEntersFirstTrueOtherwiseIfBranch() {
            String sourceFile = "statements/if_/enter-first-true-otherwise-if-branch.frendli";
            String actual = run(sourceFile);
            String expected = "in otherwise if 2";
            assertEquals(expected, actual);
        }

        @Test
        void itEntersOtherwiseBranch() {
            String sourceFile = "statements/if_/enter-otherwise-branch.frendli";
            String actual = run(sourceFile);
            String expected = "in otherwise";
            assertEquals(expected, actual);
        }

        @Test
        void itExecutesAllStmtsInTakenBranch() {
            String sourceFile = "statements/if_/execute-all-stmts-in-branch.frendli";
            String actual = run(sourceFile);
            String expected = """
                    in if
                    in if
                    in if
                    """.trim();
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class IfTestExpectError extends FrendliTestExpectError {
        @Test
        void itDisallowsTextCondition() {
            String sourceFile = "statements/if_/error-text-condition.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'if'
                      > Message:
                         > The operand must be a boolean ('true' or 'false').
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itDisallowsNumberCondition() {
            String sourceFile = "statements/if_/error-number-condition.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'if'
                      > Message:
                         > The operand must be a boolean ('true' or 'false').
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itDisallowsEmptyCondition() {
            String sourceFile = "statements/if_/error-empty-condition.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'if'
                      > Message:
                         > The operand must be a boolean ('true' or 'false').
                    """;
            assertEquals(expected, actual);
        }
    }
}
