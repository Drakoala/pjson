package com.eps.pson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

// Internal
// Set of methods for parsing JSON values.
final class JsonParser {
    private static final Pattern PT_LONG = Pattern.compile("^[-+]?[0-9]+$");
    private static final Pattern PT_DOUBLE = Pattern.compile("^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$");
    
    private JsonParser() {}
    
    private static void clearWhitespace(String json, IntRef ref) {
        for(; ref.value < json.length(); ref.value++) {
            if(" \t\n\r".indexOf(json.charAt(ref.value)) == -1) {
                break;
            }
        }
    }
    
    private static TokenType getNextToken(String json, IntRef ref) {
        clearWhitespace(json, ref);
        if(ref.value == json.length()) {
            return TokenType.NONE;
        }
        char c = json.charAt(ref.value);
        ref.value++;
        switch(c) {
            case '{': return TokenType.BRACE_OPEN;
            case '}': return TokenType.BRACE_CLOSE;
            case '[': return TokenType.SQUARE_OPEN;
            case ']': return TokenType.SQUARE_CLOSE;
            case ',': return TokenType.COMMA;
            case '"': return TokenType.STRING;
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
            case '-': return TokenType.NUMBER;
            case ':': return TokenType.COLON;
        }
        ref.value--;
        if(hasToken(json, ref, "false")) {
            ref.value += 5;
            return TokenType.FALSE;
        }
        if(hasToken(json, ref, "true")) {
            ref.value += 4;
            return TokenType.TRUE;
        }
        if(hasToken(json, ref, "null")) {
            ref.value += 4;
            return TokenType.NULL;
        }
        return TokenType.NONE;
    }
    
    private static TokenType seekNextToken(String json, IntRef ref) {
        IntRef v = new IntRef(ref);
        return getNextToken(json, v);
    }
    
    private static ArrayList<Object> decodeArray(String json, IntRef ref) throws IOException {
        ArrayList<Object> list = new ArrayList<>();
        getNextToken(json, ref);
        boolean complete = false;
        while(!complete) {
            TokenType token = seekNextToken(json, ref);
            if(token == TokenType.NONE) {
                throw new IOException("Expected token, reached end of input");
            } else if(token == TokenType.COMMA) {
                getNextToken(json, ref);
            } else if(token == TokenType.SQUARE_CLOSE) {
                getNextToken(json, ref);
                break;
            } else {
                Object value = decodeValue(json, ref);
                list.add(value);
            }
        }
        return list;
    }
    
    private static String decodeString(String json, IntRef ref) throws IOException {
        StringBuilder s = new StringBuilder(1024);
        char c;
        clearWhitespace(json, ref);
        c = json.charAt(ref.value++);
        boolean complete = false;
        while(!complete) {
            if(ref.value == json.length()) {
                break;
            }
            c = json.charAt(ref.value++);
            if(c == '"') {
                complete = true;
                break;
            } else if(c == '\\') {
                if(ref.value == json.length()) {
                    break;
                }
                c = json.charAt(ref.value++);
                if(c == 'b') {
                    s.append('\b');
                } else if(c == 'f') {
                    s.append('\f');
                } else if(c == 'n') {
                    s.append('\n');
                } else if(c == 'r') {
                    s.append('\r');
                } else if(c == 't') {
                    s.append('\t');
                } else {
                    s.append(c);
                }
            } else {
                s.append(c);
            }
        }
        if(!complete) {
            throw new IOException("String parsing incomplete");
        }
        return s.toString();
    }
    
    private static Object decodeObject(String json, IntRef ref) throws IOException {
        JsonObject o = new JsonObject();
        TokenType token = getNextToken(json, ref);
        boolean complete = false;
        while(!complete) {
            token = seekNextToken(json, ref);
            if(token == TokenType.NONE) {
                System.err.println("Unexpected token " + json.charAt(ref.value));
                return null;
            } else if(token == TokenType.COMMA) {
                getNextToken(json, ref);
            } else if(token == TokenType.BRACE_CLOSE) {
                getNextToken(json, ref);
                return o;
            } else {
                String name = decodeString(json, ref);
                if(name == null) {
                    throw new IOException("Failed to parse key for object at " + ref.value);
                }
                token = getNextToken(json, ref);
                if(token != TokenType.COLON) {
                    throw new IOException("Expected " + TokenType.COLON.name() + ", got " + token.name());
                }
                Object value = decodeValue(json, ref);
                o.put(name, value);
            }
        }
        return o;
    }
    
    protected static Object decodeValue(String json, IntRef ref) throws IOException {
        TokenType token = seekNextToken(json, ref);
        switch(token) {
            case STRING: {
                return decodeString(json, ref);
            }
            case BRACE_OPEN: {
                return decodeObject(json, ref);
            }
            case SQUARE_OPEN: {
                return decodeArray(json, ref);
            }
            case NUMBER: {
                return decodeNumber(json, ref);
            }
            case TRUE: {
                getNextToken(json, ref);
                return true;
            }
            case FALSE: {
                getNextToken(json, ref);
                return false;
            }
            case NULL: {
                getNextToken(json, ref);
                return null;
            }
            case NONE:
            default: break;
        }
        return null;
    }
    
    private static Number decodeNumber(String json, IntRef ref) {
        clearWhitespace(json, ref);
        int lastIndex = getLastIndexOfNumber(json, ref);
        int len = (lastIndex - ref.value) + 1;
        String v = new String(json.toCharArray(), ref.value, len);
        Number value = null;
        if(PT_LONG.matcher(v).matches()) {
            value = Long.valueOf(Long.parseLong(v));
        } else if(PT_DOUBLE.matcher(v).matches()) {
            value = Double.valueOf(Double.parseDouble(v));
        }
        ref.value = lastIndex + 1;
        return value;
    }
    
    private static int getLastIndexOfNumber(String json, IntRef ref) {
        int i;
        for(i = ref.value; i < json.length(); i++) {
            if("0123456789+-.eE".indexOf(json.charAt(i)) == -1) {
                break;
            }
        }
        return i - 1;
    }
    
    private static boolean hasToken(String json, IntRef ref, String token) {
        int len = json.length() - ref.value;
        if(len >= token.length()) {
            for(int i = 0; i < token.length(); i++) {
                if(json.charAt(i) != token.charAt(i)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
