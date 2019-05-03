package utils;

import com.jogamp.opengl.GL2;

/**
 * @author Jack Hosking
 * studentID 16932920
 */

public class Colour {
    private float red = 1.0f;
    private float green = 1.0f;
    private float blue = 1.0f;
    private float alpha = 1.0f;

    public Colour() { }

    // construct rgba
    public Colour(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    // construct rgb
    public Colour(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public static void setColourRGBA(Colour colour, GL2 gl) {
        gl.glColor4f(colour.red, colour.green, colour.blue, colour.alpha);
    }

    public static void setDynamicColourRGBA(Colour colour, float transparency, GL2 gl) {
        gl.glColor4f(colour.red, colour.green, colour.blue, transparency);
    }

}