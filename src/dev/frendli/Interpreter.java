package dev.frendli;

import java.util.List;

/**
 * The interpreter - recursively traverses the syntax tree produced
 * by the parser and interprets the nodes by computing the corresponding
 * values. The current node always evaluates its children first (post-order traversal).
 */
public class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void> {
    private ErrorReporter reporter;
    private Environment environment = new Environment();

    public Interpreter(ErrorReporter reporter) {
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
        executeBlock(block.statements, new Environment(environment));

        return null;
    }

    @Override
    public Void visitCreateStatement(Statement.Create statement) {
        Object value = evaluate(statement.initializer);
        environment.define(statement.name, value);

        return null;
    }

    @Override
    public Void visitChangeStatement(Statement.Change statement) {
        Object value = evaluate(statement.assignment);
        environment.assign(statement.name, value);

        return null;
    }

    @Override
    public Void visitDisplayStatement(Statement.Display statement) {
        Object value = evaluate(statement.expression);
        print(stringify(value));

        return null;
    }

    @Override
    public Void visitExpressionStatement(Statement.ExpressionStatement statement) {
        evaluate(statement.expression);

        return null;
    }

    @Override
    public Object visitBinaryExpression(Expression.Binary expression) {
        // Evaluate the operands left to right
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);

        switch (expression.operator.type) {
            case EQUALS_WORD:
                return isEqual(left, right);
            case GREATER_THAN:
                verifyNumberOperands(expression.operator, left, right);
                return (double)left > (double)right;
            case GREATER_THAN_EQUALS:
                verifyNumberOperands(expression.operator, left, right);
                return (double)left >= (double)right;
            case LESS_THAN:
                verifyNumberOperands(expression.operator, left, right);
                return (double)left < (double)right;
            case LESS_THAN_EQUALS:
                verifyNumberOperands(expression.operator, left, right);
                return (double)left <= (double)right;
            case MINUS:
                verifyNumberOperands(expression.operator, left, right);
                return (double)left - (double)right;
            case NOT_EQUALS:
                return !isEqual(left, right);
            case PLUS:
                // Overload the + operator to allow for text concatenation
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }
                throw new RuntimeError(expression.operator, "The operands must be only numbers or only texts.");
            case SLASH:
                verifyNumberOperands(expression.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                verifyNumberOperands(expression.operator, left, right);
                return (double)left * (double)right;
        }

        return null;
    }

    @Override
    public Object visitLiteralExpression(Expression.Literal expression) {
        return expression.value;
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

        // Apply the operator to the right expression
        // after the expression has been evaluated.
        switch (expression.operator.type) {
            case MINUS:
                verifyNumberOperand(expression.operator, right);
                return -(double)right;
            case NOT:
                return !isTruthy(right);
        }

        return null;
    }
    
    @Override
    public Object visitVariableExpression(Expression.Variable expression) {
        return environment.get(expression.name);
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
    private void executeBlock(List<Statement> statements, Environment innerEnvironment) {
        // Save the enclosing/outer environment so that it can be
        // restored once execution in an inner environment is done
        Environment enclosingEnvironment = innerEnvironment.enclosing;

        try {
            // Set the current environment to the environment of the
            // block to be executed, then execute the statements.
            this.environment = innerEnvironment;
            for (Statement statement : statements) {
                execute(statement);
            }
        }
        finally {
            // Restore the enclosing/outer environment. (Use a
            // "finally" clause in case an exception is thrown.)
            this.environment = enclosingEnvironment;
        }
    }

    /**
     * Check if two objects are equal. (Null values and primitives of
     * the same value are always equal; references are always not equal.)
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
        // line does not throw a NullPointerException
        if (first == null) {
            return false;
        }

        return first.equals(second);
    }

    /**
     * Checks if an object is truthy (everything except the
     * literals "false" and "empty" are truthy).
     *
     * @param object The object to be checked.
     * @return Whether it is truthy.
     */
    private boolean isTruthy(Object object) {
        // "empty" is equivalent to Java's "null"
        if (object == null) {
            return false;
        }
        if (object instanceof Boolean) {
            return (boolean)object;
        }

        return true;
    }

    /**
     * Convert a value to the Frendli representation.
     *
     * @param value The value to convert.
     * @return The Frendli value.
     */
    private String stringify(Object value) {
        if (value == null) {
            return "empty";
        }
        if (value instanceof Double) {
            // Even though all numbers are treated as doubles,
            // show integers without the decimal point.
            String text = value.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return value.toString();
    }

    /**
     * Verify that the operand is a number and throw a RuntimeError if not.
     *
     * @param operator The operator.
     * @param operand The operand.
     */
    private void verifyNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) {
            return;
        }

        throw new RuntimeError(operator, "The operand must be a number.");
    }

    /**
     * Verify that the operands are numbers and throw a RuntimeError if not.
     *
     * @param operator The operator.
     * @param left The left operand.
     * @param right The right operand.
     */
    private void verifyNumberOperands(Token operator, Object left, Object right) {
        // Evaluate both operands before reporting the error.
        if (left instanceof Double && right instanceof Double) {
            return;
        }

        throw new RuntimeError(operator, "The operands must be numbers.");
    }

    private void print(String value) {
        System.out.println(value);
    }
}
