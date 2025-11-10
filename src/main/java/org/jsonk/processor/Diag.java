package org.jsonk.processor;

import javax.lang.model.element.Element;

public record Diag(DiagnosticKind kind, String message, Element element) {

    public static Diag error(String message, Element element) {
        return new Diag(DiagnosticKind.ERROR, message, element);
    }

}
