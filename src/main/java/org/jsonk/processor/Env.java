package org.jsonk.processor;

import lombok.Setter;
import org.jsonk.util.Util;

import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Env {
    private final List<Diag> diags = new ArrayList<>();
    @Setter
    private Element currentElement;

    public void addError(String message) {
        diags.add(Diag.error(message, currentElement));
    }

    public List<Diag> getDiags() {
        return Collections.unmodifiableList(diags);
    }

    public boolean hasError() {
        return Util.anyMatch(diags, d -> d.kind() == DiagnosticKind.ERROR);
    }

    public void clearDiags() {
        diags.clear();
    }

}
