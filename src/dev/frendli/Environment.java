package dev.frendli;

import java.util.HashMap;
import java.util.Map;

/**
 * The environment - stores all variable bindings and keeps track
 * of inner and outer scopes as blocks are entered and exited.
 */
public class Environment {
    private final Map<String, Object> values = new HashMap<>(); // Maps variable names to values

    /**
     * Look up a variable.
     *
     * @param token The variable token.
     * @return The bound value.
     */
    public Object get(Token token) {
        if (values.containsKey(token.lexeme)) {
            return values.get(token.lexeme);
        }

        throw new RuntimeError(token, "'" + token.lexeme + "' has not been created. First create it and set it to a value.");
    }

    /**
     * Define a variable by binding its name to a value.
     *
     * @param token The variable token.
     * @param value The value.
     */
    public void define(Token token, Object value) {
        // Redefining a variable in the same scope is not allowed
        // (e.g. two "create" statements with the same variable name)
        if (values.containsKey(token.lexeme)) {
            throw new RuntimeError(token, "'" + token.lexeme + "' has already been created.");
        }

        values.put(token.lexeme, value);
    }

    /**
     * Assign a value to an already-existing variable.
     *
     * @param token The variable token.
     * @param value The value.
     */
    public void assign(Token token, Object value) {
        if (!values.containsKey(token.lexeme)) {
            throw new RuntimeError(token, "'" + token.lexeme + "' has not been created. First create it and set it to a value.");
        }

        values.put(token.lexeme, value);
    }
}
