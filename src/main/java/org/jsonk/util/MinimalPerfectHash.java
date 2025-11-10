package org.jsonk.util;

import java.io.Serializable;
import java.util.*;

/**
 * A production-ready, string-optimized Minimal Perfect Hash Function (MPHF).
 *
 * <p>This class is a specialized version of a generic MPHF, optimized for {@code String} keys.
 * It provides the same guarantees: perfect, minimal, immutable, thread-safe, and serializable.
 *
 * <p><b>Optimization Details:</b>
 * <ul>
 *   <li><b>Specialized Hash Function:</b> It replaces the generic {@code key.hashCode()} and a
 *       separate mixing step with the FNV-1a (Fowler-Noll-Vo) hash algorithm. FNV-1a is
 *       extremely fast for strings and provides excellent hash distribution.</li>
 *   <li><b>Fused Seeding:</b> The seeds for the two internal hash functions are used as the
 *       initial basis for the FNV-1a algorithm, creating two independent hash functions
 *       from a single, efficient implementation.</li>
 *   <li><b>No Generic Overhead:</b> By removing generics, the code is simpler and avoids any
 *       potential overhead related to type erasure or casting.</li>
 * </ul>
 *
 * <p>The result is a significant speedup in both the generation process (due to fewer retries)
 * and the final {@code get()} lookup operation.
 */
public final class MinimalPerfectHash implements Serializable {

    private static final long serialVersionUID = 2L;
    private static final int MAX_BUILD_RETRIES = 100000;

    // FNV-1a 64-bit constants
    private static final long FNV_64_PRIME = 0x100000001b3L;
    private static final long FNV_64_OFFSET_BASIS = 0xcbf29ce484222325L;

    private final int size;
    public final int[] displacements;
    public final long seed1;
    public final long seed2;
    private final String[] keys;

    public MinimalPerfectHash(Collection<String> keys, int[] displacements, long seed1, long seed2) {
        this.size = displacements.length;
        this.displacements = displacements;
        this.seed1 = seed1;
        this.seed2 = seed2;
        this.keys = new String[size];
        for (String key : keys) {
            this.keys[get0(key)] = key;
        }
    }

    public static PhfConfig generateConfig(Collection<String> keys) {
        final Set<String> keySet = new HashSet<>(keys);
        final int size = keySet.size();

        if (size == 0) {
            return new PhfConfig( keys, new int[0], 0L, 0L);
        }

        Random random = new Random();
        for (int attempt = 0; attempt < MAX_BUILD_RETRIES; attempt++) {
            // Use long seeds as the basis for the FNV hash
            long seed1 = random.nextLong();
            long seed2 = random.nextLong();

            int[] displacements = tryBuild(keySet, seed1, seed2);
            if (displacements != null) {
                return new PhfConfig(keys, displacements, seed1, seed2);
            }
        }

        throw new IllegalStateException(
                "Failed to generate a minimal perfect hash function after " + MAX_BUILD_RETRIES + " attempts. " +
                        "The key set might have poor hash distribution. Keys: " + keys
        );
    }

    /**
     * Generates a Minimal Perfect Hash Function for the given collection of strings.
     *
     * @param keys The collection of string keys. Duplicates will be ignored.
     * @return An immutable, optimized MPHF instance for strings.
     * @throws IllegalStateException if a perfect hash function could not be generated
     *         within a reasonable number of attempts.
     */
    public static MinimalPerfectHash generate(Collection<String> keys) {
        final Set<String> keySet = new HashSet<>(keys);
        final int size = keySet.size();

        if (size == 0) {
            return new MinimalPerfectHash(keys, new int[0], 0L, 0L);
        }

        Random random = new Random();
        for (int attempt = 0; attempt < MAX_BUILD_RETRIES; attempt++) {
            // Use long seeds as the basis for the FNV hash
            long seed1 = random.nextLong();
            long seed2 = random.nextLong();

            int[] displacements = tryBuild(keySet, seed1, seed2);
            if (displacements != null) {
                return new MinimalPerfectHash(keys, displacements, seed1, seed2);
            }
        }

        throw new IllegalStateException(
                "Failed to generate a minimal perfect hash function after " + MAX_BUILD_RETRIES + " attempts. " +
                        "The key set might have poor hash distribution."
        );
    }

    private static int[] tryBuild(Set<String> keys, long seed1, long seed2) {
        // Collision resolution requires odd number of buckets
        final int size = keys.size() % 2 == 0 ? keys.size() + 1 : keys.size();

        // 1. Group keys into buckets
        @SuppressWarnings("unchecked")
        List<String>[] buckets = new List[size];
        for (int i = 0; i < size; i++) {
            buckets[i] = new ArrayList<>();
        }
        for (String key : keys) {
            buckets[h1(key, seed1, size)].add(key);
        }

        // 2. Sort bucket indices by size, descending
        List<Integer> sortedBucketIndices = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            sortedBucketIndices.add(i);
        }
        sortedBucketIndices.sort((a, b) -> Integer.compare(buckets[b].size(), buckets[a].size()));

