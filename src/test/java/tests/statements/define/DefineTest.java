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
        void itCanCreateScopeForFunctionBody() {
            String sourceFile = "statements/define/create-scope.frendli";
            String actual = run(sourceFile);
            String expected = """
                    local
                    global
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanScopeParametersToFunctionBody() {
            String sourceFile = "statements/define/scope-parameter-to-function-body.frendli";
            String actual = run(sourceFile);
            String expected = """
                    In local scope
                    In global scope
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanDefineLocalFunction() {
            String sourceFile = "statements/define/define-local-function.frendli";
            String actual = run(sourceFile);
            String expected = """
                    Local function in if-block
                    Local function in nested function-block
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itDoesNotExecuteBodyWhenDefiningFunction() {
            String sourceFile = "statements/define/define-function-no-body-execution.frendli";
            String actual = run(sourceFile);
            String expected = "";
            assertEquals(expected, actual);
        }

        @Test
        void itCanReturnWithNumber() {
            String sourceFile = "statements/define/return-with-number.frendli";
            String actual = run(sourceFile);
            String expected = "123";
            assertEquals(expected, actual);
        }

        @Test
        void itCanReturnWithText() {
            String sourceFile = "statements/define/return-with-text.frendli";
            String actual = run(sourceFile);
            String expected = "Some text";
            assertEquals(expected, actual);
        }

        @Test
        void itCanReturnWithBoolean() {
            String sourceFile = "statements/define/return-with-boolean.frendli";
            String actual = run(sourceFile);
            String expected = "true";
            assertEquals(expected, actual);
        }

        @Test
        void itCanReturnWithExplicitEmpty() {
            String sourceFile = "statements/define/return-with-explicit-empty.frendli";
            String actual = run(sourceFile);
            String expected = "empty";
            assertEquals(expected, actual);
        }

        @Test
        void itCanReturnWithImplicitEmpty() {
            String sourceFile = "statements/define/return-with-implicit-empty.frendli";
            String actual = run(sourceFile);
            String expected = """
                    empty
                    empty
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanReturnWithNonLiteralExpression() {
            String sourceFile = "statements/define/return-with-non-literal-expression.frendli";
            String actual = run(sourceFile);
            String expected = """
                    102
                    123456
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanReturnFromNestedBlock() {
            String sourceFile = "statements/define/return-from-nested-block.frendli";
            String actual = run(sourceFile);
            String expected = "123";
            assertEquals(expected, actual);
        }

        @Test
        void itCanReturnFromNestedLocalFunction() {
            String sourceFile = "statements/define/return-from-nested-local-function.frendli";
            String actual = run(sourceFile);
            String expected = "123";
            assertEquals(expected, actual);
        }

        @Test
        void itCanUseBuiltInDefinitionName() {
            String sourceFile = "statements/define/use-built-in-definition-name.frendli";
            String actual = run(sourceFile);
            String expected = "<definition: onlyReturn>";
            assertEquals(expected, actual);
        }

        @Test
        void itCanEvaluateArgumentsLeftToRight() {
            String sourceFile = "statements/define/evaluate-arguments-left-to-right.frendli";
            String actual = run(sourceFile);
            String expected = """
                    Argument 1
                    Argument 2
                    Argument 3
                    """.trim();
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

        @Test
        void itCannotCreateVariableWithSameIdentifierAsParameter() {
            String sourceFile = "statements/define/error-create-variable-same-identifier-as-parameter.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 3 at 'a'
                      > Message:
                         > 'a' already exists.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotCallNonCallable() {
            String sourceFile = "statements/define/error-call-non-callable.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 4 at ')'
                      > Message:
                         > You can only call what has previously been defined (with 'define').
                    """;
            assertEquals(expected, actual);
        }
    }
}
