package dev.frendli;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * The resolver - recursively traverses the syntax tree produced
 * by the parser and performs a static semantic analysis by resolving
 * all variables found; i.e., determining which declaration a variable
 * name refers to. Frendli's use of lexical scope allows for this type
 * of static resolution, rather than resolving the variables dynamically
 * on each evaluation in the interpreter. Unlike the interpreter, the
 * resolver visits all nodes exactly 1 time (O(n)) and does not execute
 * any statements.
 */
public class Resolver implements Expression.Visitor<Void>, Statement.Visitor<Void> {
    private final ErrorReporter reporter;
    private final Interpreter interpreter;
    private final Stack<Set<String>> scopes = new Stack<>();   // Stack of local block scopes containing the variable names

    public Resolver(Interpreter interpreter, ErrorReporter reporter) {
        this.interpreter = interpreter;
        this.reporter = reporter;
    }

    @Override
    public Void visitBlockStatement(Statement.Block statement) {
        createScope();
        resolveBlock(statement.statements);
        discardScope();

        return null;
    }

    @Override
    public Void visitCreateStatement(Statement.Create statement) {
        declare(statement.name);
        verifyNotAccessingItselfInInitializer(statement.name, statement.initializer);
        resolve(statement.initializer);

        return null;
    }

    @Override
    public Void visitVariableExpression(Expression.Variable expression) {
        resolveLocalVariable(expression.name, expression);

        return null;
    }

    /**
     * Resolve a block.
     *
     * @param statements The statements to resolve.
     */
    private void resolveBlock(List<Statement> statements) {
        for (Statement statement : statements) {
            resolve(statement);
        }
    }

    /**
     * Resolve a statement.
     *
     * @param statement The statement to resolve.
     */
    private void resolve(Statement statement) {
        statement.accept(this);
    }

    /**
     * Resolve an expression.
     *
     * @param expression The expression to resolve.
     */
    private void resolve(Expression expression) {
        expression.accept(this);
    }

    /**
     * Resolve a local variable.
     *
     * @param name The variable token.
     * @param expression The associated expression.
     */
    private void resolveLocalVariable(Token name, Expression expression) {
        // Search for the name starting from the innermost scope.
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).contains(name.lexeme)) {
                // Calculate the number of hops from the innermost scope
                // to the closest scope where the name was defined.
                int depth = scopes.size() - 1 - i;

                // Add the result to the interpreter so that it can directly
                // look up the environment in which the variable exists.
                interpreter.resolve(expression, depth);
                return;
            }
        }

        // If this is reached, the variable is assumed to be global.
        // (The interpreter will throw a runtime error if it is not.)
    }

    /**
     * Create a new scope.
     */
    private void createScope() {
        scopes.push(new HashSet<>());
    }

    /**
     * Discard the innermost scope.
     */
    private void discardScope() {
        scopes.pop();
    }

    /**
     * Declare a name in the innermost scope.
     *
     * @param name The token to be declared.
     */
    private void declare(Token name) {
        if (scopes.isEmpty()) {
            return;
        }

        scopes.peek().add(name.lexeme);
    }

    /**
     * Verify that the variable being declared is not also being
     * accessed in its initializer.
     *
     * @param declared The declared variable.
     * @param initialized The variable used in the initializer.
     */
    private void verifyNotAccessingItselfInInitializer(Token declared, Expression initialized) {
        if (!(initialized instanceof Expression.Variable))
            return;

        // Accessing a variable that has previously been declared in
        // an outer scope with the same name as the one currently being
        // declared is not allowed in its own initializer. E.g:
        // create x = 1
        // if x > 0
        //      create x = x        <-- illegal access on right-hand side
        Token initializerName = ((Expression.Variable)initialized).name;
        if (initializerName.lexeme.equals(declared.lexeme)) {
            error(initializerName, "You cannot use '" + initializerName.lexeme + "' on both sides of '=' when creating it.");
        }
    }

    /**
     * Report an error.
     *
     * @param token The token that caused the error.
     * @param message The error message.
     */
    private void error(Token token, String message) {
        reporter.compileTimeError(token, message);
    }
}
