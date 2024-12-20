package dev.frendli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The scanner/lexer - traverses the characters in the source code
 * and generates the corresponding tokens as well as detects errors
 * in individual tokens.
 */
public class Scanner {
    /**
     * Reserved keywords.
     */
    private static final Map<String, TokenType> keywords;
    /**
     * Reporter of lexical errors.
     */
    private final ErrorReporter reporter;
    /**
     * Source code.
     */
    private final String source;
    /**
     * Tokens produced by this scanner.
     */
    private final List<Token> tokens = new ArrayList<>();
    /**
     * Max indent level allowed.
     */
    private final int MAX_INDENT_LEVEL = 100;
    /**
     * Stack with number of columns used in each indent level.
     */
    private final int[] indentStack = new int[MAX_INDENT_LEVEL];
    /**
     * Stack with number of alt. columns used in each indent level.
     */
    private final int[] altIndentStack = new int[MAX_INDENT_LEVEL];
    /**
     * Current indent level (always incremented by 1).
     */
    private int indentLevel = 0;
    /**
     * Number of pending indents (if > 0) or dedents (if < 0) to add.
     */
    private int pendingIndents = 0;
    /**
     * Number of columns used in the current indentation.
     */
    private int columnsInIndent = 0;
    /**
     * Number of alt. columns used in the current indentation.
     */
    private int altColumnsInIndent = 0;
    /**
     * Number of spaces used in the current indentation.
     */
    private int spacesInIndent = 0;
    /**
     * Number of tabs used in the current indentation.
     */
    private int tabsInIndent = 0;
    /**
     * Position of the first character of the current lexeme being scanned.
     */
    private int start = 0;
    /**
     * Position of the current unconsumed character of the current lexeme being scanned.
     */
    private int current = 0;
    /**
     * Current line in the source file (for error reporting).
     */
    private int line = 1;
    /**
     * Whether the scanner is at the start of the line.
     */
    private boolean isAtStartOfLine = true;
    /**
     * Whether the current line is blank (i.e. only contains whitespace, comments, and/or a newline).
     */
    private boolean isBlankLine = false;

    public Scanner(String source, ErrorReporter reporter) {
        this.source = source;
        this.reporter = reporter;
    }

    static {
        keywords = new HashMap<>();
        keywords.put("accept", TokenType.ACCEPT);
        keywords.put("and", TokenType.AND);
        keywords.put("change", TokenType.CHANGE);
        keywords.put("create", TokenType.CREATE);
        keywords.put("define", TokenType.DEFINE);
        keywords.put("describe", TokenType.DESCRIBE);
        keywords.put("empty", TokenType.EMPTY);
        keywords.put("equals", TokenType.EQUALS_WORD);
        keywords.put("false", TokenType.FALSE);
        keywords.put("has", TokenType.HAS);
        keywords.put("if", TokenType.IF);
        keywords.put("inherit", TokenType.INHERIT);
        keywords.put("me", TokenType.ME);
        keywords.put("not", TokenType.NOT);
        keywords.put("or", TokenType.OR);
        keywords.put("otherwise", TokenType.OTHERWISE);
        keywords.put("parent", TokenType.PARENT);
        keywords.put("repeat", TokenType.REPEAT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("send", TokenType.SEND);
        keywords.put("times", TokenType.TIMES);
        keywords.put("true", TokenType.TRUE);
        keywords.put("unequals", TokenType.UNEQUALS);
        keywords.put("while", TokenType.WHILE);
        keywords.put("with", TokenType.WITH);
    }

    /**
     * Scan the source code for tokens.
     *
     * @return List of tokens.
     */
    public List<Token> scan() {
        while (!isAtEnd()) {
            scanToken();
        }

        // Reset indent level and add remaining DEDENT tokens for correctly ended files.
        boolean endsWithNewline = (current > 0 && getJustConsumed() == Ascii.NEWLINE);
        if (endsWithNewline) {
            resetIndents();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line));

        return tokens;
    }