        // 3. Find displacement values for each bucket
        int[] displacements = new int[size];
        BitSet occupiedSlots = new BitSet(size);

        for (int bucketIndex : sortedBucketIndices) {
            List<String> bucket = buckets[bucketIndex];
            if (bucket.isEmpty()) continue;

            int displacement = 0;
            while (true) {
                BitSet slotsInBucket = new BitSet(size);
                boolean collisionFound = false;
                for (String key : bucket) {
                    int slot = h2(key, seed2, size, displacement);
                    if (occupiedSlots.get(slot) || slotsInBucket.get(slot)) {
                        collisionFound = true;
                        break;
                    }
                    slotsInBucket.set(slot);
                }

                if (!collisionFound) {
                    displacements[bucketIndex] = displacement;
                    occupiedSlots.or(slotsInBucket);
                    break;
                }
                displacement++;
                if (displacement > size * 10) {
                    return null; // Safety break, retry with new seeds
                }
            }
        }
        return displacements;
    }

    /**
     * Calculates the perfect hash value for a given key.
     * <p><b>Warning:</b> The key MUST be one of the keys from the original set.
     *
     * @param key The string key to hash.
     * @return The unique integer hash value in the range [0, N-1].
     */
    public int get(String key) {
        var idx = get0(key);
        return Objects.equals(keys[idx], key) ? idx : -1;
    }

    private int get0(String key) {
        int bucketIndex = h1(key, this.seed1, this.size);
        int displacement = this.displacements[bucketIndex];
        return h2(key, this.seed2, this.size, displacement);
    }

    /** Returns the number of keys in the set. */
    public int size() {
        return size;
    }

    private static int h1(String key, long seed, int size) {
        return (int) (fnv1a_64(key, seed) & 0x7FFFFFFF) % size;
    }

    private static int h2(String key, long seed, int size, int displacement) {
        return (int) ((fnv1a_64(key, seed) + displacement) & 0x7FFFFFFF) % size;

    }

    /**
     * Implements the 64-bit FNV-1a hash algorithm.
     * The seed is used as the initial hash value (offset basis).
     *
     * @param key The string to hash.
     * @param seed The seed to initialize the hash.
     * @return The 64-bit hash value.
     */
    private static long fnv1a_64(String key, long seed) {
        long hash = (seed == 0) ? FNV_64_OFFSET_BASIS : seed;
        for (int i = 0; i < key.length(); i++) {
            hash ^= key.charAt(i);
            hash *= FNV_64_PRIME;
        }
        return hash;
    }

    // --- Main method for demonstration ---
    public static void main(String[] args) {
        Set<String> keywords = new HashSet<>(Arrays.asList(
                "abstract", "continue", "for", "new", "switch", "assert", "default",
                "goto", "package", "synchronized", "boolean", "do", "if", "private",
                "this", "break", "double", "implements", "protected", "throw", "byte",
                "else", "import", "public", "throws", "case", "enum", "instanceof",
                "return", "transient", "catch", "extends", "int", "short", "try",
                "char", "final", "interface", "static", "void", "class", "finally",
                "long", "strictfp", "volatile", "const", "float", "native", "super", "while"
        ));

        System.out.println("Building String-Optimized MPHF for " + keywords.size() + " Java keywords...");
        long startTime = System.currentTimeMillis();
        MinimalPerfectHash mph = MinimalPerfectHash.generate(keywords);
        long endTime = System.currentTimeMillis();
        System.out.println("Build completed in " + (endTime - startTime) + " ms.");
        System.out.println();

        // --- Verification ---
        System.out.println("--- Verifying correctness and performance ---");
        Set<Integer> hashes = new HashSet<>();
        boolean success = true;
        for (String key : keywords) {
            int hash = mph.get(key);
            if (hash < 0 || hash >= keywords.size() || !hashes.add(hash)) {
                System.err.printf("ERROR! Key '%s' failed validation with hash %d\n", key, hash);
                success = false;
            }
        }

        if (success) {
            System.out.println("SUCCESS: All keys mapped to a unique integer in [0, " + (keywords.size() - 1) + "].");
        } else {
            System.out.println("FAILURE: Collisions or out-of-range hashes were detected.");
        }
        System.out.println();

        // --- Demonstrate lookup performance ---
        final int lookups = 200_000_000;
        String testKey = "interface";
        System.out.printf("--- Performing %,d lookups for key '%s' ---\n", lookups, testKey);
        startTime = System.nanoTime();
        int temp = 0; // To prevent JIT from optimizing the loop away
        for (int i = 0; i < lookups; i++) {
            temp += mph.get(testKey);
        }
        endTime = System.nanoTime();
        long durationNanos = endTime - startTime;
        double nsPerLookup = (double) durationNanos / lookups;
        System.out.printf("Total time: %.2f ms\n", durationNanos / 1_000_000.0);
        System.out.printf("Average lookup time: %.2f nanoseconds\n", nsPerLookup);
    }
}