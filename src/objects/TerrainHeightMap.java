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
public class TerrainHeightMap implements Drawable {

    private BufferedImage bufferedImage;
    private Image map;
    private double[][] heightMap;
    private int width, height;
    private float transparency = 1.0f;
    private Texture terrainTexture;
    private int createDisplayList;

    public TerrainHeightMap(String map) {
        try {
            setHeightMap(map);
        } catch (IOException e) {
            e.printStackTrace();
        }

        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();
        heightMap = new double[height + 2][width + 2];
        for (int y = 0; y < heightMap.length - 1; y++) {
            for (int x = 0; x < heightMap[y].length - 1; x++) {
                heightMap[x][y] = getHeight(x, y, bufferedImage);
                heightMap[x][y + 1] = getHeight(x, y + 1, bufferedImage);
            }
        }
    }

    private void setHeightMap(String heightMap) throws IOException {
        this.bufferedImage = ImageIO.read(new File(heightMap));
        this.terrainTexture = TextureIO.newTexture(new FileInputStream("src\\src\\images\\sand-texture-seamless.jpg"), false, ".jpg");
    }

    private double getHeight(int x, int y, BufferedImage bufferedImage) {
        try {
            int max_c = 256 * 256 * 256;
            int h = 20;

            double rgb = bufferedImage.getRGB(x, y);
            rgb += max_c / 2f;
            rgb /= max_c / 2f;
            rgb *= h;
            return rgb;
        } catch (Exception e) {
            return 0;
        }
    }

    private double getHeight(int x, int y) {
        try {
            return heightMap[x][y];
        } catch (Exception e) {
            return 0;
        }
    }

    private double getNormal(int j, int i) {
        try {
            double v1 = heightMap[j][i + 1] - heightMap[j][i];
            double v2 = heightMap[j + 1][i] - heightMap[j][i];
            double v3 = heightMap[j][i - 1] - heightMap[j][i];
            double v4 = heightMap[j - 1][i] - heightMap[j][i];

            double n1 = v1 * v2;
            double n2 = v2 * v3;
            double n3 = v3 * v4;
            double n4 = v4 * v1;

            return (n1 + n2 + n3 + n4) / Math.abs(n1 + n2 + n3 + n4);

        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void draw(GL2 gl, GLU glu, GLUquadric quadric, boolean filled) {
        createDisplayList = gl.glGenLists(1);
        gl.glNewList(createDisplayList, GL2.GL_COMPILE);
        Colour.setDynamicColourRGBA(new Colour(1.0f, 1.0f, 1.0f), transparency, gl);

        for (int i = 0; i < height; i++) {
            terrainTexture.enable(gl);
            terrainTexture.bind(gl);

            terrainTexture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
            terrainTexture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
            terrainTexture.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
            terrainTexture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
            gl.glBegin(GL2.GL_TRIANGLE_STRIP);
            for (int j = 0; j < width; j++) {

                gl.glNormal3d(0.0, getNormal(j, i), 0.0);
                gl.glTexCoord2d(j, i);
                gl.glVertex3d(j, getHeight(j, i), i);

                gl.glNormal3d(0.0, getNormal(j, i), 0.0);
                gl.glTexCoord2d(j, i + 1);
                gl.glVertex3d(j, getHeight(j, i), i + 1);

            }
            gl.glEnd();
        }
        terrainTexture.disable(gl);
        gl.glEndList();
    }
    
    public void drawDisplayList(GL2 gl){
        gl.glPushMatrix();
        gl.glTranslated(-200, 0, -200);
        gl.glCallList(createDisplayList);
        gl.glPopMatrix();
    }
}
