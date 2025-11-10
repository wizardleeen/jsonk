package org.jsonk;


import java.util.Map;
import java.util.Random;

public class Lab {

    private static final Map<String, String> map = Map.ofEntries(
            Map.entry("0", "Jsonk0"),
            Map.entry("1", "Jsonk1"),
            Map.entry("2", "Jsonk2"),
            Map.entry("3", "Jsonk3"),
            Map.entry("4", "Jsonk4"),
            Map.entry("5", "Jsonk5"),
            Map.entry("6", "Jsonk6"),
            Map.entry("7", "Jsonk7"),
            Map.entry("8", "Jsonk8"),
            Map.entry("9", "Jsonk9"),
            Map.entry("10", "Jsonk10"),
            Map.entry("11", "Jsonk11"),
            Map.entry("12", "Jsonk12"),
            Map.entry("13", "Jsonk13"),
            Map.entry("14", "Jsonk14"),
            Map.entry("15", "Jsonk15"),
            Map.entry("16", "Jsonk16"),
            Map.entry("17", "Jsonk17"),
            Map.entry("18", "Jsonk18"),
            Map.entry("19", "Jsonk19")
    );

    private static String getValue(String name) {
        return map.getOrDefault(name, "Jsonk");
    }

    public static void main(String[] args) {
        System.out.println(0x1F);
    }

    private static final Random rand = new Random();

    private static void run(int n) {
        for (int i = 0; i < n; i++) {
            var key = rand.nextInt(10) + "";
            getValue(key);
        }
    }



}
