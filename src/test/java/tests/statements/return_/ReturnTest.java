package tests.statements.return_;

import tests.FrendliTestExpectSuccess;
import tests.FrendliTestExpectError;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReturnTest {
    @Nested
    public class ReturnTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanReturnWithNumber() {
            String sourceFile = "statements/return_/return-with-number.frendli";
            String actual = run(sourceFile);
            String expected = "123";
            assertEquals(expected, actual);
        }

        @Test
        void itCanReturnWithText() {
            String sourceFile = "statements/return_/return-with-text.frendli";
            String actual = run(sourceFile);
            String expected = "Some text";
            assertEquals(expected, actual);
        }

        @Test
        void itCanReturnWithBoolean() {
            String sourceFile = "statements/return_/return-with-boolean.frendli";
            String actual = run(sourceFile);
            String expected = "true";
            assertEquals(expected, actual);
        }

        @Test
        void itCanReturnWithExplicitEmpty() {
            String sourceFile = "statements/return_/return-with-explicit-empty.frendli";
            String actual = run(sourceFile);
            String expected = "empty";
            assertEquals(expected, actual);
        }

        @Test
        void itCanReturnWithImplicitEmpty() {
            String sourceFile = "statements/return_/return-with-implicit-empty.frendli";
            String actual = run(sourceFile);
            String expected = """
                    empty
                    empty
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanReturnWithNonLiteralExpression() {
            String sourceFile = "statements/return_/return-with-non-literal-expression.frendli";
            String actual = run(sourceFile);
            String expected = """
                    102
                    123456
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanReturnFromNestedBlock() {
            String sourceFile = "statements/return_/return-from-nested-block.frendli";
            String actual = run(sourceFile);
            String expected = "123";
            assertEquals(expected, actual);
        }

        @Test
        void itCanReturnFromNestedLocalFunction() {
            String sourceFile = "statements/return_/return-from-nested-local-function.frendli";
            String actual = run(sourceFile);
            String expected = "123";
            assertEquals(expected, actual);
        }

        @Test
        void itDoesNotExecuteStatementsAfterReturn() {
            String sourceFile = "statements/return_/does-not-execute-statements-after-return.frendli";
            String actual = run(sourceFile);
            String expected = "";
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class ReturnTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotReturnFromTopLevel() {
            String sourceFile = "statements/return_/error-return-from-top-level.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'return'
                      > Message:
                         > You can only return from within a definition.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotReturnFromLocalNonFunction() {
            String sourceFile = "statements/return_/error-return-from-local-non-function.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 3 at 'return'
                      > Message:
                         > You can only return from within a definition.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotReturnValueWhenNotUsingWithKeyword() {
            String sourceFile = "statements/return_/error-return-value-not-using-with-keyword.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 3 at '123'
                      > Message:
                         > You must add a new line after 'return'. To return with a value, use 'return with' instead.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotReturnNoValueWhenUsingWithKeyword() {
            String sourceFile = "statements/return_/error-return-no-value-using-with-keyword.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 3 at the end of the line
                      > Message:
                         > You must add a valid expression after 'with' to return with that value. To not return an explicit value, use only 'return'.
                    """;
            assertEquals(expected, actual);
        }
    }
}
