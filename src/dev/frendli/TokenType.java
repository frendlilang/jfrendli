package dev.frendli;

/**
 * The type of token.
 */
public enum TokenType {
    // Punctuation and non-keyword operators
    CLOSE_PAREN,
    COMMA,
    DOT,
    EQUALS_SIGN,
    GREATER_THAN,
    GREATER_THAN_EQUALS,
    LESS_THAN,
    LESS_THAN_EQUALS,
    MINUS,
    OPEN_PAREN,
    PLUS,
    STAR,
    SLASH,

    // Reserved keywords
    ACCEPT,
    AND,
    CHANGE,
    CREATE,
    DEFINE,
    DESCRIBE,
    EMPTY,
    EQUALS_WORD,
    FALSE,
    HAS,
    IF,
    INHERIT,
    ME,
    NOT,
    OR,
    OTHERWISE,
    PARENT,
    REPEAT,
    RETURN,
    SEND,
    TIMES,
    TRUE,
    UNEQUALS,
    WHILE,
    WITH,

    // Literals
    IDENTIFIER,
    NUMBER,
    TEXT,

    // Formatting
    DEDENT,
    INDENT,
    NEWLINE,

    // End of file
    EOF;
}
