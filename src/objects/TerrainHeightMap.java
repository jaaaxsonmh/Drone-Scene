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
 * @author Jack Hosking studentID 16932920
 */
public class TerrainHeightMap implements Drawable {

    private BufferedImage bufferedImage;
    private Image map;
    private double[][] heightMap;
    private int width, height;
    private float transparency = 1.0f;

    public TerrainHeightMap(String map) {
        try {
            setHeightMap(map);
        } catch (IOException e) {
            e.printStackTrace();
        }

        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();
        heightMap = new double[height][width];
        //for loop for to rgb /height width/
//        int rgb = bufferedImage.getRGB(width, height);
//        int grey = rgb & 255;
    }

    private void setHeightMap(String heightMap) throws IOException {
        this.bufferedImage = ImageIO.read(new File(heightMap));
    }

    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }

    @Override
    public void draw(GL2 gl, GLU glu, GLUquadric quadric, boolean filled) {
        //start displaylist gl.
        Colour.setDynamicColourRGBA(new Colour(1.0f, 1.0f, 1.0f), transparency, gl);
        // System.out.println((int)length +" " + (int) width);
        for (int i = 0; i < width; i++) {
            // being (strip)
            gl.glBegin(filled ? GL2.GL_TRIANGLE_STRIP : GL.GL_LINE_LOOP);

            for (int j = 0; j < height; j++) {

                // t
                // n
                // v (x, z)
                
                // t
                // n
                // v (x, z + 1)
                
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

            }
            gl.glEnd();
        }

        // flush display list
    }
}