    /**
     * Scan the current lexeme for a token.
     */
    private void scanToken() {
        advance();

        if (isAtStartOfLine) {
            isAtStartOfLine = false;
            isBlankLine = false;
            consumeIndent();
        }

        // Reset the start position of the lexeme to the first character
        // in the one being scanned (previous calls to 'advance()' above
        // will increment 'current', therefore 'current - 1' is needed.)
        start = current - 1;

        char character = getJustConsumed();
        switch (character) {
            // '\r' and '\r\n' have been replaced with '\n'
            // prior to sending the source code to Scanner.
            case Ascii.NEWLINE:
                if (!isBlankLine) {
                    addToken(TokenType.NEWLINE);
                }
                line++;
                isAtStartOfLine = true;
                break;
            // Ignore whitespace and tabs that are
            // not at the beginning of the lines.
            case Ascii.SPACE:
            case Ascii.TAB:
                break;
            case Ascii.OPEN_PAREN:
                addToken(TokenType.OPEN_PAREN);
                break;
            case Ascii.CLOSE_PAREN:
                addToken(TokenType.CLOSE_PAREN);
                break;
            case Ascii.COMMA:
                addToken(TokenType.COMMA);
                break;
            case Ascii.DOT:
                addToken(TokenType.DOT);
                break;
            case Ascii.EQUALS:
                addToken(TokenType.EQUALS_SIGN);
                break;
            case Ascii.MINUS:
                addToken(TokenType.MINUS);
                break;
            case Ascii.PLUS:
                addToken(TokenType.PLUS);
                break;
            case Ascii.STAR:
                addToken(TokenType.STAR);
                break;
            case Ascii.GREATER_THAN:
                addToken(match(Ascii.EQUALS)
                    ? TokenType.GREATER_THAN_EQUALS
                    : TokenType.GREATER_THAN);
                break;
            case Ascii.LESS_THAN:
                addToken(match(Ascii.EQUALS)
                    ? TokenType.LESS_THAN_EQUALS
                    : TokenType.LESS_THAN);
                break;
            case Ascii.SLASH:
                if (match(Ascii.SLASH)) {
                    skipUntilEndOfLine();
                }
                else {
                    addToken(TokenType.SLASH);
                }
                break;
            case Ascii.DOUBLE_QUOTE:
                consumeText();
                break;
            default:
                // Check digits and alphas here
                // instead of in individual cases.
                if (isDigit(character)) {
                    consumeNumber();
                }
                else if (isAlpha(character)) {
                    consumeIdentifier();
                }
                else {
                    error(line, "Found an unexpected character " + character);
                }
                break;
        }
    }

    /**
     * Consume the current indentation.
     */
    private void consumeIndent() {
        // Count and consume the spaces and tabs.
        countColumnsInIndent();

        char character = getJustConsumed();

        // Skip blank lines (lines with only whitespace, comments, and/or newline).
        boolean isComment = (character == Ascii.SLASH && peek() == Ascii.SLASH);
        isBlankLine = (character == Ascii.SPACE || character == Ascii.TAB || isComment || character == Ascii.NEWLINE);
        if (isBlankLine) {
            if (isAtEnd() && character != Ascii.NEWLINE) {
                error(line, "The last line is indented. The file must end with a new line without indentation.");
            }

            // Do not increment line count here as other reported errors
            // will then report the next line rather than the current.
            // (The newline is caught by the switch statement in scanToken.)
            return;
        }

        // Only check for mixed tabs and spaces after checking blank lines.
        // (Whether they are mixed in blank lines is insignificant.)
        boolean isMixingTabsAndSpaces = (tabsInIndent > 0 && spacesInIndent > 0);
        if (isMixingTabsAndSpaces) {
            error(line, "Found both spaces and tabs in the indentation. Use only one or the other.");
        }

        // Check if the line is as equally indented as the previous line.
        if (columnsInIndent == indentStack[indentLevel]) {
            if (altColumnsInIndent != altIndentStack[indentLevel]) {
                error(line, "There is a problem with the indentation.");
            }
        }
        // Check if the line is more indented than the previous line.
        else if (columnsInIndent > indentStack[indentLevel]) {
            increaseIndent();
        }
        // Check if the line is less indented than the previous line.
        else {
            decreaseIndent();
        }
    }

