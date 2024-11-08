package dev.frendli;

/**
 * Interface for any class that visits statements.
 *
 * @param <R> The return type after visiting the statement.
 */
public interface StatementVisitor<R> {
    R visitBlockStatement(Statement.Block statement);
    R visitCreateStatement(Statement.Create statement);
    R visitChangeStatement(Statement.Change statement);
    R visitDefineStatement(Statement.Define statement);
    R visitExpressionStatement(Statement.ExpressionStatement statement);
    R visitIfStatement(Statement.If statement);
    R visitRepeatTimesStatement(Statement.RepeatTimes statement);
    R visitRepeatWhileStatement(Statement.RepeatWhile statement);
    R visitReturnStatement(Statement.Return statement);
    R visitReturnWithStatement(Statement.ReturnWith statement);
}
