package tests.expressions.concatenate;

import tests.FrendliTestExpectSuccess;
import tests.FrendliTestExpectError;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConcatenateTest {
    @Nested
    public class ConcatenateTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanConcatenateTexts() {
            String sourceFile = "expressions/concatenate/concatenate-texts.frendli";
            String actual = run(sourceFile);
            String expected = """
                    one
                    onetwo
                    onetwothree
                    """.trim();
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class ConcatenateTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotConcatenateTextAndNumber() {
            String sourceFile = "expressions/concatenate/error-concatenate-text-number.frendli";
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
