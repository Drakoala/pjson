package com.eps.pson;

import java.io.IOException;
import java.io.Writer;

/**
 * Defines the API for types that can be serialized 
 * as a JSON bean.
 * 
 * @since 0.1
 * @author David Vallee
 */
public interface JsonSerializable {
    
    /**
     * Writes the contents of this JSON bean.
     * 
     * @param out The write this bean shall be written to.
     * @throws IOException If an error occurs in writing.
     */
    void writeJson(Writer out) throws IOException;
}
