package com.eps.pson;

// Internal
// Used for passing around integers as a reference, as opposed to by value.
// Used to pass the lexer's index around when parsing.
final class IntRef {
    int value;
    
    IntRef(int value) {
        this.value = value;
    }
    
    IntRef(IntRef ref) {
        this(ref.value);
    }
}
