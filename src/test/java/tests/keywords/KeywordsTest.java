package tests.keywords;

import tests.FrendliTestExpectError;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeywordsTest {
    @Nested
    public class KeywordsTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotUseAcceptAsIdentifier() {
            String sourceFile = "keywords/error-accept-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'accept'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseAndAsIdentifier() {
            String sourceFile = "keywords/error-and-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'and'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseChangeAsIdentifier() {
            String sourceFile = "keywords/error-change-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'change'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseCreateAsIdentifier() {
            String sourceFile = "keywords/error-create-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'create'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseDefineAsIdentifier() {
            String sourceFile = "keywords/error-define-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'define'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseDescribeAsIdentifier() {
            String sourceFile = "keywords/error-describe-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'describe'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseEmptyAsIdentifier() {
            String sourceFile = "keywords/error-empty-as-identifier.frendli";
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

        @Test
        void itCannotUseEqualsAsIdentifier() {
            String sourceFile = "keywords/error-equals-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'equals'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseFalseAsIdentifier() {
            String sourceFile = "keywords/error-false-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'false'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseHasAsIdentifier() {
            String sourceFile = "keywords/error-has-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'has'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseIfAsIdentifier() {
            String sourceFile = "keywords/error-if-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'if'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseInheritAsIdentifier() {
            String sourceFile = "keywords/error-inherit-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'inherit'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseMeAsIdentifier() {
            String sourceFile = "keywords/error-me-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'me'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseNotAsIdentifier() {
            String sourceFile = "keywords/error-not-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'not'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseOrAsIdentifier() {
            String sourceFile = "keywords/error-or-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'or'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseOtherwiseAsIdentifier() {
            String sourceFile = "keywords/error-otherwise-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'otherwise'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseParentAsIdentifier() {
            String sourceFile = "keywords/error-parent-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'parent'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseRepeatAsIdentifier() {
            String sourceFile = "keywords/error-repeat-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'repeat'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseReturnAsIdentifier() {
            String sourceFile = "keywords/error-return-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'return'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseSendAsIdentifier() {
            String sourceFile = "keywords/error-send-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'send'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseTimesAsIdentifier() {
            String sourceFile = "keywords/error-times-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'times'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseTrueAsIdentifier() {
            String sourceFile = "keywords/error-true-as-identifier.frendli";
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
        void itCannotUseUnequalsAsIdentifier() {
            String sourceFile = "keywords/error-unequals-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'unequals'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseWhileAsIdentifier() {
            String sourceFile = "keywords/error-while-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'while'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseWithAsIdentifier() {
            String sourceFile = "keywords/error-with-as-identifier.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at 'with'
                      > Message:
                         > A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.
                    """;
            assertEquals(expected, actual);
        }
    }
}
