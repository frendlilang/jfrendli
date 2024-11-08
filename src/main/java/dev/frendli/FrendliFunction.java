package dev.frendli;

import java.util.List;

/**
 * A user-defined function (runtime representation).
 */
public class FrendliFunction implements FrendliCallable {
    /**
     * The compile-time (syntax tree node) representation of a function.
     */
    private final Statement.Define declaration;
    /**
     * The environment used when declared.
     */
    private final Environment closure;

    public FrendliFunction(Statement.Define declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public int arity() {
        return declaration.parameters.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        // Each time a function gets called, a new environment is created.
        // This allows for recursion to work. Parameters are encapsulated
        // by the function, and they are bound to the arguments sent in
        // this newly created environment.
        Environment currentEnvironment = new Environment(closure);
        for (int i = 0; i < declaration.parameters.size(); i++) {
            // The number of arguments are verified before this method is
            // called in "visitCallExpression" in the Interpreter.
            Token name = declaration.parameters.get(i);
            Object value = arguments.get(i);
            currentEnvironment.define(name, value);
        }

        // When a return statement is executed, it will throw a Return exception
        // that should be caught by the caller (here), containing the return value.
        try {
            interpreter.executeBlock(declaration.body.statements, currentEnvironment);
        }
        catch (Return returnObject) {
            return returnObject.value;
        }

        // Functions without any return statements will implicitly return "empty".
        return null;
    }

    @Override
    public String toString() {
        return "<definition: " + declaration.name.lexeme + ">";
    }
}
