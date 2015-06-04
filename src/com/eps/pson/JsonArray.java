package com.eps.pson;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a JSON array of any type.
 * 
 * @since 0.1
 * @author David Vallee
 * @see ArrayList
 */
public class JsonArray extends ArrayList<Object> implements JsonSerializable {
    
    /**
     * Initializes an empty JSON array with the given initial size.
     * 
     * @param size The initial size of this array.
     */
    public JsonArray(int size) {
        super(size);
    }
    
    /**
     * Initializes an array with the given elements.
     * 
     * @param array The initial set of elements.
     */
    public JsonArray(final Object[] array) {
        super(array.length);
        for(int i = 0; i < array.length; i++) {
            add(array[i]);
        }
    }
    
    /**
     * Initializes an empty JSON array with an initial size of 8.
     */
    public JsonArray() {
        super(8);
    }
    
    public void writeJson(Writer out) throws IOException {
        write(out, this);
    }
    
    protected static void write(Writer out, List<?> value) throws IOException {
        if(out == null) {
            throw new NullPointerException("out");
        }
        if(value == null) {
            out.write("null");
            return;
        }
        Iterator<?> iter = value.iterator();
        boolean first = true;
        out.write('[');
        while(iter.hasNext()) {
            if(first) {
                first = false;
            } else {
                out.write(',');
            }
            Object v = iter.next();
            if(v == null) {
                out.write("null");
                continue;
            }
            JsonObject.encode(out, v);
        }
        out.write(']');
    }
}
