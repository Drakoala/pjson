package com.eps.pson;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a JSON object, retains order of insertion.
 * Defines the API for encoding and decoding JSON, with 
 * support for "pretty printing" through {@link JsonWriter}.
 * 
 * @since 0.1
 * @author David Vallee
 * @see LinkedHashMap
 */
public class JsonObject extends LinkedHashMap<Object, Object> implements JsonSerializable {
    
    public void writeJson(Writer out) throws IOException {
        write(out, this);
    }
    
    protected static void write(Writer out, Map<?, ?> object) throws IOException {
        if(out == null) {
            throw new NullPointerException("out");
        }
        if(object == null) {
            out.write("null");
            return;
        }
        Iterator<?> iter = object.entrySet().iterator();
        boolean first = true;
        out.write('{');
        while(iter.hasNext()) {
            if(first) first = false;
            else {
                out.write(',');
            }
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>)iter.next();
            out.write('\"');
            out.write(escape(String.valueOf(entry.getKey())));
            out.write('\"');
            out.write(':');
            encode(out, entry.getValue());
        }
        out.write('}');
    }
    
    /**
     * Encodes the given value to a JSON-compatible string, using the 
     * given writer. If the given writer is an instance of {@link JsonWriter},
     * the JSON string is "pretty-ified".
     * 
     * @param out The {@link Writer} the JSON string will be written to.
     * @param value The value to be encoded into a JSON string.
     * @throws IOException If an error occurs while encoding.
     */
    public static void encode(Writer out, Object value) throws IOException {
        if(out == null) {
            throw new NullPointerException("out");
        }
        if(value instanceof String) {
            out.write('\"');
            out.write(escape((String)value));
            out.write('\"');
            return;
        } else if(value instanceof Double) {
            Double v = (Double)value;
            if(v.isInfinite() || v.isNaN()) {
                out.write("null");
            } else {
                out.write(v.toString());
            }
            return;
        } else if(value instanceof Float) {
            Float v = (Float)value;
            if(v.isInfinite() || v.isNaN()) {
                out.write("null");
            } else {
                out.write(v.toString());
            }
            return;
        } else if(value instanceof JsonSerializable) {
            ((JsonSerializable)value).writeJson(out);
            return;
        } else if(value instanceof Map) {
            write(out, (Map<?, ?>)value);
            return;
        } else if(value instanceof List) {
            JsonArray.write(out, (List<?>)value);
            return;
        } else if(value instanceof Object) {
            out.write('\"');
            out.write(escape(value.toString()));
            out.write('\"');
        } else {
            out.write(String.valueOf(value));
        }
    }
    
    /**
     * Returns a JSON string representation of this object.
     * 
     * @throws RuntimeException If an {@link IOException} occurs.
     */
    public String toString() {
        try {
            return encode(this);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    /**
     * @throws IOException If an {@link IOException} occurs.
     * @see toString()
     */
    public String toStringWithException() throws IOException {
        return encode(this);
    }
    
    private static void escape(StringBuffer out, String value) {
        if(out == null) {
            throw new NullPointerException("out");
        }
        for(int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch(ch) {
                case '"': out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\b': out.append("\\b"); break;
                case '\f': out.append("\\f"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if((ch >= '\u0000' && ch <= '\u001F')
                            || (ch >= '\u007F' && ch <= '\u009F')
                            || (ch >= '\u2000' && ch <= '\u20FF')) {
                        String ss = Integer.toHexString(ch);
                        out.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            out.append('0');
                        }
                        out.append(ss.toUpperCase());
                    } else {
                        out.append(ch);
                    }
            }
        }
    }
    
    private static String escape(String value) {
        if(value == null) {
            return null;
        }
        StringBuffer out = new StringBuffer();
        escape(out, value);
        return out.toString();
    }
    
    /**
     * Encodes the given value to a JSON-compatible (roughly speaking) string.
     * 
     * @param value The object to be encoded.
     * @return The JSON-comptible string.
     * @throws IOException If an error occurs while encoding.
     */
    public static String encode(Object value) throws IOException {
        StringWriter out = new StringWriter();
        encode(out, value);
        return out.toString();
    }
    
    /**
     * Encodes the given value to an indent-formatted JSON string.
     * 
     * @param value The object to be encoded.
     * @return The pretty JSON-comptible string.
     * @throws IOException If an error occurs while encoding.
     */
    public static String encodePretty(Object value) throws IOException {
        @SuppressWarnings("resource")
        JsonWriter out = new JsonWriter().setPrettyPrinting(true);
        encode(out, value);
        return out.toString();
    }
    
    /**
     * Decodes the given JSON string, attempting to convert it 
     * to the appropriate value. Appropriate values may be:
     * <br>
     * <li>Object - Represented by {@link JsonObject}, or a {@link HashMap}.</li>
     * <li>String - Represented by {@link String}.</li>
     * <li>Array - Represented by {@link JsonArray}, or {@link ArrayList}</li>
     * <li>Number - Represented by {@link Double}, so as not to lose precision.</li>
     * <li>Boolean - Represented by the {@code boolean} primitive type.</li>
     * <li>Null/Undefined - Represented by, surprise, {@code null}.</li>
     * <br>
     * 
     * @param json The JSON string to be decoded and converted to its respective value.
     * @return The respective value in code of the JSON string.
     * @throws IOException
     * @throws NullPointerException If the JSON string is null.
     */
    public static Object decode(String json) throws IOException {
        if(json == null) {
            throw new NullPointerException("json");
        }
        return JsonParser.decodeValue(json, new IntRef(0));
    }
}
