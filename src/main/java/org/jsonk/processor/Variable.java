package org.jsonk.processor;

import javax.lang.model.type.TypeMirror;

interface Variable {

    String name();

    TypeMirror type();

}
