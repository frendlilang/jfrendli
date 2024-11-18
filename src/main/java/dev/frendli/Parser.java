package dev.frendli;

import java.util.ArrayList;
import java.util.List;

// ========
// GRAMMAR: (current grammar implemented) (final version: jfrendli/grammar/frendli-grammar.txt)
// ========
// file:                    statement* EOF ;
// statement:               functionDeclaration
//                          | variableDeclaration
//                          | changeStatement
//                          | expressionStatement
//                          | ifStatement
//                          | repeatTimesStatement
//                          | repeatWhileStatement
//                          | returnStatement
//                          | returnWithStatement ;
// functionDeclaration:     "define" IDENTIFIER "(" parameters? ")" block ;
// parameters:              "accept" IDENTIFIER ( "," IDENTIFIER )* ;
// variableDeclaration:     "create" IDENTIFIER "=" expression NEWLINE ;
// changeStatement:         "change" IDENTIFIER "=" expression NEWLINE ;
// expressionStatement:     expression NEWLINE ;
// ifStatement:             "if" expression block ( "otherwise" "if" expression block )* ( "otherwise" block )?
// repeatTimesStatement:    "repeat" expression "times" block ;
// repeatWhileStatement:    "repeat" "while" expression block ;
// returnStatement:         "return" NEWLINE ;
// returnWithStatement:     "return" "with" expression NEWLINE ;
// block:                   NEWLINE INDENT statement+ DEDENT ;
// expression:              logicOr ;
// logicOr:                 logicAnd ( "or" logicAnd )* ;
// logicAnd:                comparison ( "and" comparison )* ;
// comparison:              term ( ( "<" | "<=" | ">" | ">=" | "equals" | "unequals" ) term )* ;
// term:                    factor ( ( "+" | "-" ) factor )* ;
// factor:                  unary ( ( "*" | "/" ) unary )* ;
// unary:                   ( "not" | "-" ) unary | call ;
// call:                    primary ( "(" arguments? ")" )* ;
// arguments:               "send" expression ( "," expression )* ;
// primary:                 IDENTIFIER | NUMBER | TEXT | "true" | "false" | "empty" | "(" expression ")" ;

/**
 * The parser - traverses the tokens produced by the scanner and
 * maps them to rules in the grammar to form an abstract syntax tree,
 * as well as detecting and reporting parse errors.
 * (It performs recursive descent/top-down parsing, starting with
 * the lowest-precedence.)
 */
public class Parser {
    /**
     * Reporter of syntax errors.
     */
    private final ErrorReporter reporter;
    /**
     * Tokens to be parsed.
     */
    private final List<Token> tokens;
    /**
     * Position of the current unconsumed token.
     */
    private int current = 0;

    public Parser (List<Token> tokens, ErrorReporter reporter) {
        this.tokens = tokens;
        this.reporter = reporter;
    }

