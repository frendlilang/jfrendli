package dev.frendli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The interpreter - recursively traverses the syntax tree produced
 * by the parser and interprets the nodes by computing the corresponding
 * values. The current node always evaluates its children first (post-order traversal).
 */
public class Interpreter implements ExpressionVisitor<Object>, StatementVisitor<Void> {
    private final ErrorReporter reporter;                               // Reporter of runtime errors
    private final Environment globalEnvironment = new Environment();    // The global environment
    private Environment currentEnvironment = globalEnvironment;         // The current environment which changes during execution as blocks are entered and exited
    private final Map<Token, Integer> resolved = new HashMap<>();       // Variables (key) resolved by the resolver (value = distance to corresponding environment)
    private final List<String> nativeNames = new ArrayList<>();         // The names of standard library members (used by Resolver)

    public Interpreter(ErrorReporter reporter, Logger logger) {
        globalEnvironment.defineNative("time", new NativeFunction.Time());
        globalEnvironment.defineNative("display", new NativeFunction.Display(logger));
        nativeNames.add("time");
        nativeNames.add("display");
        this.reporter = reporter;
    }

    /**
     * Interpret and evaluate a syntax tree of statements.
     *
     * @param statements The statements.
     */
    public void interpret(List<Statement> statements) {
        try {
            for (Statement statement : statements) {
                execute(statement);
            }
        }
        catch (RuntimeError error) {
            reporter.runtimeError(error);
        }
    }

    @Override
    public Void visitBlockStatement(Statement.Block block) {
        executeBlock(block.statements, new Environment(currentEnvironment));

        return null;
    }

    @Override
    public Void visitCreateStatement(Statement.Create statement) {
        Object value = evaluate(statement.initializer);
        currentEnvironment.define(statement.name, value);

        return null;
    }

    @Override
    public Void visitChangeStatement(Statement.Change statement) {
        Object value = evaluate(statement.assignment);
        assignVariable(statement.name, value);

        return null;
    }
    
    @Override
    public Void visitDefineStatement(Statement.Define statement) {
        // When a define statement is visited, a runtime representation of the
        // function is created (FrendliFunction) holding its compile-time
        // representation (Statement.Define) and the environment it was declared
        // in (currentEnvironment) for closure. Thus, the function is NOT called
        // here, merely saved to a variable name that can later on be called.
        FrendliFunction function = new FrendliFunction(statement, currentEnvironment);
        currentEnvironment.define(statement.name, function);

        return null;
    }

    @Override
    public Void visitExpressionStatement(Statement.ExpressionStatement statement) {
        evaluate(statement.expression);

        return null;
    }

    @Override
    public Void visitIfStatement(Statement.If statement) {
        if (isTrue(evaluate(statement.condition), statement.location)) {
            execute(statement.thenBranch);
        }
        else {
            boolean otherwiseIfIsTrue = false;
            for (Statement.OtherwiseIf otherwiseIf : statement.otherwiseIfs) {
                if (isTrue(evaluate(otherwiseIf.condition), otherwiseIf.location)) {
                    execute(otherwiseIf.thenBranch);
                    otherwiseIfIsTrue = true;
                    break;
                }
            }

            if (!otherwiseIfIsTrue && statement.otherwiseBranch != null) {
                execute(statement.otherwiseBranch);
            }
        }

        return null;
    }

    @Override
    public Void visitRepeatTimesStatement(Statement.RepeatTimes statement) {
        Object times = evaluate(statement.times);
        verifyPositiveInteger(times, statement.location);

        int exactTimes = (int)((double)times);
        for (int i = 0; i < exactTimes; i++) {
            execute(statement.body);
        }

        return null;
    }

    @Override
    public Void visitRepeatWhileStatement(Statement.RepeatWhile statement) {
        while (isTrue(evaluate(statement.condition), statement.location)) {
            execute(statement.body);
        }

        return null;
    }

    @Override
    public Void visitReturnStatement(Statement.Return statement) {
        // The Return exception class is used for control flow.
        throw new Return(null);
    }

    @Override
    public Void visitReturnWithStatement(Statement.ReturnWith statement) {
        Object value = evaluate(statement.value);

        // The Return exception class is used for control flow.
        throw new Return(value);
    }

