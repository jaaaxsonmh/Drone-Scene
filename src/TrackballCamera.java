import com.jogamp.opengl.GL2;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import objects.Drone;

import java.awt.*;
import java.awt.event.*;

/**
 * @author Jack Hosking
 * studentID 16932920
 */

public class TrackballCamera implements MouseWheelListener {

    // some hard limitations to camera values
    private static final double MIN_DISTANCE = 1;
    private static final double MIN_FOV = 1;
    private static final double MAX_FOV = 80;

    // the point to look at
    private double lookAt[] = {0, 0, 0};

    // the camera rotation angles
    private double angleX = 45;
    private double angleY = 15;

    //Camera Positions
    private double camX;
    private double camY;
    private double camZ;

    // old mouse position for dragging

    // camera parameters
    private double fieldOfView      = 45;
    private double distanceToOrigin = 5;
    private double windowWidth      = 1;
    private double windowHeight     = 1;

    // GLU context
    private GLU glu = new GLU();


    /**
     * Constructor of the trackball camera
     * @param canvas the GL drawable context to register this camera with
     */

    TrackballCamera(GLCanvas canvas) {
        canvas.addMouseWheelListener(this);
    }

    /**
     * "Draws" the camera.
     * This sets up the projection matrix and
     * the camera position and orientation.
     * This method has to be called first thing
     * in the <code>display()</code> method
     * of the main program
     *
     * @param gl then OpenGL context to draw the camera in
     */
    public void draw(GL2 gl) {
        // set up projection first
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        // setting up perspective projection
        // far distance is hardcoded to 3*cameraDistance. If your scene is bigger,
        // you might need to adapt this
        glu.gluPerspective(fieldOfView, windowWidth / windowHeight, 0.1, distanceToOrigin * 3);

        // then set up the camera position and orientation
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        glu.gluLookAt(
                getX(), getY(), getZ(),                // eye
                lookAt[0], lookAt[1], lookAt[2], // center
                0, 1, 0);

        System.out.println(camX);
    }

    private double getZ() {
        return camZ;
    }

    private double getY() {
        return camY;
    }

    private double getX() {
        return camX;
    }


    /**
     * Gets the distance of the camera from the lookAt point
     * @return the distance of the camera from the lookAt point
     */
    double getDistance() {
        return distanceToOrigin;
    }

    /**
     * Sets the distance of the camera to the lookAt point.
     * @param dist the new distance of the camera to the lookAt point
     */
    void setDistance(double dist) {
        distanceToOrigin = dist;
        limitDistance();
    }

    /**
     * Limits the distance of the camera to valid values.
     */
    private void limitDistance() {
        if (distanceToOrigin < MIN_DISTANCE) {
            distanceToOrigin = MIN_DISTANCE;
        }
    }

    /**
     * Gets the field of view angle of the camera
     * @return the field of view of the camera in degrees
     */
    double getFieldOfView() {
        return fieldOfView;
    }

    /**
     * Sets the field of view angle of the camera.
     * @param fov the new field of view angle of the camera in degrees
     */
    void setFieldOfView(double fov) {
        fieldOfView = fov;
        limitFieldOfView();
    }

    /**
     * Limits the field of view angle to a valid range.
     */
    private void limitFieldOfView() {
        if (fieldOfView < MIN_FOV) {
            fieldOfView = MIN_FOV;
        }
        if (fieldOfView > MAX_FOV) {
            fieldOfView = MAX_FOV;
        }
    }

    /**
     * Sets up the lookAt point
     * @param x X coordinate of the lookAt point
     * @param y Y coordinate of the lookAt point
     * @param z Z coordinate of the lookAt point
     */
    void setLookAt(double x, double y, double z) {
        lookAt = new double[]{x, y, z};
    }

    void setCamX(double camX){
        this.camX = camX;
    }

    void setCamZ(double camZ) {
        this.camZ = camZ;
    }

    void setCamY(double camY){
        this.camY = camY;
    }

    /**
     * Passes a new window size to the camera.
     * This method should be called from the <code>reshape()</code> method
     * of the main program.
     *
     * @param width the new window width in pixels
     * @param height the new window height in pixels
     */
    public void newWindowSize(int width, int height) {
        windowWidth = Math.max(1.0, width);
        windowHeight = Math.max(1.0, height);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int clicks = e.getWheelRotation();
        // zoom using the FoV
        while (clicks > 0) {
            fieldOfView *= 1.1;
            clicks--;
        }
        while (clicks < 0) {
            fieldOfView /= 1.1;
            clicks++;
        }
        limitFieldOfView();
    }


}