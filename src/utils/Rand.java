package utils;

import java.util.Random;

/**
 * @author Jack Hosking
 * studentID 16932920
 */

public class Rand {

    private Rand() {

    }

    private final static Random rand = new Random();

    public static float getFloatBetween(float min, float max) {
        return rand.nextFloat() * (max - min) + min;
    }
}