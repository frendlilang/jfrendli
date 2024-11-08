package dev.frendli;

import java.util.List;

/**
 * Interface for any callable object.
 */
public interface FrendliCallable {
    /**
     * Get the number of declared parameters.
     *
     * @return number of declared parameters.
     */
    int arity();

    /**
     * Call the callee.
     *
     * @param interpreter The interpreter.
     * @param arguments The arguments to send.
     * @return An object.
     */
    Object call(Interpreter interpreter, List<Object> arguments);
}
