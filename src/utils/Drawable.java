package utils;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

/**
 * @author Jack Hosking 
 * studentID 16932920
 * scale: 1 unit : 1 meter
 */

public interface Drawable {
    void draw(GL2 gl, GLU glu, GLUquadric quadric, boolean filled);

}