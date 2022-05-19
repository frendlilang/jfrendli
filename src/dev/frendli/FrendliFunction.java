package dev.frendli;

import java.util.List;

/**
 * A user-defined function (runtime representation).
 */
public class FrendliFunction implements FrendliCallable {
    private final Statement.Define declaration;

    public FrendliFunction(Statement.Define declaration) {
        this.declaration = declaration;
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
        Environment currentEnvironment = new Environment(interpreter.globalEnvironment);
        for (int i = 0; i < declaration.parameters.size(); i++) {
            // The number of arguments are verified before this method is
            // called in "visitCallExpression" in the Interpreter.
            Token name = declaration.parameters.get(i);
            Object value = arguments.get(i);
            currentEnvironment.define(name, value);
        }
        interpreter.executeBlock(declaration.body.statements, currentEnvironment);

        return null;
    }

    @Override
    public String toString() {
        return "<definition: " + declaration.name.lexeme + ">";
    }
}