    /**
     * Count and consume the columns in the indentation.
     */
    private void countColumnsInIndent() {
        final int TAB_COLUMN_SIZE = 8;
        final int ALT_TAB_COLUMN_SIZE = 1;

        columnsInIndent = 0;
        altColumnsInIndent = 0;
        spacesInIndent = 0;
        tabsInIndent = 0;

        char character = getJustConsumed();
        while ((character == Ascii.SPACE || character == Ascii.TAB)) {
            if (character == Ascii.SPACE) {
                spacesInIndent++;
                columnsInIndent++;
                altColumnsInIndent++;
            }
            else {
                tabsInIndent++;
                columnsInIndent = tabsToSpaces(columnsInIndent, TAB_COLUMN_SIZE);
                altColumnsInIndent = tabsToSpaces(altColumnsInIndent, ALT_TAB_COLUMN_SIZE);
            }

            // Don't add '&& !isAtEnd()' to the loop condition as
            // it prevents the last space/tab from being counted.
            if (isAtEnd()) {
                return;
            }
            character = advance();
        }
    }

    /**
     * Increase the indent level by 1 level and add the remaining indent.
     */
    private void increaseIndent() {
        // Check if the next level of indentation exceeds allowed limit.
        if (indentLevel + 1 >= MAX_INDENT_LEVEL) {
            error(line, "The max indentation has been reached. You cannot indent further.");
        }
        if (altColumnsInIndent <= altIndentStack[indentLevel]) {
            error(line, "You may be using tabs in this indentation, and spaces in the previous one. Use only spaces or tabs.");
        }

        // When the current line is more indented than the previous one,
        // add a new indent level to the stack, store the number of columns
        // used in that indent level, and increment pending indents so
        // INDENT tokens can be added later.
        pendingIndents++;
        indentLevel++;
        indentStack[indentLevel] = columnsInIndent;
        altIndentStack[indentLevel] = altColumnsInIndent;

        addPendingIndents();
    }

    /**
     * Decrease the indent level down to the next consistent level
     * and add the remaining dedents.
     */
    private void decreaseIndent() {
        // When the current line is less indented than the previous one, pop
        // indent levels off of the stack until the current indentation is
        // consistent with any other indent level, and decrement pending
        // indents so DEDENT tokens can be added later.
        while (indentLevel > 0 && columnsInIndent < indentStack[indentLevel]) {
            pendingIndents--;
            indentLevel--;
        }
        // Check if the line is still not consistently indented.
        if (columnsInIndent != indentStack[indentLevel] || altColumnsInIndent != altIndentStack[indentLevel]) {
            error(line, "The level of indentation does not match any previous lines. (Make sure to use only tabs or only spaces on all lines.)");
        }

        addPendingIndents();
    }

    /**
     * Reset the indent level to 0 and add the remaining dedents.
     */
    private void resetIndents() {
        while (indentLevel > 0) {
            pendingIndents--;
            indentLevel--;
        }
        addPendingIndents();
    }

    /**
     * Add the pending indents or dedents.
     */
    private void addPendingIndents() {
        while (pendingIndents != 0) {
            if (pendingIndents > 0) {
                addToken(TokenType.INDENT);
                pendingIndents--;
            }
            else {
                addToken(TokenType.DEDENT);
                pendingIndents++;
            }
        }
    }

    /**
     * Consume the current number.
     */
    private void consumeNumber() {
        while (isDigit(peek())) {
            advance();
        }

        // Only consume the dot if there are succeeding digits
        // (since it may otherwise be a method call).
        if (peek() == Ascii.DOT && isDigit(peekNext())) {
            // Consume the dot (.) then all following digits.
            do {
                advance();
            } while (isDigit(peek()));
        }

        double literal = Double.parseDouble(getJustConsumedLexeme());
        addToken(TokenType.NUMBER, literal);
    }

