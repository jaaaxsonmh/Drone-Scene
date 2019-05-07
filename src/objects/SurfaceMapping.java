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
 * @author Jack Hosking
 * studentID 16932920
 */

public class SurfaceMapping implements Drawable {

    private float yPos;
    private BufferedImage bufferedImage;
    private Texture surfaceTexture;
    private float transparency = 1.0f;

    public SurfaceMapping(float yPos, String textureSurface) {
        this.yPos = yPos;
        boolean texture = !textureSurface.isEmpty();
        if (texture) {
            try {
                setSurfaceTexture(textureSurface);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        surfaceTexture.enable(gl);
        surfaceTexture.bind(gl);
        surfaceTexture.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        surfaceTexture.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);

        Colour.setDynamicColourRGBA(new Colour(1.0f, 1.0f, 1.0f), transparency, gl);

        for (int i = -50; i < 50; i++) {
            for (int j = -50; j < 50; j++) {
                gl.glBegin(filled ? GL2.GL_QUADS : GL.GL_LINE_LOOP);

                // makes a 1x1 square grid.
                gl.glNormal3f(0.0f, 1.0f, 0.0f);
                gl.glTexCoord2d(2, 1);
                gl.glVertex3f(i, yPos, j);

                gl.glNormal3f(0.0f, 1.0f, 0.0f);
                gl.glTexCoord2d(2 , 2);
                gl.glVertex3d(i + 1, yPos, j);

                gl.glNormal3f(0, 1.0f, 0);
                gl.glTexCoord2d(1, 2);
                gl.glVertex3d(i + 1, yPos / 0.5, j + 1);

                gl.glNormal3f(0, 1.0f, 0);
                gl.glTexCoord2d(1, 1);
                gl.glVertex3d(i, yPos * 0.5, j + 1);

                gl.glEnd();
            }
        }
        surfaceTexture.disable(gl);
    }
}