    @Override
    public Object visitBinaryExpression(Expression.Binary expression) {
        // Evaluate the operands left to right
        Object left = evaluate(expression.left);
        Token operator = expression.operator;
        Object right = evaluate(expression.right);

        switch (operator.type) {
            case EQUALS_WORD:
                return isEqual(left, right);
            case UNEQUALS:
                return !isEqual(left, right);
            case GREATER_THAN:
                verifyNumberOperands(left, right, operator);
                return (double)left > (double)right;
            case GREATER_THAN_EQUALS:
                verifyNumberOperands(left, right, operator);
                return (double)left >= (double)right;
            case LESS_THAN:
                verifyNumberOperands(left, right, operator);
                return (double)left < (double)right;
            case LESS_THAN_EQUALS:
                verifyNumberOperands(left, right, operator);
                return (double)left <= (double)right;
            case MINUS:
                verifyNumberOperands(left, right, operator);
                return (double)left - (double)right;
            case PLUS:
                // Overload the + operator to allow for text concatenation
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }
                throw new RuntimeError(operator, "The operands must be only numbers or only texts.");
            case SLASH:
                verifyNumberOperands(left, right, operator);
                verifyNonZeroOperand(right, operator);
                return (double)left / (double)right;
            case STAR:
                verifyNumberOperands(left, right, operator);
                return (double)left * (double)right;
        }

