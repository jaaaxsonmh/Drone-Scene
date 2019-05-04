package utils;

import com.jogamp.opengl.GL2;

public class Lighting {
    private int droneSpotlight = GL2.GL_LIGHT3;

    public Lighting(GL2 gl){
        gl.glLightf(droneSpotlight, GL2.GL_SPOT_CUTOFF, 50);
        float[] droneSpotLightAxis = {0, -0.2f, 0};
        gl.glLightfv(droneSpotlight, GL2.GL_SPOT_DIRECTION, droneSpotLightAxis, 0);
        gl.glLightf(droneSpotlight, GL2.GL_SPOT_EXPONENT, 5);
        gl.glLightfv(droneSpotlight, GL2.GL_DIFFUSE, new float[]{0.5f, 0.5f, 0.5f, 1}, 0);
        gl.glLightfv(droneSpotlight, GL2.GL_SPECULAR, new float[]{1, 1, 1, 1}, 0);
        gl.glLightfv(droneSpotlight, GL2.GL_AMBIENT, new float[]{0.5f,0.5f,0.5f,0.5f}, 0);
        gl.glLightfv(droneSpotlight, GL2.GL_EMISSION, new float[]{1,1,1,1}, 0);
        gl.glEnable(droneSpotlight);
    }

    public void drawDroneSpotlight(GL2 gl, float[] spotLightPosition) {
        gl.glLightfv(droneSpotlight, GL2.GL_POSITION, spotLightPosition, 0);

        //If the spotLight is higher than 4 meters off the ground, then disable the lighting
        if(spotLightPosition[1] < 4) {
            gl.glEnable(droneSpotlight);
        } else {
            gl.glDisable(droneSpotlight);
        }
    }

    public void setSceneLighting(GL2 gl) {
        float ambient[] = {0, 0, 0, 1};
        float diffuse[] = {1f, 1f, 1f, 1};
        float specular[] = {1, 1, 1, 1};

        float[] ambientLight = {0.1f, 0.1f, 0.1f, 0f}; // weak RED ambient
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_AMBIENT, ambientLight, 0);

        float position0[] = {5, 5, 5, 0};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position0, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specular, 0);

        float position1[] = {-10, -10, -10, 0};
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, position1, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, specular, 0);

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_LIGHT1);

        // lets use use standard color functions
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        // normalise the surface normals for lighting calculations
        gl.glEnable(GL2.GL_NORMALIZE);
    }
}
