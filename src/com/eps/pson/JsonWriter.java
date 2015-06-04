package com.eps.pson;

import java.io.StringWriter;

/**
 * A custom string writer which allows for "pretty printing" JSON strings.
 * As with {@link StringWriter}, closing this writer has no effect.
 * 
 * @since 0.1
 * @author David Vallee
 * @see StringWriter
 */
public final class JsonWriter extends StringWriter {
    private boolean pretty;
    private int index;
    
    /**
     * Initializes a JSON writer which, by default, 
     * does not "pretty print".
     */
    public JsonWriter() {
        pretty = false;
        index = 0;
    }
    
    private void writeIndentation() {
        if(!pretty) return;
        for(int i = 0; i < index; i++) {
            super.write("    ");
        }
    }
    
    public void write(int c) {
        if(pretty) {
            if(((char)c) == '[' || ((char)c) == '{') {
                super.write(c);
                super.write('\n');
                index++;
                writeIndentation();
            } else if (((char)c) == ',') {
                super.write(c);
                super.write('\n');
                writeIndentation();
            } else if (((char)c) == ']' || ((char)c) == '}') {
                super.write('\n');
                index--;
                writeIndentation();
                super.write(c);
            } else {
                super.write(c);
            }
        } else super.write(c);
    }
    
    /**
     * Sets whether or not "pretty printing" is enabled. If true,
     * JSON objects (arrays included), are indented to enhance 
     * readability.
     * 
     * @param prettyEnabled Enables "pretty printing" if true.
     * @return This JSON writer.
     */
    public JsonWriter setPrettyPrinting(boolean prettyEnabled) {
        this.pretty = prettyEnabled;
        return this;
    }
    
    /**
     * Gets whether or not "pretty printing" is enabled.
     * 
     * @return True if "pretty printing" is enabled.
     */
    public boolean isPrettyPrintingEnabled() {
        return pretty;
    }
}
