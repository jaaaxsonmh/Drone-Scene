package utils;

import com.jogamp.opengl.GL2;

/**
 * @author Jack Hosking 
 * studentID 16932920
 * scale: 1 unit : 1 meter
 */

public class Material {
    private float[] lightAmbiance = {1, 0.7f, 0, 0.02f};
    private float[] lightDiffuse = {1, 0.7f, 0, 0.02f};
    private float[] whiteSpecular = {1, 1, 0, 1};
    private float[] standardMagrikarp = {0};
    private float[] goldenMagrikarp = {20};
    private float[] teslaEmission = {1, 0.7f, 0, 1};
    private float[] none = {0.0f, 0.0f, 0.0f, 1.0f};


    public void setLightProjectionMaterial(GL2 gl) {
        setMaterial(gl, lightAmbiance, lightDiffuse, whiteSpecular, goldenMagrikarp, teslaEmission);
    }

    public void setColourMaterial(GL2 gl, Colour colour){
        float[] baseMaterial = {colour.red, colour.green, colour.blue, colour.alpha};
        setMaterial(gl, baseMaterial, baseMaterial, baseMaterial,none , none );
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, baseMaterial, 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, baseMaterial, 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, baseMaterial, 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, none, 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, none, 0);
    }

    public void setMaterial(GL2 gl, float[] ambiance, float[] emission, float[] shine, float[] diffuse, float[] specular ) {
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambiance, 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, specular, 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, emission, 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, shine, 0);
    }

    public void clearMaterials(GL2 gl) {
        setMaterial(gl, none, none, standardMagrikarp, none, none);
    }
}
