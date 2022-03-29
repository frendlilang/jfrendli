package dev.frendli;

/**
 * The interpreter - recursively traverses the syntax tree produced
 * by the parser and interprets the nodes by computing the corresponding
 * values. The current node always evaluates its children first (post-order traversal).
 */
public class Interpreter implements Expression.Visitor<Object> {
    @Override
    public Object visitBinaryExpression(Expression.Binary expression) {
        // Evaluate the operands left to right
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);

        switch (expression.operator.type) {
            case EQUALS_WORD:
                return isEqual(left, right);
            case GREATER_THAN:
                return (double)left > (double)right;
            case GREATER_THAN_EQUALS:
                return (double)left >= (double)right;
            case LESS_THAN:
                return (double)left < (double)right;
            case LESS_THAN_EQUALS:
                return (double)left <= (double)right;
            case MINUS:
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
                break;
            case SLASH:
                return (double)left / (double)right;
            case STAR:
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
                // Cast the right value to a double
                // before applying the operator.
                return -(double)right;
            case NOT:
                return !isTruthy(right);
        }

        return null;
    }

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
}
