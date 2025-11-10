package org.jsonk.processor;

import javax.tools.Diagnostic;

public enum DiagnosticKind {
    INFO,
    WARNING,
    ERROR,

    ;

    Diagnostic.Kind toKind() {
        return switch (this) {
            case INFO -> Diagnostic.Kind.NOTE;
            case WARNING -> Diagnostic.Kind.WARNING;
            case ERROR -> Diagnostic.Kind.ERROR;
        };
    }
}
