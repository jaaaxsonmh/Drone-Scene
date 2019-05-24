package objects;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import utils.Colour;
import utils.Drawable;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jack Hosking studentID 16932920 scale: 1 unit : 1 meter
 */
public class SurfaceMapping implements Drawable {

    private BufferedImage bufferedImage;
    private Texture surfaceTexture;
    private float transparency = 1.0f;
    private int createDisplayList;
    private double tide = 0;
    private double tidePull = 0.0005;

    public SurfaceMapping(String textureSurface) {
        try {
            setSurfaceTexture(textureSurface);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setSurfaceTexture(String surfaceTexture) throws IOException {
        this.surfaceTexture = TextureIO.newTexture(new FileInputStream(surfaceTexture), false, ".jpg");
    }

    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }

    @Override
    public void draw(GL2 gl, GLU glu, GLUquadric quadric, boolean filled) {
        createDisplayList = gl.glGenLists(1);
        gl.glNewList(createDisplayList, GL2.GL_COMPILE);

        surfaceTexture.enable(gl);
        surfaceTexture.bind(gl);
        surfaceTexture.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        surfaceTexture.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);

        Colour.setDynamicColourRGBA(new Colour(1.0f, 1.0f, 1.0f), transparency, gl);

        for (int i = -200; i < 200; i++) {
            for (int j = -200; j < 200; j++) {
                gl.glBegin(filled ? GL2.GL_QUADS : GL.GL_LINE_LOOP);

                // makes a 1x1 square grid.
                gl.glNormal3f(0.0f, 1.0f, 0.0f);
                gl.glTexCoord2d(2, 1);
                gl.glVertex3f(i, 0, j);

                gl.glNormal3f(0.0f, 1.0f, 0.0f);
                gl.glTexCoord2d(2, 2);
                gl.glVertex3d(i + 1, 0, j);

                gl.glNormal3f(0, 1.0f, 0);
                gl.glTexCoord2d(1, 2);
                gl.glVertex3d(i + 1, 0, j + 1);

                gl.glNormal3f(0, 1.0f, 0);
                gl.glTexCoord2d(1, 1);
                gl.glVertex3d(i, 0, j + 1);

                gl.glEnd();
            }
        }
        surfaceTexture.disable(gl);
        gl.glEndList();
    }

    public void drawDisplayList(GL2 gl) {
        gl.glPushMatrix();
                        gl.glTranslated(0, tide, 0);
        gl.glCallList(createDisplayList);
        gl.glPopMatrix();
    }

    public void animate() {
        if (tide > 1.0 || tide <0.0) {
            tidePull *= -1;
        }
        tide += tidePull;
    }
}
