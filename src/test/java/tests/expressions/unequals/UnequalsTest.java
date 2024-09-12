package tests.expressions.unequals;

import tests.FrendliTestExpectSuccess;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnequalsTest {
    @Nested
    public class UnequalsTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itCanCompareEmptyToEmpty() {
            String sourceFile = "expressions/unequals/compare-empty-empty.frendli";
            String actual = run(sourceFile);
            String expected = "false";
            assertEquals(expected, actual);
        }

        @Test
        void itCanCompareEmptyToNonEmpty() {
            String sourceFile = "expressions/unequals/compare-empty-non-empty.frendli";
            String actual = run(sourceFile);
            String expected = """
                    true
                    true
                    true
                    true
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanCompareWholeNumberToFloatingPointNumber() {
            String sourceFile = "expressions/unequals/compare-whole-number-floating-point-number.frendli";
            String actual = run(sourceFile);
            String expected = """
                    false
                    false
                    true
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanCompareBooleanToBoolean() {
            String sourceFile = "expressions/unequals/compare-boolean-boolean.frendli";
            String actual = run(sourceFile);
            String expected = """
                    false
                    false
                    true
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanCompareTextToText() {
            String sourceFile = "expressions/unequals/compare-text-text.frendli";
            String actual = run(sourceFile);
            String expected = """
                    false
                    false
                    true
                    """.trim();
            assertEquals(expected, actual);
        }

        @Test
        void itCanCompareDifferentTypes() {
            String sourceFile = "expressions/unequals/compare-different-types.frendli";
            String actual = run(sourceFile);
            String expected = """
                    true
                    true
                    true
                    """.trim();
            assertEquals(expected, actual);
        }
    }
}
