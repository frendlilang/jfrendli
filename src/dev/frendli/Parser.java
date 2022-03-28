package dev.frendli;

import java.util.List;

// ========
// GRAMMAR: (incrementally added to and modified)
// ========
// expression:          equality ;
// equality:            comparison ( ( "not"? "equals" ) comparison )* ;
// comparison:          term ( ( "<" | "<=" | ">" | ">=" ) term )* ;
// term:                factor ( ( "+" | "-" ) factor )* ;
// factor:              unary ( ( "*" | "/" ) unary )* ;
// unary:               ( "not" | "-" ) unary | primary ;
// primary:             NUMBER | TEXT | "true" | "false" | "empty" | "(" expression ")" ;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser (List<Token> tokens) {
        this.tokens = tokens;
    }

    // expression: equality ;
    private Expression expression() {
        return equality();
    }

    // equality: comparison ( ( "not"? "equals" ) comparison )* ;
    private Expression equality() {
        Expression expression = comparison();

        while (match(TokenType.NOT, TokenType.EQUALS_WORD)) {
            Token operator = getJustConsumed();
            if (operator.type == TokenType.NOT) {
                consume(TokenType.EQUALS_WORD, "'not' must be followed by 'equals' when comparing equality.");
                operator = new Token(TokenType.NOT_EQUALS, "not equals", null, operator.line);
            }
            Expression right = comparison();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    /**
     * Consume the current token (advance).
     *
     * @return The consumed token.
     */
    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }

        return getJustConsumed();
    }

    /**
     * Check if the current unconsumed token is of any of the types
     * provided and consume it if it is.
     *
     * @param types The types to check for.
     * @return Whether it matches any of the types.
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    /**
     * Check if the current unconsumed token is of a certain type
     * without consuming it.
     *
     * @param type The type to check for.
     * @return Whether it matches the type.
     */
    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }

        return type == peek().type;
    }

    /**
     * Look ahead at the current unconsumed token without consuming it.
     *
     * @return The current unconsumed token.
     */
    private Token peek() {
        return tokens.get(current);
    }

    /**
     * Look at the most recently consumed token.
     *
     * @return The previous token.
     */
    private Token getJustConsumed() {
        return tokens.get(current - 1);
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }
}