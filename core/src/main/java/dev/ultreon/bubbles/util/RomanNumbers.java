package dev.ultreon.bubbles.util;

import java.math.BigInteger;
import java.util.TreeMap;

public class RomanNumbers {
    private static final TreeMap<Integer, String> map = new TreeMap<>();

    public static String toRoman(int number) {
        if (number >= 40000000 || number < 0) {
            return String.valueOf(number);
        }

        if (number == 0) {
            return "-";
        }

        int l = map.floorKey(number);
        if (number == l) {
            return map.get(number);
        }
        return map.get(l) + RomanNumbers.toRoman(number - l);
    }

    public static String toRoman(long number) {
        return RomanNumbers.toRoman((int) number);
    }

    public static String toRoman(short number) {
        return RomanNumbers.toRoman((int) number);
    }

    public static String toRoman(byte number) {
        return RomanNumbers.toRoman((int) number);
    }

    public static String toRoman(BigInteger number) {
        return RomanNumbers.toRoman(number.longValue());
    }

    static {
        map.put(10000000, "N");
        map.put(9000000, "KN");
        map.put(5000000, "O");
        map.put(4000000, "KO");
        map.put(1000000, "K");
        map.put(900000, "PK");
        map.put(500000, "S");
        map.put(400000, "PS");
        map.put(100000, "P");
        map.put(90000, "QP");
        map.put(50000, "R");
        map.put(40000, "QR");
        map.put(10000, "Q");
        map.put(9000, "MQ");
        map.put(5000, "T");
        map.put(4000, "MT");
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");
    }
}