        return null;
    }

    @Override
    public Object visitCallExpression(Expression.Call expression) {
        Object callee = evaluate(expression.callee);
        if (!(callee instanceof FrendliCallable)) {
            throw new RuntimeError(expression.location, "You can only call what has previously been defined (with 'define')");
        }
        FrendliCallable function = (FrendliCallable)callee;

        // Evaluate the arguments from left to right
        List<Object> arguments = new ArrayList<>();
        for (Expression argument : expression.arguments) {
            arguments.add(evaluate(argument));
        }

        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expression.location, "The number of arguments sent must be " + function.arity() + " but got " + arguments.size() + ".");
        }

        return function.call(this, arguments);
    }

    @Override
    public Object visitGroupingExpression(Expression.Grouping expression) {
        // The Grouping expression object references another expression
        // (the one in between the parentheses) which needs to be evaluated.
        return evaluate(expression.expression);
    }

    @Override
    public Object visitLiteralExpression(Expression.Literal expression) {
        return expression.value;
    }

    @Override
    public Object visitLogicalExpression(Expression.Logical expression) {
        // Evaluate the left operand first.
        Object left = evaluate(expression.left);
        Token operator = expression.operator;
        if (operator.type == TokenType.OR) {
            if (isTrue(left, operator)) {
                return true;
            }
        }
        else /* operator == AND */ {
            if (!isTrue(left, operator)) {
                return false;
            }
        }

        return isTrue(evaluate(expression.right), operator);
    }

    @Override
    public Object visitUnaryExpression(Expression.Unary expression) {
        Object right = evaluate(expression.right);
        Token operator = expression.operator;

        // Apply the operator to the right expression
        // after the expression has been evaluated.
        switch (operator.type) {
            case MINUS:
                verifyNumberOperand(right, operator);
                return -(double)right;
            case NOT:
                return !isTrue(right, operator);
        }

        return null;
    }
    
    @Override
    public Object visitVariableExpression(Expression.Variable expression) {
        return getVariable(expression.name);
    }

    /**
     * Get the value bound to a variable.
     *
     * @param name The variable name.
     * @return The value.
     */
    private Object getVariable(Token name) {
        // The distance will always be 0 or greater and never null due to the
        // Resolver reporting an error if the local or global name (including
        // native) cannot be resolved (thereby not proceeding to the interpreter).
        // (I.e. this is a coupling point between Resolver and Interpreter.)
        Integer distance = resolved.get(name);

        return currentEnvironment.getAt(distance, name);
    }

    /**
     * Assign a value to a variable.
     *
     * @param name The variable name.
     * @param value The value.
     */
    private void assignVariable(Token name, Object value) {
        // The distance will always be 0 or greater and never null due to the
        // Resolver reporting an error if the local or global name (including
        // native) cannot be resolved (thereby not proceeding to the interpreter).
        // (I.e. this is a coupling point between Resolver and Interpreter.)
        Integer distance = resolved.get(name);
        currentEnvironment.assignAt(distance, name, value);
    }

    /**
     * Evaluate an expression.
     *
     * @param expression The expression to evaluate.
     * @return The resulting value.
     */
    private Object evaluate(Expression expression) {
        return expression.accept(this);
    }

    /**
     * Execute a statement.
     *
     * @param statement The statement to execute.
     */
    private void execute(Statement statement) {
        statement.accept(this);
    }

    /**
     * Execute a block in its corresponding scope/environment.
     *
     * @param statements The statements within the block.
     * @param innerEnvironment The environment.
     */
    public void executeBlock(List<Statement> statements, Environment innerEnvironment) {
        // Save the enclosing/outer environment so that it can be
        // restored once execution in an inner environment is done.
        Environment enclosingEnvironment = this.currentEnvironment;

        try {
            // Set the current environment to the environment of the
            // block to be executed, then execute the statements.
            this.currentEnvironment = innerEnvironment;
            for (Statement statement : statements) {
                execute(statement);
            }
        }
        finally {
            // Restore the enclosing/outer environment. (Use a
            // "finally" clause in case an exception is thrown.)
            this.currentEnvironment = enclosingEnvironment;
        }
    }

    /**
     * Add a local variable to the resolved data.
     *
     * @param name The name to resolve.
     * @param distance The distance from the innermost scope to where the variable is defined.
     */
    public void resolve(Token name, int distance) {
        resolved.put(name, distance);
    }

    /**
     * Check if two objects are equal. (Null values and primitives of the
     * same value are always equal; different references are always unequal.)
     *
     * @param first The first object.
     * @param second The second object.
     * @return Whether they are equal.
     */
    private boolean isEqual(Object first, Object second) {
        if (first == null && second == null) {
            return true;
        }
        // Check if "first" is "null" so that the last
        // line does not throw a NullPointerException.
        if (first == null) {
            return false;
        }

        return first.equals(second);
    }

    /**
     * Check if an operand is true.
     *
     * @param operand The operand to be checked.
     * @param location The location of the nearest token.
     * @return Whether it is true.
     */
    private boolean isTrue(Object operand, Token location) {
        verifyBooleanOperand(operand, location);

        return (boolean)operand;
    }

    /**
     * Verify that the operand is a boolean and throw a RuntimeError if not.
     *
     * @param operand The operand.
     * @param location The location of the nearest token.
     */
    private void verifyBooleanOperand(Object operand, Token location) {
        if (operand instanceof Boolean) {
            return;
        }

        throw new RuntimeError(location, "The operand must be a boolean ('true' or 'false').");
    }

    /**
     * Verify that the operand/denominator is a non-zero number and throw
     * a RuntimeError if not.
     *
     * @param operand The operand.
     * @param location The location of the nearest token.
     */
    private void verifyNonZeroOperand(Object operand, Token location) {
        if (operand instanceof Double && (double)operand != 0) {
            return;
        }

        throw new RuntimeError(location, "Division by zero is not allowed. The operand must be a non-zero number.");
    }

    /**
     * Verify that the operand is a number and throw a RuntimeError if not.
     *
     * @param operand The operand.
     * @param location The location of the nearest token.
     */
    private void verifyNumberOperand(Object operand, Token location) {
        if (operand instanceof Double) {
            return;
        }

        throw new RuntimeError(location, "The operand must be a number.");
    }

    /**
     * Verify that the operands are numbers and throw a RuntimeError if not.
     *
     * @param left The left operand.
     * @param right The right operand.
     * @param location The location of the nearest token.
     */
    private void verifyNumberOperands(Object left, Object right, Token location) {
        // Evaluate both operands before reporting the error.
        if (left instanceof Double && right instanceof Double) {
            return;
        }

        throw new RuntimeError(location, "The operands must be numbers.");
    }

    /**
     * Verify that the number is positive and can be represented as an
     * integer, otherwise throw a RuntimeError.
     *
     * @param number The number.
     * @param location The location of the nearest token.
     */
    private void verifyPositiveInteger(Object number, Token location) {
        verifyNumberOperand(number, location);
        double numberDouble = (double)number;
        if (numberDouble > 0 && Math.floor(numberDouble) == numberDouble) {
            return;
        }

        throw new RuntimeError(location, "The number must be a positive integer.");
    }

    /**
     * Get the names of the native members (standard library).
     *
     * @return The names of the native members.
     */
    public List<String> getNativeNames() {
        return List.copyOf(nativeNames);
    }
}
