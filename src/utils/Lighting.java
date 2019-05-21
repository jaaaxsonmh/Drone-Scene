package utils;

import com.jogamp.opengl.GL2;

/**
 * @author Jack Hosking studentID 16932920 scale: 1 unit : 1 meter
 */
public class Lighting {

    private int droneSpotlight = GL2.GL_LIGHT3;
    private float timeCycle = 0;
    //Change clock time to a larger value to decrease the day/night cycle.
    private float clockTime = 0.0005f;

    public Lighting(GL2 gl) {
        gl.glLightf(droneSpotlight, GL2.GL_SPOT_CUTOFF, 90);
        float[] droneSpotLightAxis = {0, -0.2f, 0};
        gl.glLightfv(droneSpotlight, GL2.GL_SPOT_DIRECTION, droneSpotLightAxis, 0);
        gl.glLightf(droneSpotlight, GL2.GL_SPOT_EXPONENT, 5);
        gl.glLightfv(droneSpotlight, GL2.GL_DIFFUSE, new float[]{0.7f, 0.7f, 0.7f, 1.0f}, 0);
        gl.glLightfv(droneSpotlight, GL2.GL_SPECULAR, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 0);
        gl.glLightfv(droneSpotlight, GL2.GL_AMBIENT, new float[]{0.7f, 0.7f, 0.7f, 0.7f}, 0);
        gl.glLightfv(droneSpotlight, GL2.GL_EMISSION, new float[]{1, 1, 1, 1}, 0);
        gl.glEnable(droneSpotlight);
    }

    public void drawDroneSpotlight(GL2 gl, float[] spotLightPosition) {
        gl.glLightfv(droneSpotlight, GL2.GL_POSITION, spotLightPosition, 0);
        gl.glEnable(droneSpotlight);
    }

    public void setSceneLighting(GL2 gl) {

        float ambient[] = {timeCycle, timeCycle, timeCycle, 1.0f};
        float diffuse[] = {timeCycle, timeCycle, timeCycle, 1.0f};
        float specular[] = {0.5f, 0.5f, 0.5f, 1.0f};
        float direction[] = {0, 1, 0};

        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPOT_DIRECTION, direction, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, specular, 0);

        // lets use use standard color functions
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glEnable(GL2.GL_NORMALIZE);
    }

    public void animate() {
        // night cycle is longer as once it hits 0, it goes for 0.4f more (to 0.2 and back)
        if (timeCycle > 0.6 || timeCycle < -0.2f) {
            clockTime *= -1;
        }
//        System.out.println("Time Cycle: "+ timeCycle);
        timeCycle += clockTime;
    }
}
