package tests.expressions.literals;

import tests.FrendliTestExpectSuccess;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LiteralsTest {
    @Nested
    public class LiteralsTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanEvaluateNumber() {
            String sourceFile = "expressions/literals/evaluate-number.frendli";
            String actual = run(sourceFile);
            String expected = """
                    0
                    1
                    12.34
                    1234567.89
                    -1234567.89
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanEvaluateBoolean() {
            String sourceFile = "expressions/literals/evaluate-boolean.frendli";
            String actual = run(sourceFile);
            String expected = """
                    true
                    false
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanEvaluateText() {
            String sourceFile = "expressions/literals/evaluate-text.frendli";
            String actual = run(sourceFile);
            String expected = """
                    abcdefghijklmnopqrstuvwxyz
                    ABCDEFGHIJKLMNOPQRSTUVWXYZ

                    0123456789
                    _ !#%&/=?*^-.:;<>()[]{}|'~
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanEvaluateEmpty() {
            String sourceFile = "expressions/literals/evaluate-empty.frendli";
            String actual = run(sourceFile);
            String expected = "empty";
            assertEquals(expected, actual);
        }
    }
}
