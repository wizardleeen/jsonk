package org.jsonk.util;

import java.util.Collection;

public record PhfConfig(
    Collection<String> keys,
    int[] displacements,
    long seed1,
    long seed2
) {

    public MinimalPerfectHash createPhf() {
        return new MinimalPerfectHash(keys, displacements, seed1, seed2);
    }

}
