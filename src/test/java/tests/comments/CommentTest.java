package tests.comments;

import tests.FrendliTestExpectSuccess;
import tests.FrendliTestExpectError;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentTest {
    @Nested
    public class CommentTestExpectSuccess extends FrendliTestExpectSuccess {
        @Test
        void itIgnoresCommentSideEffects() {
            String sourceFile = "comments/ignore-comment-side-effect.frendli";
            String actual = run(sourceFile);
            String expected = "10";
            assertEquals(expected, actual);
        }

        @Test
        void itIgnoresEmptyComment() {
            String sourceFile = "comments/ignore-empty-comment.frendli";
            String actual = run(sourceFile);
            String expected = "";
            assertEquals(expected, actual);
        }

        @Test
        void itIgnoresSameLineComment() {
            String sourceFile = "comments/ignore-same-line-comment.frendli";
            String actual = run(sourceFile);
            String expected = "";
            assertEquals(expected, actual);
        }

        @Test
        void itIgnoresCommentAtEndOfFile() {
            String sourceFile = "comments/ignore-comment-at-eof.frendli";
            String actual = run(sourceFile);
            String expected = "";
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class CommentTestExpectError extends FrendliTestExpectError {
        @Test
        void itCannotUseCommentAsVariable() {
            String sourceFile = "comments/error-use-comment-as-variable.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 4 at 'a'
                      > Message:
                         > 'a' has not been created or defined. To create it, use 'create', or define it using 'define'.
                    """;
            assertEquals(expected, actual);
        }

        @Test
        void itCannotUseCommentAsValue() {
            String sourceFile = "comments/error-use-comment-as-value.frendli";
            String actual = runExpectComptimeError(sourceFile);
            String expected = """
                    Error
                      > Where:
                         > Line 2 at the end of the line
                      > Message:
                         > Cannot find a valid expression.
                    """;
            assertEquals(expected, actual);
        }
    }
}
