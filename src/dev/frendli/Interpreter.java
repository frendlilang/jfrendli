package dev.frendli;

/**
 * The interpreter - recursively traverses the syntax tree produced
 * by the parser and interprets the nodes by computing the corresponding
 * values. The current node always evaluates its children first (post-order traversal).
 */
public class Interpreter implements Expression.Visitor<Object> {
    private ErrorReporter reporter;

    public Interpreter(ErrorReporter reporter) {
        this.reporter = reporter;
    }

    /**
     * Interpret and evaluate a syntax tree.
     *
     * @param expression The expression to interpret.
     */
    public void interpret(Expression expression){
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        }
        catch (RuntimeError error) {
            reporter.runtimeError(error);
        }
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
        // (the one in between the parentheses) which need to be evaluated.
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
}
