package com.starling.starlingroundup.utils;

public class Math {

    public static long roundLongToNearest100(long number) {
        return (number / 100 + 1) * 100;
    }
}