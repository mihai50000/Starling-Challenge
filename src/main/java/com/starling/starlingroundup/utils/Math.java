package com.starling.starlingroundup.utils;

public class Math {

    public static long roundLongToNext100(long number) {
        return number - number % 100 + 100;
    }
}