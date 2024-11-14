package tests.statements.define;

import tests.FrendliTestExpectSuccess;
import tests.FrendliTestExpectError;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefineTest {
    @Nested
    public class DefineTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanDefineFunctionWithSingleStatement() {
            String sourceFile = "statements/define/define-function-with-single-statement.frendli";
            String actual = run(sourceFile);
            String expected = "In statement 1";
            assertEquals(expected, actual);
        }

        @Test
        void itCanDefineFunctionWithMultipleStatements() {
            String sourceFile = "statements/define/define-function-with-multiple-statements.frendli";
            String actual = run(sourceFile);
            String expected = """
                    In statement 1
                    In statement 2
                    In statement 3
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanAcceptAllDataTypes() {
            String sourceFile = "statements/define/define-function-accept-all-data-types.frendli";
            String actual = run(sourceFile);
            String expected = """
                    123
                    Some text
                    false
                    empty
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanDefineFunctionWithSingleParameter() {
            String sourceFile = "statements/define/define-function-with-single-parameter.frendli";
            String actual = run(sourceFile);
            String expected = "Argument 1";
            assertEquals(expected, actual);
        }

        @Test
        void itCanDefineFunctionWithMultipleParameters() {
            String sourceFile = "statements/define/define-function-with-multiple-parameters.frendli";
            String actual = run(sourceFile);
            String expected = """
                    Argument 1
                    Argument 2
                    Argument 3
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itScopesParametersToFunctionBody() {
            String sourceFile = "statements/define/scope-parameter-to-function-body.frendli";
            String actual = run(sourceFile);
            String expected = """
                    In inner scope
                    In outer scope
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itUsesBuiltInDefinitionName() {
            String sourceFile = "statements/define/use-built-in-definition-name.frendli";
            String actual = run(sourceFile);
            String expected = "<definition: onlyReturn>";
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class DefineTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotCallFunctionWithTooFewArguments() {
            String sourceFile = "statements/define/error-call-function-with-too-few-arguments.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 7 at ')'
                      > Message:
                         > The number of arguments sent must be 3 but got 1.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotCallFunctionWithTooManyArguments() {
            String sourceFile = "statements/define/error-call-function-with-too-many-arguments.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 7 at ')'
                      > Message:
                         > The number of arguments sent must be 3 but got 5.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotAccessParameterVariableOutsideFunction() {
            String sourceFile = "statements/define/error-access-parameter-variable-outside-function.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 7 at 'first'
                      > Message:
                         > 'first' has not been created or defined. To create it, use 'create', or define it using 'define'.
                    """;
            assertEquals(expected, actual);
        }
    }
}
