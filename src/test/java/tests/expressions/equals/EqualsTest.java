package tests.expressions.equals;

import tests.FrendliTestExpectSuccess;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EqualsTest {
    @Nested
    public class EqualsTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanCompareEmptyToEmpty() {
            String sourceFile = "expressions/equals/compare-empty-empty.frendli";
            String actual = run(sourceFile);
            String expected = "true";
            assertEquals(expected, actual);
        }

        @Test
        void itCanCompareEmptyToNonEmpty() {
            String sourceFile = "expressions/equals/compare-empty-non-empty.frendli";
            String actual = run(sourceFile);
            String expected = """
                    false
                    false
                    false
                    false
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanCompareWholeNumberToFloatingPointNumber() {
            String sourceFile = "expressions/equals/compare-whole-number-floating-point-number.frendli";
            String actual = run(sourceFile);
            String expected = """
                    true
                    true
                    false
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanCompareBooleanToBoolean() {
            String sourceFile = "expressions/equals/compare-boolean-boolean.frendli";
            String actual = run(sourceFile);
            String expected = """
                    true
                    true
                    false
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanCompareTextToText() {
            String sourceFile = "expressions/equals/compare-text-text.frendli";
            String actual = run(sourceFile);
            String expected = """
                    true
                    true
                    false
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanCompareDifferentTypes() {
            String sourceFile = "expressions/equals/compare-different-types.frendli";
            String actual = run(sourceFile);
            String expected = """
                    false
                    false
                    false
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itIsReflexive() {
            String sourceFile = "expressions/equals/compare-reflexive.frendli";
            String actual = run(sourceFile);
            String expected = """
                    true
                    true
                    true
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itIsSymmetric() {
            String sourceFile = "expressions/equals/compare-symmetric.frendli";
            String actual = run(sourceFile);
            String expected = """
                    true
                    true
                    true
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itIsTransitive() {
            String sourceFile = "expressions/equals/compare-transitive.frendli";
            String actual = run(sourceFile);
            String expected = "true";
            assertEquals(expected, actual);
        }
    }
}
