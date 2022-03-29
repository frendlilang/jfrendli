package dev.frendli;

/**
 * The interpreter - recursively traverses the syntax tree produced
 * by the parser and interprets the nodes by computing the corresponding
 * values. The current node always evaluates its children first (post-order traversal).
 */
public class Interpreter implements Expression.Visitor<Object> {
    @Override
    public Object visitLiteralExpression(Expression.Literal expression) {
        return expression.value;
    }

    @Override
    public Object visitUnaryExpression(Expression.Unary expression) {
        Object right = evaluate(expression.right);

        // Apply the operator to the right expression once the
        // expression has been evaluated.
        switch (expression.operator.type) {
            case MINUS:
                // Case the right value to a double before applying
                // the operator.
                return -(double)right;
            case NOT:
                return !isTruthy(right);
        }

        return null;
    }

    @Override
    public Object visitGroupingExpression(Expression.Grouping expression) {
        // The Grouping expression object references another expression
        // (the one in between the parentheses) which need to be evaluated.
        return evaluate(expression.expression);
    }

    private Object evaluate(Expression expression) {
        return expression.accept(this);
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
