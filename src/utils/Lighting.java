package utils;

import com.jogamp.opengl.GL2;

public class Lighting {

    private int droneSpotlight = GL2.GL_LIGHT3;
    private float sceneDiffuse[] = {1f, 1f, 1f, 1};
    private float sceneAmbient[] = {0, 0, 0, 1};
    private float sceneSpecular[] = {1, 1, 1, 1};

    public Lighting(GL2 gl) {
        gl.glLightf(droneSpotlight, GL2.GL_SPOT_CUTOFF, 90);
        float[] droneSpotLightAxis = {0, -2.0f, 0};
        gl.glLightfv(droneSpotlight, GL2.GL_SPOT_DIRECTION, droneSpotLightAxis, 0);
        gl.glLightf(droneSpotlight, GL2.GL_SPOT_EXPONENT, 10);
        gl.glLightfv(droneSpotlight, GL2.GL_DIFFUSE, sceneDiffuse, 0);
        gl.glLightfv(droneSpotlight, GL2.GL_SPECULAR, sceneSpecular, 0);
        gl.glLightfv(droneSpotlight, GL2.GL_AMBIENT, sceneAmbient, 0);
        gl.glLightfv(droneSpotlight, GL2.GL_EMISSION, new float[]{1, 1, 1, 1}, 0);
        gl.glEnable(droneSpotlight);
    }

    public void drawDroneSpotlight(GL2 gl, float[] spotLightPosition) {
        gl.glLightfv(droneSpotlight, GL2.GL_POSITION, spotLightPosition, 0);
        gl.glEnable(droneSpotlight);
    }

    public void setSceneLighting(GL2 gl) {

        float[] ambientLight = {0.1f, 0.1f, 0.1f, 0f}; // weak RED ambient
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_AMBIENT, ambientLight, 0);

        float position0[] = {5, 5, 5, 0};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position0, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, sceneAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, sceneDiffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, sceneSpecular, 0);

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);

        // lets use use standard color functions
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        // normalise the surface normals for lighting calculations
        gl.glEnable(GL2.GL_NORMALIZE);
    }
}
