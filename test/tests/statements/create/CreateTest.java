package tests.statements.create;

import tests.FrendliTestExpectError;
import tests.FrendliTestExpectSuccess;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateTest {
    @Nested
    public class CreateTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanCreateGlobalVariableAsNumber() {
            String sourceFile = "statements/create/create-global-variable-number.frendli";
            String actual = run(sourceFile);
            String expected = "12";
            assertEquals(expected, actual);
        }

        @Test
        void itCanCreateGlobalVariableAsText() {
            String sourceFile = "statements/create/create-global-variable-text.frendli";
            String actual = run(sourceFile);
            String expected = "one";
            assertEquals(expected, actual);
        }

        @Test
        void itCanCreateGlobalVariableAsBoolean() {
            String sourceFile = "statements/create/create-global-variable-boolean.frendli";
            String actual = run(sourceFile);
            String expected = "true";
            assertEquals(expected, actual);
        }

        @Test
        void itCanCreateGlobalVariableAsEmpty() {
            String sourceFile = "statements/create/create-global-variable-empty.frendli";
            String actual = run(sourceFile);
            String expected = "empty";
            assertEquals(expected, actual);
        }

        @Test
        void itCanCreateLocalVariableAsNumber() {
            String sourceFile = "statements/create/create-local-variable-number.frendli";
            String actual = run(sourceFile);
            String expected = "12";
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class CreateTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotCreateGlobalVariableWithNoInitValue() {
            String sourceFile = "statements/create/error-create-global-variable-no-value.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at the end of the line
                      > Message:
                         > 'a' must be initialized using '='. You may set it to 'empty' if needed.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotCreateVariableWithNumberIdentifier() {
            String sourceFile = "statements/create/error-create-variable-number-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '1'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotCreateVariableWithBooleanIdentifier() {
            String sourceFile = "statements/create/error-create-variable-boolean-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'true'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotCreateVariableWithEmptyIdentifier() {
            String sourceFile = "statements/create/error-create-variable-empty-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'empty'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }
    }
}
