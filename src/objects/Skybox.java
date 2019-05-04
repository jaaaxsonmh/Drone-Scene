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

public class Skybox implements Drawable {

    private float whiteMaterial[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    private float greyMaterial[] = { 0.5f, 0.5f, 0.5f, 1.0f };
    private Colour white = new Colour(1.0f, 1.0f, 1.0f, 1.0f);

    private Texture skyDome;

    public Skybox() {
        try {
            skyDome = TextureIO.newTexture(new File("src\\images\\skydome.jpg"), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawTexture(GL2 gl) {
        skyDome.enable(gl);
        skyDome.bind(gl);

        skyDome.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
        skyDome.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);

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

        Colour.setColourRGBA(white, gl);

        gl.glScaled(50.0, 50.0, 50.0);
        //Rotate Sphere so that
        gl.glRotated(180, 0, 1, 0);
        gl.glRotated(-90, 1, 0, 0);
        glu.gluQuadricTexture(quadric, true);
        glu.gluSphere(quadric, 1.0, 50, 40);

        gl.glPopMatrix();
        skyDome.disable(gl);
    }
}
