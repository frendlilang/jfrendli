package dev.frendli;

import java.util.HashMap;
import java.util.Map;

/**
 * The environment - stores all variable bindings and keeps track
 * of inner and outer scopes as blocks are entered and exited.
 */
public class Environment {
    // Each environment stores a reference to its enclosing environment
    // in order to look up variables in outer scopes.
    public final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>(); // Maps variable names to values

    // Global scope
    public Environment() {
        enclosing = null;
    }
    // Local scope
    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

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

        // Look in outer scopes. This calls the get method in the outer/enclosing
        // environment, causing recursive lookup up the scope chain.
        if (enclosing != null) {
            return enclosing.get(token);
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
     * Define a native binding (for standard library).
     *
     * @param name The native name.
     * @param value The value.
     */
    public void defineNative(String name, Object value) {
        // Native definitions should occur before anything else and
        // are not checked if their names already exist.
        values.put(name, value);
    }

    /**
     * Assign a value to an already-existing variable.
     *
     * @param token The variable token.
     * @param value The value.
     */
    public void assign(Token token, Object value) {
        if (values.containsKey(token.lexeme)) {
            values.put(token.lexeme, value);
            return;
        }

        // Look in outer scopes. This calls the assign method in the outer/enclosing
        // environment, causing recursive lookup up the scope chain.
        if (enclosing != null) {
            enclosing.assign(token, value);
            return;
        }

        throw new RuntimeError(token, "'" + token.lexeme + "' has not been created. First create it and set it to a value.");
    }
}
