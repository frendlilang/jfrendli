package dev.frendli;

import java.util.ArrayList;
import java.util.List;

/**
 * The interpreter - recursively traverses the syntax tree produced
 * by the parser and interprets the nodes by computing the corresponding
 * values. The current node always evaluates its children first (post-order traversal).
 */
public class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void> {
    private final ErrorReporter reporter;
    public final Environment globalEnvironment = new Environment();
    private Environment currentEnvironment = globalEnvironment;

    public Interpreter(ErrorReporter reporter) {
        globalEnvironment.defineNative("time", new NativeFunction.Time());
        globalEnvironment.defineNative("display", new NativeFunction.Display());
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
        currentEnvironment.assign(statement.name, value);

        return null;
    }
    
    @Override
    public Void visitDefineStatement(Statement.Define statement) {
        FrendliFunction function = new FrendliFunction(statement);
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
        if (isTrue(statement.start, evaluate(statement.condition))) {
            execute(statement.thenBranch);
        }
        else if (statement.otherwiseBranch != null) {
            execute(statement.otherwiseBranch);
        }

        return null;
    }

    @Override
    public Void visitRepeatTimesStatement(Statement.RepeatTimes statement) {
        Object times = evaluate(statement.times);
        verifyPositiveInteger(statement.start, times);

        int exactTimes = (int)((double)times);
        for (int i = 0; i < exactTimes; i++) {
            execute(statement.body);
        }

        return null;
    }

    @Override
    public Void visitRepeatWhileStatement(Statement.RepeatWhile statement) {
        while (isTrue(statement.start, evaluate(statement.condition))) {
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
                verifyNumberOperands(operator, left, right);
                return (double)left > (double)right;
            case GREATER_THAN_EQUALS:
                verifyNumberOperands(operator, left, right);
                return (double)left >= (double)right;
            case LESS_THAN:
                verifyNumberOperands(operator, left, right);
                return (double)left < (double)right;
            case LESS_THAN_EQUALS:
                verifyNumberOperands(operator, left, right);
                return (double)left <= (double)right;
            case MINUS:
                verifyNumberOperands(operator, left, right);
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
                verifyNumberOperands(operator, left, right);
                verifyNonZeroOperand(operator, right);
                return (double)left / (double)right;
            case STAR:
                verifyNumberOperands(operator, left, right);
                return (double)left * (double)right;
        }

        return null;
    }

    @Override
    public Object visitCallExpression(Expression.Call expression) {
        Object callee = evaluate(expression.callee);

        // Evaluate the arguments from left to right
        List<Object> arguments = new ArrayList<>();
        for (Expression argument : expression.arguments) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof FrendliCallable)) {
            throw new RuntimeError(expression.endToken, "You can only call what has previously been defined (with 'define') or described (with 'describe').");
        }
        FrendliCallable function = (FrendliCallable)callee;

        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expression.endToken, "The number of arguments sent must be " + function.arity() + " but got " + arguments.size() + ".");
        }

        return function.call(this, arguments);
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
            if (isTrue(operator, left)) {
                return true;
            }
        }
        else /* operator == AND */ {
            if (!isTrue(operator, left)) {
                return false;
            }
        }

        return isTrue(operator, evaluate(expression.right));
    }

    @Override
    public Object visitGroupingExpression(Expression.Grouping expression) {
        // The Grouping expression object references another expression
        // (the one in between the parentheses) which needs to be evaluated.
        return evaluate(expression.expression);
    }

    @Override
    public Object visitUnaryExpression(Expression.Unary expression) {
        Object right = evaluate(expression.right);
        Token operator = expression.operator;

        // Apply the operator to the right expression
        // after the expression has been evaluated.
        switch (operator.type) {
            case MINUS:
                verifyNumberOperand(operator, right);
                return -(double)right;
            case NOT:
                return !isTrue(operator, right);
        }

        return null;
    }
    
    @Override
    public Object visitVariableExpression(Expression.Variable expression) {
        return currentEnvironment.get(expression.name);
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
     * @param location The location of the nearest token.
     * @param operand The operand to be checked.
     * @return Whether it is true.
     */
    private boolean isTrue(Token location, Object operand) {
        verifyBooleanOperand(location, operand);

        return (boolean)operand;
    }

    /**
     * Verify that the operand is a boolean and throw a RuntimeError if not.
     *
     * @param location The location of the nearest token.
     * @param operand The operand.
     */
    private void verifyBooleanOperand(Token location, Object operand) {
        if (operand instanceof Boolean) {
            return;
        }

        throw new RuntimeError(location, "The operand must be a boolean ('true' or 'false').");
    }

    /**
     * Verify that the operand/denominator is a non-zero number and throw
     * a RuntimeError if not.
     *
     * @param location The location of the nearest token.
     * @param operand The operand.
     */
    private void verifyNonZeroOperand(Token location, Object operand) {
        if (operand instanceof Double && (double)operand != 0) {
            return;
        }

        throw new RuntimeError(location, "Division by zero is not allowed. The operand must be a non-zero number.");
    }

    /**
     * Verify that the operand is a number and throw a RuntimeError if not.
     *
     * @param location The location of the nearest token.
     * @param operand The operand.
     */
    private void verifyNumberOperand(Token location, Object operand) {
        if (operand instanceof Double) {
            return;
        }

        throw new RuntimeError(location, "The operand must be a number.");
    }

    /**
     * Verify that the operands are numbers and throw a RuntimeError if not.
     *
     * @param location The location of the nearest token.
     * @param left The left operand.
     * @param right The right operand.
     */
    private void verifyNumberOperands(Token location, Object left, Object right) {
        // Evaluate both operands before reporting the error.
        if (left instanceof Double && right instanceof Double) {
            return;
        }

        throw new RuntimeError(location, "The operands must be numbers.");
    }

    /**
     * Verify that the number is positive can be represented as integer,
     * otherwise throw a RuntimeError.
     *
     * @param location The location of the nearest token.
     * @param number The number.
     */
    private void verifyPositiveInteger(Token location, Object number) {
        verifyNumberOperand(location, number);
        double numberDouble = (double)number;
        if (numberDouble > 0 && Math.floor(numberDouble) == numberDouble) {
            return;
        }

        throw new RuntimeError(location, "The number must be a positive integer.");
    }
}
