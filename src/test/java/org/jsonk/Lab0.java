package org.jsonk;


import java.util.Map;
import java.util.Random;

public class Lab0 {

    private static String getValue(String name) {
        return switch (name) {
            case "0" -> "Jsonk0";
            case "1" -> "Jsonk1";
            case "2" -> "Jsonk2";
            case "3" -> "Jsonk3";
            case "4" -> "Jsonk4";
            case "5" -> "Jsonk5";
            case "6" -> "Jsonk6";
            case "7" -> "Jsonk7";
            case "8" -> "Jsonk8";
            case "9" -> "Jsonk9";
            case "10" -> "Jsonk10";
            case "11" -> "Jsonk11";
            case "12" -> "Jsonk12";
            case "13" -> "Jsonk13";
            case "14" -> "Jsonk14";
            case "15" -> "Jsonk15";
            case "16" -> "Jsonk16";
            case "17" -> "Jsonk17";
            case "18" -> "Jsonk18";
            case "19" -> "Jsonk19";
            default -> "Jsonk";
        };
    }

    public static void main(String[] args) {
        run(100000);
        var start = System.currentTimeMillis();
        run(100000000);
        var elapsed = System.currentTimeMillis() - start;
        System.out.println(elapsed);
    }

    private static final Random rand = new Random();

    private static void run(int n) {
        for (int i = 0; i < n; i++) {
            var key = rand.nextInt(10) + "";
            getValue(key);
        }
    }



}
