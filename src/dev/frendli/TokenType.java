package dev.frendli;

public enum TokenType {
    // Single characters
    COMMA,
    DOT,
    EQUALS_SIGN,
    GREATER_THAN,
    GREATER_THAN_EQUALS,
    LESS_THAN,
    LESS_THAN_EQUALS,
    MINUS,
    OPEN_PAREN,
    CLOSE_PAREN,
    PLUS,
    STAR,
    SLASH,

    // Keywords
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
    NEW,
    NOT,
    OR,
    OTHERWISE,
    PARENT,
    REPEAT,
    RETURN,
    SEND,
    TIMES,
    TRUE,
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
    EOF
}
