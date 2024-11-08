package dev.frendli;

/**
 * Interface for any class that visits expressions.
 *
 * @param <R> The return type after visiting the expression.
 */
public interface ExpressionVisitor<R> {
    R visitBinaryExpression(Expression.Binary expression);
    R visitCallExpression(Expression.Call expression);
    R visitGroupingExpression(Expression.Grouping expression);
    R visitLiteralExpression(Expression.Literal expression);
    R visitLogicalExpression(Expression.Logical expression);
    R visitUnaryExpression(Expression.Unary expression);
    R visitVariableExpression(Expression.Variable expression);
}