    /**
     * Parse the tokens.
     *
     * @return The statement syntax trees.
     */
    public List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(statement());
        }

        return statements;
    }

    // statement: functionDeclaration
    //            | variableDeclaration
    //            | changeStatement
    //            | expressionStatement
    //            | ifStatement
    //            | repeatTimesStatement
    //            | repeatWhileStatement
    //            | returnStatement
    //            | returnWithStatement ;
    private Statement statement() {
        try {
            if (match(TokenType.DEFINE)) {
                return functionDeclaration();
            }
            if (match(TokenType.CREATE)) {
                return variableDeclaration();
            }
            if (match(TokenType.CHANGE)) {
                return changeStatement();
            }
            if (match(TokenType.IF)) {
                return ifStatement();
            }
            if (match(TokenType.REPEAT)) {
                return match(TokenType.WHILE) ? repeatWhileStatement() : repeatTimesStatement();
            }
            if (match(TokenType.RETURN)) {
                return match(TokenType.WITH) ? returnWithStatement() : returnStatement();
            }

            return expressionStatement();
        }
        // Catch parse errors here as the point being synchronized
        // to in the Java call stack when needed. This method is
        // suitable to synchronize to since it always gets called
        // for each statement in the code.
        catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    // functionDeclaration: "define" IDENTIFIER "(" parameters? ")" block ;
    private Statement functionDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "You must provide a name for what you are defining.");
        consume(TokenType.OPEN_PAREN, "An opening parenthesis '(' is missing.");

        List<Token> parameterList = new ArrayList<>();
        if (!check(TokenType.CLOSE_PAREN)) {
            parameterList = parameters();
        }

        consume(TokenType.CLOSE_PAREN, "A closing parenthesis ')' is missing.");
        Statement body = block();

        return new Statement.Define(name, parameterList, (Statement.Block)body);
    }

    // parameters: "accept" IDENTIFIER ( "," IDENTIFIER )* ;
    private List<Token> parameters() {
        final int MAX_PARAMETERS = 255;
        List<Token> parameterList = new ArrayList<>();
        consume(TokenType.ACCEPT, "The list of parameters to accept must begin with the 'accept' keyword.");

        do {
            if (parameterList.size() >= MAX_PARAMETERS) {
                error(peek(), "You cannot accept more than " + MAX_PARAMETERS + " parameters.");
            }
            parameterList.add(consume(TokenType.IDENTIFIER, "You must provide a name for each parameter to accept."));
        }
        while (match(TokenType.COMMA));

        return parameterList;
    }

    // variableDeclaration: "create" IDENTIFIER "=" expression NEWLINE ;
    private Statement variableDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "A name for what is created must be provided, beginning with a letter or underscore, but no reserved keywords.");
        consume(TokenType.EQUALS_SIGN, "'" + name.lexeme + "' must be initialized using '='. You may set it to 'empty' if needed.");
        Expression initializer = expression();
        consumeNewline();

        return new Statement.Create(name, initializer);
    }

    // changeStatement: "change" IDENTIFIER "=" expression NEWLINE ;
    private Statement changeStatement() {
        // The identifier can come from the result of an expression that can be
        // of any size. Thus, do not consume IDENTIFIER directly in the 1st step.
        // E.g. change point.x = 2, where point.x is an expression that produces
        // an l-value (the storage location rather than an r-value).
        Expression expression = expression();
        Token equalsSign = consume(TokenType.EQUALS_SIGN, "A value must be assigned using '='.");

        Token name = null;
        if (expression instanceof Expression.Variable) {
            // Convert the r-value expression into an l-value (the variable name)
            name = ((Expression.Variable)expression).name;
        }
        else {
            // Report an error if the target is invalid, but since the parser is not in
            // a confused state, there is no need to synchronize by throwing the error.
            error(equalsSign, "Values cannot be assigned to that target.");
        }

        Expression value = expression();
        consumeNewline();

        return new Statement.Change(name, value);
    }

    // expressionStatement: expression NEWLINE ;
    private Statement expressionStatement() {
        Expression expression = expression();

        // Anticipate the error of not writing "create" or "change" when
        // declaring or assigning variables. E.g. x = 1 will not be parsed
        // as a "create" or "change" statement; instead, it will end up here.
        if (expression instanceof Expression.Variable && check(TokenType.EQUALS_SIGN)) {
            Token name = ((Expression.Variable)expression).name;
            error(name, "If you meant to create or change " + "'" + name.lexeme + "', use the 'create' or 'change' keyword.");
        }

        consumeNewline();

        return new Statement.ExpressionStatement(expression);
    }

    // ifStatement: "if" expression block ( "otherwise" "if" expression block )* ( "otherwise" block )?
    private Statement ifStatement() {
        Token location = getJustConsumed();
        Expression condition = expression();
        Statement thenBranch = block();
        List<Statement.OtherwiseIf> otherwiseIfs = new ArrayList<>();
        Statement otherwiseBranch = null;

        while (match(TokenType.OTHERWISE) && match(TokenType.IF)) {
            Token otherwiseIfLocation = getJustConsumed();
            Expression otherwiseIfCondition = expression();
            Statement otherwiseIfBranch = block();
            otherwiseIfs.add(new Statement.OtherwiseIf(otherwiseIfCondition, otherwiseIfBranch, otherwiseIfLocation));
        }

        if (getJustConsumed().type == TokenType.OTHERWISE) {
            otherwiseBranch = block();
        }

        return new Statement.If(condition, thenBranch, otherwiseIfs, otherwiseBranch, location);
    }

    // repeatTimesStatement: "repeat" expression "times" block ;
    private Statement repeatTimesStatement() {
        Token location = getJustConsumed();
        Expression times = expression();
        consume(TokenType.TIMES, "The expression must be followed by 'times'.");
        Statement body = block();

        return new Statement.RepeatTimes(times, body, location);
    }

    // repeatWhileStatement: "repeat" "while" expression block ;
    private Statement repeatWhileStatement() {
        Token location = getJustConsumed();
        Expression condition = expression();
        Statement body = block();

        return new Statement.RepeatWhile(condition, body, location);
    }

    // returnStatement: "return" NEWLINE ;
    private Statement returnStatement() {
        Token location = getJustConsumed();
        consume(TokenType.NEWLINE, "You must add a new line after 'return'. To return with a value, use 'return with' instead.");

        return new Statement.Return(location);
    }

    // returnWithStatement: "return" "with" expression NEWLINE ;
    private Statement returnWithStatement() {
        Token location = getJustConsumed();
        Expression value = expression();
        consumeNewline();

        return new Statement.ReturnWith(location, value);
    }

    // block: NEWLINE INDENT statement+ DEDENT ;
    private Statement block() {
        consumeNewline();
        consume(TokenType.INDENT, "Blocks must be indented.");

        List<Statement> statements = new ArrayList<>();
        while (!check(TokenType.DEDENT) && !isAtEnd()) {
            statements.add(statement());
        }

        Token dedent = consume(TokenType.DEDENT, "Blocks must be dedented at the end.");
        if (statements.size() == 0) {
            // Report an error if there are no statements in the block.
            // Synchronization by throwing the error is not needed since
            // the parser understands that the block has ended with DEDENT.
            error(dedent, "Blocks must contain at least 1 statement.");
        }

        return new Statement.Block(statements);
    }

    // expression: logicOr ;
    private Expression expression() {
        return or();
    }

    // logicOr: logicAnd ( "or" logicAnd )* ;
    private Expression or() {
        Expression left = and();
        
        while (match(TokenType.OR)) {
            Token operator = getJustConsumed();
            Expression right = and();
            left = new Expression.Logical(left, operator, right);
        }
        
        return left;
    }

    // logicAnd: comparison ( "and" comparison )* ;
    private Expression and() {
        Expression left = comparison();

        while (match(TokenType.AND)) {
            Token operator = getJustConsumed();
            Expression right = comparison();
            left = new Expression.Logical(left, operator, right);
        }

        return left;
    }

    // comparison: term ( ( "<" | "<=" | ">" | ">=" | "equals" | "unequals" ) term )* ;
    private Expression comparison() {
        Expression left = term();

        while (match(TokenType.LESS_THAN, TokenType.LESS_THAN_EQUALS,
                TokenType.GREATER_THAN, TokenType.GREATER_THAN_EQUALS,
                TokenType.EQUALS_WORD, TokenType.UNEQUALS)) {
            Token operator = getJustConsumed();
            Expression right = term();
            left = new Expression.Binary(left, operator, right);
        }

        return left;
    }

    // term: factor ( ( "+" | "-" ) factor )* ;
    private Expression term() {
        Expression left = factor();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = getJustConsumed();
            Expression right = factor();
            left = new Expression.Binary(left, operator, right);
        }

        return left;
    }

    // factor: unary ( ( "*" | "/" ) unary )* ;
    private Expression factor() {
        Expression left = unary();

        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = getJustConsumed();
            Expression right = unary();
            left = new Expression.Binary(left, operator, right);
        }

        return left;
    }

    // unary: ( "not" | "-" ) unary | call ;
    private Expression unary() {
        if (match(TokenType.NOT, TokenType.MINUS)) {
            Token operator = getJustConsumed();
            Expression right = unary();
            return new Expression.Unary(operator, right);
        }

        return call();
    }

    // call: primary ( "(" arguments? ")" )* ;
    private Expression call() {
        Expression expression = primary();

        while (true) {
            // If there is an open parenthesis, finish parsing the rest
            // of the call and then (through looping) see if the parsed
            // expression is in turn being called. E.g. getFunction()()
            if (match(TokenType.OPEN_PAREN)) {
                // If there is no closing parenthesis, add all arguments.
                List<Expression> argumentList = new ArrayList<>();
                if (!check(TokenType.CLOSE_PAREN)) {
                    argumentList = arguments();
                }
                Token endToken = consume(TokenType.CLOSE_PAREN, "A closing parenthesis ')' is missing.");
                expression = new Expression.Call(expression, argumentList, endToken);;
            }
            else {
                break;
            }
        }

        return expression;
    }

    // arguments: "send" expression ( "," expression )* ;
    private List<Expression> arguments() {
        final int MAX_ARGUMENTS = 255;
        List<Expression> argumentList = new ArrayList<>();
        consume(TokenType.SEND, "The list of arguments to send must begin with the 'send' keyword.");

        do {
            if (argumentList.size() >= MAX_ARGUMENTS) {
                error(peek(), "You cannot send more than " + MAX_ARGUMENTS + " arguments.");
            }
            argumentList.add(expression());
        }
        while (match(TokenType.COMMA));

        return argumentList;
    }

    // primary: IDENTIFIER | NUMBER | TEXT | "true" | "false" | "empty" | "(" expression ")" ;
    private Expression primary() {
        if (match(TokenType.IDENTIFIER)) {
            return new Expression.Variable(getJustConsumed());
        }
        if (match(TokenType.NUMBER, TokenType.TEXT)) {
            return new Expression.Literal(getJustConsumed().literal);
        }
        if (match(TokenType.TRUE)) {
            return new Expression.Literal(true);
        }
        if (match(TokenType.FALSE)) {
            return new Expression.Literal(false);
        }
        if (match(TokenType.EMPTY)) {
            return new Expression.Literal(null);
        }
        if (match(TokenType.OPEN_PAREN)) {
            Expression expression = expression();
            consume(TokenType.CLOSE_PAREN, "A closing parenthesis ')' is missing.");
            return new Expression.Grouping(expression);
        }

        // If this is reached, the current token is not the start of an expression.
        // Hence, an error is thrown to synchronize the parser's state using Java's
        // call stack, catching the exception where it's being synchronized to.
        String message = "Cannot find a valid expression.";

        // If the user starts the expression with an indentation where a
        // new a block is not allowed, provide a more meaningful message.
        if (peek().type == TokenType.INDENT) {
            message = "This line is too indented. Decrease the level of indentation used.";
        }
        else if (getJustConsumed().type == TokenType.WITH) {
            message = "You must add a valid expression after 'return with' to return with that value. To not return an explicit value, use only 'return'.";
        }

        throw error(peek(), message);
    }

    /**
     * Advance to the next token.
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
     * Consume the current token if it is of the expected type,
     * otherwise report and throw an error.
     *
     * @param type The expected type.
     * @param errorMessage The error message if unexpected type.
     * @return The consumed token.
     */
    private Token consume(TokenType type, String errorMessage) {
        if (check(type)) {
            return advance();
        }

        throw error(peek(), errorMessage);
    }

    /**
     * Consume the current token if it is a newline, otherwise
     * report and throw an error.
     *
     * @return The consumed newline token.
     */
    private Token consumeNewline() {
        return consume(TokenType.NEWLINE, "Expected a new line.");
    }

    /**
     * Check if the current unconsumed token matches any of the types
     * provided and consume it if it does.
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

    /**
     * Check if there are no more tokens to be parsed.
     *
     * @return Whether all tokens have been parsed.
     */
    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    /**
     * Synchronize the tokens to the next block or statement.
     * (Prevents cascaded and falsely reported errors.)
     */
    private void synchronize() {
        // Errors caused by an illegal increase in indentation tend to cause more
        // ambiguous cascaded errors in the eyes of a novice compared to other
        // initial errors. Synchronizing to the next same-level block (i.e. just
        // after its corresponding dedentation) in these cases allows novices to
        // focus on the initial errors first while not getting confused about
        // falsely reported errors. An obvious tradeoff is that the code up until
        // the next same-level block will not be checked for errors in this pass.
        Token errorToken = advance();
        if (errorToken.type == TokenType.INDENT) {
            synchronizeToNextBlock();
        }
        else {
            synchronizeToNextStatement();
        }
    }

    /**
     * Synchronize the tokens to the next statement or end
     * of current block.
     */
    private void synchronizeToNextStatement() {
        // In addition to not advancing passed the start of a statement,
        // don't discard a DEDENT as it allows blocks to then consume it.
        // Otherwise, the parser thinks that statements following the DEDENT
        // belongs to the previous block, causing cascaded indent/dedent errors.
        while (!isAtEnd() && !isAtStartOfStatement() && !check(TokenType.DEDENT)) {
            advance();
        }
    }

    /**
     * Synchronize the tokens to the next block on the same level.
     * (I.e. tokens in nested blocks will also be discarded.)
     */
    private void synchronizeToNextBlock() {
        TokenType justConsumedType;
        int nestedLevels = 0;

        while (!isAtEnd()) {
            // When the corresponding DEDENT has been consumed (i.e.
            // nestedLevels == 0) the tokens have been synchronized.
            justConsumedType = advance().type;
            if (justConsumedType == TokenType.DEDENT && nestedLevels == 0) {
                return;
            }
            if (justConsumedType == TokenType.DEDENT) {
                nestedLevels--;
            }
            else if (justConsumedType == TokenType.INDENT) {
                nestedLevels++;
            }
        }
    }

    /**
     * Check if the current unconsumed token is the start
     * of a statement.
     *
     * @return Whether it is the start of a statement.
     */
    private boolean isAtStartOfStatement() {
        if (getJustConsumed().type == TokenType.NEWLINE) {
            return true;
        }

        switch (peek().type) {
            case CHANGE:
            case CREATE:
            case DEFINE:
            case DESCRIBE:
            case HAS:
            case IF:
            case REPEAT:
            case RETURN:
                return true;
            default:
                return false;
        }
    }

    /**
     * Report a parse error.
     *
     * @param token The token that caused the error.
     * @param message The error message.
     * @return The ParseError.
     */
    private ParseError error(Token token, String message) {
        reporter.compileTimeError(token, message);

        // Let the caller decide what to do with the error,
        // as synchronization may not be needed for all errors.
        // Thus don't throw the error here.
        return new ParseError();
    }
}
