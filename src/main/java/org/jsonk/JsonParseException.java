package org.jsonk;

public class JsonParseException extends RuntimeException {

    public JsonParseException(String error, int line, int column) {
        super(error + ". line: " + line + ", column: " + column);
    }
}
