package utils;

import com.jogamp.opengl.GL2;

/**
 * @author Jack Hosking studentID 16932920 scale: 1 unit : 1 meter
 */
public class Lighting {

    private int droneSpotlight = GL2.GL_LIGHT3;
    private float sceneDiffuse[] = {1.0f, 1.0f, 1.0f, 1};
    private float sceneAmbient[] = {0, 0, 0, 1};
    private float sceneSpecular[] = {0.3f, 0.3f, 0.3f, 1};

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

        float position0[] = {0, 10, 0, 0};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position0, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, sceneAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, sceneDiffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, sceneSpecular, 0);

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        // lets use use standard color functions
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glEnable(GL2.GL_NORMALIZE);

    }

    public void setSunLighting(GL2 gl, float time) {
        float ambient[] = {time, time, time, 1.0f};
        float diffuse[] = {time, time, time, 1.0f};
        float specular[] = {0.5f, 0.5f, 0.5f, 1.0f};

        float position[] = {0, 10, 0, 0};
        float direction[] = {0, 1, 0};

        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, position, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPOT_DIRECTION, direction, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, specular, 0);
    }
    
    public void controlLightCycle(GL2 gl, float time){
            if(time < 0) { time = 0; }
            setSunLighting(gl, time);
            gl.glEnable(GL2.GL_LIGHT1);   
    }
}
