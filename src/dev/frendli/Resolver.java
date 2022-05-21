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
    private final Interpreter interpreter;
    private final Stack<Set<String>> scopes = new Stack<>();   // Stack of local block scopes containing the variable names

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    @Override
    public Void visitBlockStatement(Statement.Block statement) {
        createScope();
        resolve(statement.statements);
        discardScope();

        return null;
    }

    /**
     * Resolve a list of statements.
     *
     * @param statements The statements to resolve.
     */
    private void resolve(List<Statement> statements) {
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
}
