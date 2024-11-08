package dev.frendli;

import java.util.HashMap;
import java.util.Map;

/**
 * The environment - stores all variable bindings and keeps track
 * of inner and outer scopes as blocks are entered and exited.
 */
public class Environment {
    /**
     * The enclosing environment.
     * (Used for looking up variables in outer scopes.)
     */
    public final Environment enclosing;
    /**
     * Variable bindings (maps variable names to values) in this environment.
     */
    private final Map<String, Object> values = new HashMap<>();

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
     * @param name The variable name.
     * @return The bound value.
     */
    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        // Look in outer scopes. This calls the get method in the outer/enclosing
        // environment, causing recursive lookup up the scope chain.
        if (enclosing != null) {
            return enclosing.get(name);
        }

        throw new RuntimeError(name, "'" + name.lexeme + "' has not been created or defined. To create it, use 'create', or define it using 'define'.");
    }

    /**
     * Look up a variable in an environment at a certain
     * distance from the current one.
     *
     * @param distance The distance from the current environment.
     * @param name The variable name.
     * @return The bound value.
     */
    public Object getAt(int distance, Token name) {
        // This code assumes the resolver has correctly resolved the variables.
        return getEnclosingEnvironment(distance).values.get(name.lexeme);
    }

    /**
     * Define a variable by binding its name to a value.
     *
     * @param name The variable name.
     * @param value The value.
     */
    public void define(Token name, Object value) {
        // Redefining a variable in the same scope is not allowed
        // (e.g. two "create" or "define" statements with the same variable name)
        if (values.containsKey(name.lexeme)) {
            String message = "'" + name.lexeme + "' has already been created. If you meant to change it, use 'change'.";
            if (values.get(name.lexeme) instanceof FrendliFunction) {
                message = "'" + name.lexeme + "' has already been defined.";
            }

            throw new RuntimeError(name, message);
        }

        values.put(name.lexeme, value);
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
     * @param name The variable name.
     * @param value The value.
     */
    public void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        // Look in outer scopes. This calls the assign method in the outer/enclosing
        // environment, causing recursive lookup up the scope chain.
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "'" + name.lexeme + "' has not yet been created. To create it, use 'create'. Only then may you later change it using 'change'.");
    }

    /**
     * Assign a value to an already-existing variable in an
     * environment at a certain distance from the current one.
     *
     * @param distance The distance from the current environment.
     * @param name The variable name.
     * @param value The value.
     */
    public void assignAt(int distance, Token name, Object value) {
        Environment environment = getEnclosingEnvironment(distance);
        environment.values.put(name.lexeme, value);
    }

    /**
     * Get an enclosing environment a certain distance
     * from the current one.
     *
     * @param distance The distance from the current environment.
     * @return The environment at the given distance.
     */
    private Environment getEnclosingEnvironment(int distance) {
        // This code assumes the resolver has correctly resolved the variables.
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }

        return environment;
    }
}
