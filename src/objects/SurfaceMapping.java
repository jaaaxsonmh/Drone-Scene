package objects;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import utils.Colour;
import utils.Drawable;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Jack Hosking
 * studentID 16932920
 */

public class SurfaceMapping implements Drawable {

    private float yPos;
    private Texture surfaceTexture;
    private float transparency = 1.0f;

    public SurfaceMapping(float yPos, String file) {
        this.yPos = yPos;
        boolean texture = !file.isEmpty();
        if (texture) {
            try {
                setSurfaceTexture(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setSurfaceTexture(String file) throws IOException {

        surfaceTexture = TextureIO.newTexture(new FileInputStream(file), false, ".jpg");
        System.out.println(surfaceTexture);
    }

    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }

    @Override
    public void draw(GL2 gl, GLU glu, GLUquadric quadric, boolean filled) {

        surfaceTexture.enable(gl);
        surfaceTexture.bind(gl);

        surfaceTexture.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        surfaceTexture.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);

        Colour.setDynamicColourRGBA(new Colour(1.0f, 1.0f, 1.0f), transparency, gl);

        // System.out.println((int)length +" " + (int) width);
        for (float i = -200; i < 200; i++) {
            for (float j = -200; j < 200; j++) {
                gl.glBegin(filled ? GL2.GL_QUADS : GL.GL_LINE_LOOP);

                // makes a 1x1 square grid.
                gl.glNormal3f(0.0f, 1.0f, 0.0f);
                gl.glTexCoord2d(2, 1);
                gl.glVertex3d(i, yPos, j);

                gl.glNormal3f(0.0f, 1.0f, 0.0f);
                gl.glTexCoord2d(2 , 2);
                gl.glVertex3d(i + 1, yPos, j);

                gl.glNormal3f(0, 1.0f, 0);
                gl.glTexCoord2d(1, 2);
                gl.glVertex3d(i + 1, yPos, j + 1);

                gl.glNormal3f(0, 1.0f, 0);
                gl.glTexCoord2d(1, 1);
                gl.glVertex3d(i, yPos, j + 1);

                gl.glEnd();
            }
        }
        surfaceTexture.disable(gl);
    }
}
