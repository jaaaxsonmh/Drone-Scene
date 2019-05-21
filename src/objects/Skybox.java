package objects;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import utils.Colour;
import utils.Drawable;

import java.io.File;
import java.io.IOException;

/**
 * @author Jack Hosking 
 * studentID 16932920
 * scale: 1 unit : 1 meter
 */

public class Skybox implements Drawable {

    private float whiteMaterial[] = {1.0f, 1.0f, 1.0f, 1.0f};
    private float greyMaterial[] = {0.5f, 0.5f, 0.5f, 1.0f};
    private Colour white = new Colour(1.0f, 1.0f, 1.0f, 1.0f);
    private double domeRotation = 180, sphereX = 0, sphereZ = 0;

    private Texture skyDome;

    public Skybox() {
        try {
            skyDome = TextureIO.newTexture(new File("src\\images\\skydome.jpg"), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawTexture(GL2 gl) {
        gl.glEnable(GL2.GL_TEXTURE_2D);
        skyDome.bind(gl);
        skyDome.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
        skyDome.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
        skyDome.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
        skyDome.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
    }

    private void drawMaterial(GL2 gl) {
        gl.glEnable(GL2.GL_LIGHTING);
        // setup material
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, greyMaterial, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, whiteMaterial, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, greyMaterial, 0);
        gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 50.0f);
    }

    @Override
    public void draw(GL2 gl, GLU glu, GLUquadric quadric, boolean filled) {
        drawMaterial(gl);
        drawTexture(gl);
        gl.glPushMatrix();


        gl.glTranslated(sphereX, 0, sphereZ);
        gl.glScaled(50.0, 40.0, 50.0);
        gl.glRotated(domeRotation, 0, 1, 0);
        gl.glRotated(-90, 1, 0, 0);
        glu.gluQuadricTexture(quadric, true);
        glu.gluSphere(quadric, 1.0, 40, 40);

        gl.glPopMatrix();
        skyDome.disable(gl);
    }

    public void animate() {
        domeRotation += 0.2f;
    }

    public void setSphereX(double sphereX) {
        this.sphereX = sphereX;
    }

    public void setSphereZ(double sphereZ) {
        this.sphereZ = sphereZ;
    }
}
