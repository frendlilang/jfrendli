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
public class Resolver implements ExpressionVisitor<Void>, StatementVisitor<Void> {
    /**
     * Reporter of compile-time errors.
     */
    private final ErrorReporter reporter;
    /**
     * The interpreter executing the code (runtime).
     */
    private final Interpreter interpreter;
    /**
     * Stack of block scopes each containing the names of the declared variables.
     * (The first scope is always the global scope.)
     */
    private final Stack<Set<String>> scopes = new Stack<>();
    /**
     * The current context in which something is being resolved.
     * (E.g. a function or method.)
     */
    private ContextType currentContext = ContextType.NONE;

    public Resolver(Interpreter interpreter, ErrorReporter reporter) {
        this.interpreter = interpreter;
        this.reporter = reporter;

        // The first scope on the stack is the global scope.
        createScope();
        scopes.peek().addAll(interpreter.getNativeNames());
    }

    /**
     * Resolve a list of statements.
     *
     * @param statements The statements to resolve.
     */
    public void resolve(List<Statement> statements) {
        for (Statement statement : statements) {
            resolve(statement);
        }
    }

    @Override
    public Void visitBlockStatement(Statement.Block statement) {
        createScope();
        resolve(statement.statements);
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
    public Void visitChangeStatement(Statement.Change statement) {
        resolve(statement.assignment);
        resolve(statement.name);

        return null;
    }

    @Override
    public Void visitDefineStatement(Statement.Define statement) {
        // Declare the name in the current scope before resolving the
        // function's statements in its inner scope to allow for recursion.
        declare(statement.name);
        resolveFunction(statement, ContextType.FUNCTION);

        return null;
    }

    @Override
    public Void visitExpressionStatement(Statement.ExpressionStatement statement) {
        resolve(statement.expression);

        return null;
    }

    @Override
    public Void visitIfStatement(Statement.If statement) {
        resolve(statement.condition);
        resolve(statement.thenBranch);

        for (Statement.OtherwiseIf otherwiseIf : statement.otherwiseIfs) {
            resolve(otherwiseIf.condition);
            resolve(otherwiseIf.thenBranch);
        }

        if (statement.otherwiseBranch != null) {
            resolve(statement.otherwiseBranch);
        }

        return null;
    }

    @Override
    public Void visitRepeatTimesStatement(Statement.RepeatTimes statement) {
        resolve(statement.times);
        resolve(statement.body);

        return null;
    }

    @Override
    public Void visitRepeatWhileStatement(Statement.RepeatWhile statement) {
        resolve(statement.condition);
        resolve(statement.body);

        return null;
    }

    @Override
    public Void visitReturnStatement(Statement.Return statement) {
        if (currentContext != ContextType.FUNCTION) {
            error(statement.location, "You can only return from within a definition.");
        }

        return null;
    }

    @Override
    public Void visitReturnWithStatement(Statement.ReturnWith statement) {
        if (currentContext != ContextType.FUNCTION) {
            error(statement.location, "You can only return from within a definition.");
        }

        resolve(statement.value);

        return null;
    }

    @Override
    public Void visitBinaryExpression(Expression.Binary expression) {
        resolve(expression.left);
        resolve(expression.right);

        return null;
    }

    @Override
    public Void visitCallExpression(Expression.Call expression) {
        resolve(expression.callee);
        for (Expression argument : expression.arguments) {
            resolve(argument);
        }

        return null;
    }

    @Override
    public Void visitGroupingExpression(Expression.Grouping expression) {
        resolve(expression.expression);

        return null;
    }

    @Override
    public Void visitLiteralExpression(Expression.Literal expression) {
        return null;
    }

    @Override
    public Void visitLogicalExpression(Expression.Logical expression) {
        resolve(expression.left);
        resolve(expression.right);

        return null;
    }

    @Override
    public Void visitUnaryExpression(Expression.Unary expression) {
        resolve(expression.right);

        return null;
    }

    @Override
    public Void visitVariableExpression(Expression.Variable expression) {
        resolve(expression.name);

        return null;
    }

    /**
     * Declare a name in the innermost scope.
     *
     * @param name The name to be declared.
     */
    private void declare(Token name) {
        Set<String> scope = getInnermostScope();
        if (scope.contains(name.lexeme)) {
            error(name, "'" + name.lexeme + "' already exists.");
        }

        scope.add(name.lexeme);
    }

    /**
     * Declare names in the innermost scope.
     *
     * @param names The names to be declared.
     */
    private void declare(List<Token> names) {
        for (Token name : names) {
            declare(name);
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
     * Resolve a variable.
     *
     * @param name The name to resolve.
     */
    private void resolve(Token name) {
        // Search for the name in each scope to know where it was most recently
        // declared (lexically closer) by starting from the innermost scope.
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).contains(name.lexeme)) {
                // Add the distance to the interpreter so that it can
                // look up the variable in the correct environment.
                interpreter.resolve(name, getDistanceToScope(i));

                return;
            }
        }

        // If this is reached, the variable or function has not been
        // declared lexically prior to where it is being referenced.
        error(name, "'" + name.lexeme + "' has not been created or defined. To create it, use 'create', or define it using 'define'.");
    }

    /**
     * Resolve a function.
     *
     * @param function The function to resolve.
     * @param context The context (function or method).
     */
    private void resolveFunction(Statement.Define function, ContextType context) {
        // Before resolving the function, set the current context to
        // the one passed so that the resolver can generate errors
        // when, for instance, "return" is used outside a function.
        ContextType enclosingContext = currentContext;
        currentContext = context;

        // Declare the parameters in the function's local scope.
        createScope();
        declare(function.parameters);
        resolve(function.body.statements);
        discardScope();

        // Reset the context.
        currentContext = enclosingContext;

        // Note:
        // Don't call resolve(function.body) because that will in turn call
        // the visitBlockStatement() which creates an additional inner scope.
        // resolveFunction() is separated in order to declare the parameters
        // in its local scope.
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
        if (!isGlobalScope()) {
            scopes.pop();
        }
    }

    /**
     * Get the innermost scope.
     *
     * @return The innermost scope.
     */
    private Set<String> getInnermostScope() {
        return scopes.peek();
    }

    /**
     * Get the distance from the innermost scope to an outer scope.
     *
     * @param targetScopeIndex The index of the target scope.
     * @return The distance to the target scope.
     */
    private int getDistanceToScope(int targetScopeIndex) {
        int innermostScopeIndex = scopes.size() - 1;

        return innermostScopeIndex - targetScopeIndex;
    }

    /**
     * Check if the current scope is the global scope.
     *
     * @return Whether the current scope is the global scope.
     */
    private boolean isGlobalScope() {
        return scopes.size() == 1;
    }

    /**
     * Verify that the variable being initialized is not also being
     * accessed in its initializer.
     *
     * @param initialized The initialized variable.
     * @param initializer The right-hand side initializer.
     */
    private void verifyNotAccessingItselfInInitializer(Token initialized, Expression initializer) {
        if (!(initializer instanceof Expression.Variable))
            return;

        // Accessing a variable that has previously been initialized in
        // an outer scope with the same name as the one currently being
        // initialized is not allowed in its own initializer. E.g:
        // create x = 1
        // if x > 0
        //      create x = x        <-- illegal access on right-hand side
        Token initializerName = ((Expression.Variable)initializer).name;
        if (initializerName.lexeme.equals(initialized.lexeme)) {
            error(initializerName, "You cannot use '" + initializerName.lexeme + "' on both sides of '=' when creating it.");
        }

        // TODO: Update implementation. The current implementation applies to
        //       right-hand sides that are exactly a variable, not e.g. x + 1
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
