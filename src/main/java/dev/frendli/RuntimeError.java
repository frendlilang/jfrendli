package dev.frendli;

/**
 * Runtime error occurring during evaluation of the syntax tree.
 */
public class RuntimeError extends RuntimeException {
    // Store the token to report to the user where
    // the error occurred.
    public final Token token;

    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
