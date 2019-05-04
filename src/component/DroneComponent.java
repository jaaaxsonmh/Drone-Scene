package component;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import utils.Drawable;

import java.util.LinkedList;

/**
 * @author Jack Hosking
 * studentID 16932920
 */

public abstract class DroneComponent implements Drawable {

    private LinkedList<DroneComponent> children;
    protected double radius;
    protected double height;
    private Axis axis;
    private double rotation, transX, transY, transZ;
    protected double[] eqn;


    public DroneComponent(double radius, double height, Axis axis) {
        children = new LinkedList<>();
        this.radius = radius;
        this.height = height;
        this.axis = axis;
    }

    @Override
    public void draw(GL2 gl, GLU glu, GLUquadric quadric, boolean filled) {
        gl.glPushMatrix();

        transformNode(gl);
        drawNode(gl, glu, quadric, filled);
        // draw each child
        for (DroneComponent child : children) {
            child.draw(gl, glu, quadric, filled);
        }

        gl.glPopMatrix();
    }

    public void setRotation(double rotation){
        this.rotation = rotation;
    }

    public void addChild(DroneComponent child) {
        children.add(child);
    }

    private void transformNode(GL2 gl) {
        // do the translation relative to the parent and then rotate and move nodes
        gl.glTranslated(transX, transY, transZ);

        if(this.axis == Axis.X) {
            gl.glRotated(rotation, 1, 0, 0);
        } else if (this.axis == Axis.Y){
            gl.glRotated(rotation, 0, 1, 0);
        } else if (this.axis == Axis.Z) {
            gl.glRotated(rotation, 0, 0, 1);
        }
    }

    public void setTranslations(double x, double y, double z) {
        transX = x;
        transY = y;
        transZ = z;
    }




    public void setEqn(double[] eqn) {
        this.eqn = eqn;
    }

    public abstract void drawNode(GL2 gl, GLU glu, GLUquadric glUquadric, boolean filled);

}
