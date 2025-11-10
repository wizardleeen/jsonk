package org.jsonk;

public class Option {

    static final Option DEFAULT = new Option();

    boolean indent;

    public static Option create() {
        return new Option();
    }

    public Option indent() {
        this.indent = true;
        return this;
    }

}
