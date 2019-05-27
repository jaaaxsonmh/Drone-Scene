package utils;

import com.jogamp.opengl.GL2;

/**
 * @author Jack Hosking 
 * studentID 16932920
 * scale: 1 unit : 1 meter
 */

public class Colour {
    public float red = 1.0f;
    public float green = 1.0f;
    public float blue = 1.0f;
    public float alpha = 1.0f;

    public Colour() {
    }

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

    public Colour add(Colour rgb) {
        return new Colour(red + rgb.red, green + rgb.green, blue + rgb.blue);
    }

    public Colour subtract(Colour rgb) {
        return new Colour(red - rgb.red, green - rgb.green, blue - rgb.blue);
    }

    public Colour scale(float scale) {
        return new Colour(red * scale, green * scale, blue * scale);
    }

    public static void setColourRGBA(Colour colour, GL2 gl) {
        gl.glColor4f(colour.red, colour.green, colour.blue, colour.alpha);
    }

    public static void setDynamicColourRGBA(Colour colour, float transparency, GL2 gl) {
        gl.glColor4f(colour.red, colour.green, colour.blue, transparency);
    }

}