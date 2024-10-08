package tests.expressions.add;

import tests.FrendliTestExpectError;
import tests.FrendliTestExpectSuccess;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddTest {
    @Nested
    public class AddTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanAddWholeNumbers() {
            String sourceFile = "expressions/add/add-whole-numbers.frendli";
            String actual = run(sourceFile);
            String expected = "46";
            assertEquals(expected, actual);
        }

        @Test
        void itCanAddFloatingPointNumbers() {
            String sourceFile = "expressions/add/add-floating-point-numbers.frendli";
            String actual = run(sourceFile);
            String expected = """
                    69.12
                    34.68
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanAddNegativeFloatingPointNumbers() {
            String sourceFile = "expressions/add/add-negative-floating-point-numbers.frendli";
            String actual = run(sourceFile);
            String expected = """
                    44.44
                    -34.68
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanAddWholeNumberAndFloatingPointNumber() {
            String sourceFile = "expressions/add/add-whole-number-floating-point-number.frendli";
            String actual = run(sourceFile);
            String expected = """
                    46.56
                    12.34
                    """.trim();
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class AddTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotAddNumberAndText() {
            String sourceFile = "expressions/add/error-add-number-text.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '+'
                      > Message:
                         > The operands must be only numbers or only texts.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotAddNumberAndBoolean() {
            String sourceFile = "expressions/add/error-add-number-boolean.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '+'
                      > Message:
                         > The operands must be only numbers or only texts.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotAddNumberAndEmpty() {
            String sourceFile = "expressions/add/error-add-number-empty.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '+'
                      > Message:
                         > The operands must be only numbers or only texts.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotAddEmptyAndEmpty() {
            String sourceFile = "expressions/add/error-add-empty-empty.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '+'
                      > Message:
                         > The operands must be only numbers or only texts.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotAddEmptyAndBoolean() {
            String sourceFile = "expressions/add/error-add-empty-boolean.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '+'
                      > Message:
                         > The operands must be only numbers or only texts.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotAddEmptyAndText() {
            String sourceFile = "expressions/add/error-add-empty-text.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '+'
                      > Message:
                         > The operands must be only numbers or only texts.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotAddTextAndBoolean() {
            String sourceFile = "expressions/add/error-add-text-boolean.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '+'
                      > Message:
                         > The operands must be only numbers or only texts.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotAddBooleanAndBoolean() {
            String sourceFile = "expressions/add/error-add-boolean-boolean.frendli";
            String actual = runExpectRuntimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at '+'
                      > Message:
                         > The operands must be only numbers or only texts.
                    """;
            assertEquals(expected, actual);
        }
    }
}
