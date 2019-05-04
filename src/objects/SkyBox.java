package objects;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import utils.Drawable;

import java.io.File;
import java.io.IOException;

public class SkyBox implements Drawable {

    private Texture[] textures;

    public SkyBox(){
        textures = new Texture[6];
        try {
            textures[0] = TextureIO.newTexture(new File("./images/uvmap.jpg"), true);
            textures[2] = TextureIO.newTexture(new File("./images/wall_tileable.jpg"), true);
            textures[3] = TextureIO.newTexture(new File("./images/sand_tileable.jpg"), true);
            textures[4] = TextureIO.newTexture(new File("./images/grass_tileable.jpg"), true);
            textures[6] = TextureIO.newTexture(new File("./images/skybox.jpg"), true);
            textures[7] = TextureIO.newTexture(new File("./images/skydome.jpg"), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void draw(GL2 gl, GLU glu, GLUquadric quadric, boolean filled) {
        final double[][] vertices = { { -1, -1, +1 }, { +1, -1, +1 }, { +1, +1, +1 }, { -1, +1, +1 }, { -1, -1, -1 },
                { +1, -1, -1 }, { +1, +1, -1 }, { -1, +1, -1 } };
        final int[][] faces = { { 0, 1, 2, 3 }, { 4, 5, 6, 7 }, { 1, 5, 6, 2 }, { 4, 0, 3, 7 }, { 4, 5, 1, 0 },
                { 3, 2, 6, 7 } };
        final double[][] normals = { { 0, 0, +1 }, { 0, 0, -1 }, { +1, 0, 0 }, { -1, 0, 0 }, { 0, -1, 0 },
                { 0, +1, 0 } };
        final double[][][] texCoords1 = { // texture coordinates for 1 texture
                { { 0.25, 1.00 }, { 0.50, 1.00 }, { 0.50, 0.75 }, { 0.25, 0.75 } },
                { { 0.25, 0.25 }, { 0.50, 0.25 }, { 0.50, 0.50 }, { 0.25, 0.50 } },
                { { 0.75, 0.25 }, { 0.50, 0.25 }, { 0.50, 0.50 }, { 0.75, 0.50 } },
                { { 0.25, 0.25 }, { 0.00, 0.25 }, { 0.00, 0.50 }, { 0.25, 0.50 } },
                { { 0.25, 0.25 }, { 0.50, 0.25 }, { 0.50, 0.00 }, { 0.25, 0.00 } },
                { { 0.25, 0.75 }, { 0.50, 0.75 }, { 0.50, 0.50 }, { 0.25, 0.50 } } };
        final double[][][] texCoords6 = { // texture coordinates for 6 textures
                { { 0, 0 }, { 1, 0 }, { 1, 1 }, { 0, 1 } }, { { 0, 0 }, { 1, 0 }, { 1, 1 }, { 0, 1 } },
                { { 0, 0 }, { 1, 0 }, { 1, 1 }, { 0, 1 } }, { { 0, 0 }, { 1, 0 }, { 1, 1 }, { 0, 1 } },
                { { 0, 0 }, { 1, 0 }, { 1, 1 }, { 0, 1 } }, { { 0, 0 }, { 1, 0 }, { 1, 1 }, { 0, 1 } } };

        gl.glPushMatrix();


        for (int f = 0; f < faces.length; f++) {
            gl.glNormal3dv(normals[f], 0);
            gl.glBegin(GL2.GL_QUADS);
            for (int v = 0; v < 4; v++) {
                gl.glTexCoord2dv(texCoords6[f][v], 0);
                gl.glVertex3dv(vertices[faces[f][v]], 0);
            }
            gl.glEnd();
        }

        gl.glPopMatrix();
    }
}
