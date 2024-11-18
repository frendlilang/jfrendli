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
    }
}