    /**
     * Consume the current identifier.
     */
    private void consumeIdentifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }

        // If the lexeme matches one of the reserved keywords, the token
        // type will be that of the keyword, otherwise a regular identifier.
        TokenType type = keywords.getOrDefault(getJustConsumedLexeme(), TokenType.IDENTIFIER);
        addToken(type);
    }

    /**
     * Consume the current text literal.
     */
    private void consumeText() {
        while (peek() != Ascii.DOUBLE_QUOTE && peek() != Ascii.NEWLINE && !isAtEnd()) {
            advance();
        }

        if (isAtEnd() || peek() == Ascii.NEWLINE) {
            error(line, "The text is not terminated. Texts must be terminated on the same line by a " + Ascii.DOUBLE_QUOTE);
            return;
        }

        // Consume the terminating double quote (").
        advance();

        // Remove double quotes from text literal.
        String literal = getLexeme(start + 1, current - 1);
        addToken(TokenType.TEXT, literal);
    }

    /**
     * Create a token from the current lexeme that does not
     * have a literal value and add it to the list of tokens.
     *
     * @param type The type of the token.
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * Create a token from the current lexeme and add it to
     * the list of tokens.
     *
     * @param type The type of the token.
     * @param literal The literal value of the token.
     */
    private void addToken(TokenType type, Object literal) {
        tokens.add(new Token(type, getJustConsumedLexeme(), literal, line));
    }

    /**
     * Advance to the next character.
     *
     * @return The consumed character.
     */
    private char advance() {
        return source.charAt(current++);
    }

    /**
     * Check if the current unconsumed character matches an expected
     * character and consume it if it does.
     *
     * @param expected The expected character.
     * @return Whether they match or not.
     */
    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (getCurrentUnconsumed() != expected) {
            return false;
        }

        current++;

        return true;
    }

    /**
     * Look ahead at the current unconsumed character without consuming it.
     *
     * @return The current unconsumed character.
     */
    private char peek() {
        if (isAtEnd()) {
            return Ascii.NULL;
        }

        return getCurrentUnconsumed();
    }

    /**
     * Look ahead at the next unconsumed character without consuming it.
     *
     * @return The next unconsumed character.
     */
    private char peekNext() {
        if (current + 1 > source.length()) {
            return Ascii.NULL;
        }

        return source.charAt(current + 1);
    }

    /**
     * Get the current character that has not yet been consumed.
     *
     * @return The current unconsumed character.
     */
    private char getCurrentUnconsumed() {
        return source.charAt(current);
    }

    /**
     * Get the character that was most recently consumed.
     *
     * @return The most recently consumed character.
     */
    private char getJustConsumed() {
        return source.charAt(current - 1);
    }

    /**
     * Get a lexeme.
     *
     * @param start The start index (inclusive).
     * @param end The end index (exclusive).
     * @return The lexeme.
     */
    private String getLexeme(int start, int end) {
        return source.substring(start, end);
    }

    /**
     * Get the lexeme that was most recently consumed.
     *
     * @return The most recently consumed lexeme.
     */
    private String getJustConsumedLexeme() {
        return source.substring(start, current);
    }

    /**
     * Advance to the end of the line.
     */
    private void skipUntilEndOfLine() {
        while (peek() != Ascii.NEWLINE && !isAtEnd()) {
            advance();
        }
    }

    /**
     * Check if a character is alpha (a-Z) or underscore.
     *
     * @param character The character to be checked.
     * @return Whether it is alpha.
     */
    private boolean isAlpha(char character) {
        return (character >= Ascii.a && character <= Ascii.z)
                || (character >= Ascii.A && character <= Ascii.Z)
                || (character == Ascii.UNDERSCORE);
    }

    /**
     * Check if a character is alphanumeric (a-Z, 0-9) or underscore.
     *
     * @param character The character to be checked.
     * @return Whether it is alphanumeric.
     */
    private boolean isAlphaNumeric(char character) {
        return isAlpha(character) || isDigit(character);
    }

    /**
     * Check if a character is a digit (0-9).
     *
     * @param character The character to be checked.
     * @return Whether it is a digit.
     */
    private boolean isDigit(char character) {
        return character >= Ascii._0 && character <= Ascii._9;
    }

    /**
     * Check if the end of the source file has been reached.
     *
     * @return Whether it is at the end.
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Convert tabs to spaces.
     *
     * @param startColumnOfTab The column where the tab starts.
     * @param tabSize The size of the tab.
     * @return The size in spaces.
     */
    private int tabsToSpaces(int startColumnOfTab, int tabSize) {
        return (startColumnOfTab / tabSize + 1) * tabSize;
    }

    /**
     * Report a syntax error.
     *
     * @param line The line where the error occurred.
     * @param message The error message.
     */
    private void error(int line, String message) {
        reporter.compileTimeError(line, message);
    }
}
