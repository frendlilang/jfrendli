package dev.frendli;

/**
 * Exception to be thrown upon visiting return statements.
 * It is used for control flow (returning) and NOT error handling,
 * by having the caller catch the exception.
 */
public class Return extends RuntimeException {
    public final Object value;

    public Return(Object value) {
        // Disable unnecessary parts of the RuntimeException
        // as this class is not used for error handling.
        super(null, null, false, false);
        this.value = value;
    }
}
