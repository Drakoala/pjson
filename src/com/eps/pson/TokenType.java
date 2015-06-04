package com.eps.pson;

// Internal
// Indicates the type of a supported JSON token.
enum TokenType {
    NONE,
    VALUE,
    BRACE_OPEN,
    BRACE_CLOSE,
    SQUARE_OPEN,
    SQUARE_CLOSE,
    COLON,
    COMMA,
    STRING,
    NUMBER,
    TRUE,
    FALSE,
    NULL;
}
