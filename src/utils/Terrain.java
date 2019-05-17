package utils;

/**
 * @author Jack Hosking 
 * studentID 16932920
 * scale: 1 unit : 1 meter
 */

public interface Terrain {
    public double getAlitude(double i, double j);
    public Colour getColour (double i, double j);
}
