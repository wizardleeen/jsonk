package org.jsonk.util;

import lombok.Getter;

import java.util.*;

public class PerfectHashTable {

    private static final double LOAD_FACTOR = 0.5;
    private static final int MAX_BUILD_RETRIES = 100000;
    private static final long FNV_64_PRIME = 0x100000001b3L;
    private static final long FNV_64_OFFSET_BASIS = 0xcbf29ce484222325L;

    private final String[] table;
    private final int[] ordinals;
    @Getter
    private final long seed;
    private final int mask;

    private PerfectHashTable(String[] table, int[] ordinals, long seed, int mask) {
        this.table = table;
        this.ordinals = ordinals;
        this.seed = seed;
        this.mask = mask;
    }

    public static PerfectHashTable generate(List<String> keys) {
        var minSize = keys.size() / LOAD_FACTOR;
        var tableSize = 1;
        while (tableSize < minSize)
            tableSize <<= 1;
        var rand = new Random();
        for (int i = 0; i < MAX_BUILD_RETRIES; i++) {
            var seed = rand.nextLong();
            var table = tryBuild(keys, seed, tableSize);
            if (table != null) {
                var ordinalMap = new HashMap<String, Integer>();
                var k = 0;
                for (String key : keys) {
                    ordinalMap.put(key, k++);
                }
                var ordinals = new int[table.length];
                for (int j = 0; j < table.length; j++) {
                    var key = table[j];
                    if (key != null)
                        ordinals[j] = ordinalMap.get(key);
                    else
                        ordinals[j] = -1;
                }
                return new PerfectHashTable(table, ordinals, seed, tableSize - 1);
            }
        }
        throw new IllegalStateException("Failed to generate PerfectHashTable for: " + keys);
    }

    public int get(String key) {
        var index = index(hash(key, seed), mask);
        var expected = table[index];
        return Objects.equals(expected, key) ? ordinals[index] : -1;
    }

    private static String[] tryBuild(List<String> keys, long seed, int tableSize) {
        var table = new String[tableSize];
        for (String key : keys) {
            var index = index(hash(key, seed), tableSize - 1);
            if (table[index] != null)
                return null;
            table[index] = key;
        }
        return table;
    }

    public static long hash(String key, long seed) {
        var h = seed == 0 ? FNV_64_OFFSET_BASIS : seed;
        for (int i = 0; i < key.length(); i++) {
            h = (h ^ key.charAt(i)) * FNV_64_PRIME;
        }
        return h;
    }

    public static int index(long hash, int mask) {
        var h = (int) hash ^ (int) hash >>> 16;
        return h & mask;
    }

    public String[] getTable() {
        return Arrays.copyOf(table, table.length);
    }

    public int[] getOrdinals() {
        return Arrays.copyOf(ordinals, ordinals.length);
    }

    public char[][] keyCharArrays() {
        return Arrays.stream(table).map(s -> s != null ? s.toCharArray() : null).toArray(char[][]::new);
    }

}
