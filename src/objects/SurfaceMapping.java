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
    private Image heightMap;
    private Texture surfaceTexture;
    private float transparency = 1.0f;

    public SurfaceMapping(float yPos, String textureSurface, String heightMap) {
        this.yPos = yPos;
        boolean texture = !textureSurface.isEmpty();
        if (texture) {
            try {
                setSurfaceTexture(textureSurface);
                setHeightMap(heightMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setSurfaceTexture(String surfaceTexture) throws IOException {
        this.surfaceTexture = TextureIO.newTexture(new FileInputStream(surfaceTexture), false, ".jpg");
    }

    private void setHeightMap(String heightMap) throws IOException {
        this.bufferedImage = ImageIO.read(new File(heightMap));
    }

    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }

    @Override
    public void draw(GL2 gl, GLU glu, GLUquadric quadric, boolean filled) {

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        System.out.println(width);
        System.out.println(height);

        Colour.setDynamicColourRGBA(new Colour(1.0f, 1.0f, 1.0f), transparency, gl);

        // System.out.println((int)length +" " + (int) width);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                gl.glBegin(filled ? GL2.GL_TRIANGLE_STRIP : GL.GL_LINE_LOOP);

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
    }
}
