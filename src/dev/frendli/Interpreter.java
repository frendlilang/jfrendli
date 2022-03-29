package dev.frendli;

/**
 * The interpreter - recursively traverses the syntax tree produced
 * by the parser and interprets the nodes by computing the corresponding
 * values.
 */
public class Interpreter implements Expression.Visitor<Object> {
    @Override
    public Object visitLiteralExpression(Expression.Literal expression) {
        return expression.value;
    }

    @Override
    public Object visitGroupingExpression(Expression.Grouping expression) {
        // The Grouping expression object references another expression
        // (the one in between the parentheses) which need to be
        // recursively evaluated
        return evaluate(expression.expression);
    }

    private Object evaluate(Expression expression) {
        return expression.accept(this);
    }
}
