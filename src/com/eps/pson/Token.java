package com.eps.pson;

// Internal
// Immutable representation of a JSON token in parsing.
final class Token {
    private final TokenType type;
    private Object value;
    
    Token(final TokenType type, final Object value) {
        this.type = type;
        this.value = value;
    }
    
    TokenType getType() {
        return type;
    }
    
    Object getValue() {
        return value;
    }
}
