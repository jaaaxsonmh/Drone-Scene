
/**
 * A demonstration class for texturing.
 *
 * @author Jacqueline Whalley
 */

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class Texturing implements GLEventListener, KeyListener {

    // position of Light 0
    private float lightPosition[] = { 2.0f, 2.0f, 2.0f, 1.0f };

    // materials
    private float zeroMaterial[] = { 0.0f, 0.0f, 0.0f, 1.0f };
    private float whiteMaterial[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    private float greyMaterial[] = { 0.5f, 0.5f, 0.5f, 1.0f };

    private GLU glu;
    private GLUquadric quadric;
    private static GLCanvas canvas;

    private TrackballCamera camera;
    private boolean moveLight = false;
    private boolean rotateTexture = false;
    private double scaleTexture = 1.0;
    private boolean clampS = false;
    private boolean clampT = false;

    private int activeTexture = 0;
    private int myTextureId = 0;
    private Texture[] textures;
    private Object activeObject = Object.PLANE;
    private Filtering activeFiltering = Filtering.NEAREST;

    public static void main(String[] args) {
        Frame frame = new Frame("Texturing");
        canvas = new GLCanvas();

        System.out.println("Key mapping:");
        System.out.println("1: UV texture");
        System.out.println("2: Line texture");
        System.out.println("3: Wall texture");
        System.out.println("4: Sand texture");
        System.out.println("5: Grass texture");
        System.out.println("6: Earth texture");
        System.out.println("7: Skybox texture");
        System.out.println("8: Skydome texture");
        System.out.println("P: Plane");
        System.out.println("C: Cube");
        System.out.println("Y: Cylinder");
        System.out.println("S: Sphere");
        System.out.println("L: Toggle movement of the light source");
        System.out.println("R: Toggle rotation of the texture");
        System.out.println("PageUP/Down : Scale the texture");
        System.out.println("Right/Up    : Toggle clamping of S/T");
        System.out.println("Left  mouse button: Rotate scene");
        System.out.println("Right mouse button: Change camera distance");
        System.out.println("Mouse wheel       : Zoom");

        Texturing app = new Texturing();
        canvas.addGLEventListener(app);
        canvas.addKeyListener(app);

        frame.add(canvas);
        frame.setSize(400, 400);
        final Animator animator = new Animator(canvas);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                // Run this on another thread than the AWT event queue to
                // make sure the call to Animator.stop() completes before
                // exiting
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });
        // Center frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        // Enable VSync
        gl.setSwapInterval(1);
        // Setup the drawing area and shading mode
        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        gl.glShadeModel(GL2.GL_SMOOTH); // try setting this to GL_FLAT and see
        // what happens.
        gl.glEnable(GL2.GL_DEPTH_TEST);

        glu = new GLU();

        camera = new TrackballCamera(canvas);
        camera.setDistance(1);
        camera.setFieldOfView(30);

        quadric = glu.gluNewQuadric();

        // parameters for light 0
        float ambientLight[] = { 0, 0, 0, 1 }; // no ambient
        float diffuseLight[] = { 1, 1, 1, 1 }; // white light for diffuse
        float specularLight[] = { 1, 1, 1, 1 }; // white light for specular
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambientLight, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseLight, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specularLight, 0);
        // amount of global ambient light
        float globalAmbientLight[] = { 0.72f, 0.72f, 0.72f, 1 };
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globalAmbientLight, 0);
        // enable lighting
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_NORMALIZE);

        // load textures from files
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

        // generate random texture - simple procedural texture
        int width = 256;
        int height = 256;
        int idx = 0;
        byte randomTextureData[] = new byte[width * height * 3];
        for (int s = 0; s < width; s++) {
            for (int t = 0; t < height; t++) {
                randomTextureData[idx++] = (byte) (255 * Math.random()); // R
                randomTextureData[idx++] = (byte) (255 * Math.random()); // G
                randomTextureData[idx++] = (byte) (255 * Math.random()); // B
            }
        }
        Buffer buffer = ByteBuffer.wrap(randomTextureData);
        buffer.rewind();
        int[] textureId = new int[1];
        gl.glGenTextures(1, textureId, 0);
        myTextureId = textureId[0];
        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextureId);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, 3, width, height, 0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, buffer);

        activeObject = Object.PLANE;
        activeTexture = 0;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        camera.newWindowSize(width, height);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        camera.draw(gl);

        drawLightSource(gl);
        drawCoordinateSystem(gl);
        drawMaterial(gl);
        drawTexture(gl);

        switch (activeObject) {
            case PLANE:
                drawTexturedPlane(gl);
                break;
            case CUBE:
                drawTexturedCube(gl);
                break;
            case CYLINDER:
                drawTexturedCylinder(gl);
                break;
            case SPHERE:
                drawTexturedSphere(gl);
                break;
        }

        // Flush all drawing operations to the graphics card
        gl.glFlush();
    }

    private void drawLightSource(GL2 gl) {
        if (moveLight) {
            double time = System.currentTimeMillis() / 1000.0;
            lightPosition[0] = (float) (2.0 * Math.sin(time));
            lightPosition[1] = (float) (2.0 * Math.cos(time));
            lightPosition[2] = (float) (2.0 * Math.cos(time / 2));
        } else {
            lightPosition[0] = 2.0f;
            lightPosition[1] = 2.0f;
            lightPosition[2] = 2.0f;
        }

        // position the light
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0);

        // draw a small white sphere for the light source
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glPushMatrix();
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glColor4d(1, 1, 1, 1);
        gl.glTranslated(lightPosition[0], lightPosition[1], lightPosition[2]);
        glu.gluSphere(quadric, 0.1, 16, 8);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glPopMatrix();
    }

    private void drawCoordinateSystem(GL2 gl) {
        double size = 1.5;
        gl.glLineWidth(2.0f);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glBegin(GL2.GL_LINES);
        gl.glColor3d(1, 0, 0);
        gl.glVertex3d(0, 0, 0);
        gl.glVertex3d(size, 0, 0);
        gl.glColor3d(0, 1, 0);
        gl.glVertex3d(0, 0, 0);
        gl.glVertex3d(0, size, 0);
        gl.glColor3d(0, 0, 1);
        gl.glVertex3d(0, 0, 0);
        gl.glVertex3d(0, 0, size);
        gl.glEnd();
    }

    private void drawMaterial(GL2 gl) {
        gl.glEnable(GL2.GL_LIGHTING);
        // setup material
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, greyMaterial, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, whiteMaterial, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, greyMaterial, 0);
        gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 50.0f);
    }

    private void drawTexture(GL2 gl) {
        gl.glEnable(GL2.GL_TEXTURE_2D);

        if (activeTexture < 0) {
            // this is the old manual OpenGL way of doing textures
            // bind the texture
            gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextureId);
            // set clamping parameters
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, clampS ? GL2.GL_CLAMP : GL2.GL_REPEAT);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, clampT ? GL2.GL_CLAMP : GL2.GL_REPEAT);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, activeFiltering.getMinMode());
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, activeFiltering.getMagMode());
        } else {
            // this is how to use textures with the Texture class
            Texture tex = textures[activeTexture];
            // bind the texture
            tex.bind(gl);
            // set clamping parameters
            tex.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, clampS ? GL2.GL_CLAMP : GL2.GL_REPEAT);
            tex.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, clampT ? GL2.GL_CLAMP : GL2.GL_REPEAT);
            tex.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, activeFiltering.getMinMode());
            tex.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, activeFiltering.getMagMode());
        }

        // switch to texture matrix mode
        gl.glMatrixMode(GL2.GL_TEXTURE);
        gl.glLoadIdentity();

        if (rotateTexture) {
            double time = (System.currentTimeMillis() / 100.0) % 360.0;
            gl.glTranslated(0.5, 0.5, 0.0);
            gl.glRotated(time, 0, 0, 1);
            gl.glTranslated(-0.5, -0.5, 0.0); // rotate around texture center
        }


        // switch back to model/vide matrix mode
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    private void drawTexturedPlane(GL2 gl) {
        gl.glBegin(GL2.GL_POLYGON);
        gl.glNormal3d(0, 0, 1);
        gl.glTexCoord2d(0, 0);
        gl.glVertex3d(-1, -1, 0);
        gl.glTexCoord2d(1, 0);
        gl.glVertex3d(1, -1, 0);
        gl.glTexCoord2d(1, 1);
        gl.glVertex3d(1, 1, 0);
        gl.glTexCoord2d(0, 1);
        gl.glVertex3d(-1, 1, 0);
        gl.glEnd();
    }

    private void drawTexturedCube(GL2 gl) {
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

        if (activeTexture == 6) {
            // skybox texture: disable lighting and enlarge box
            gl.glDisable(GL2.GL_LIGHTING);
            gl.glColor3d(1, 1, 1);
            gl.glScaled(3.0, 3.0, 3.0);

        }

        for (int f = 0; f < faces.length; f++) {
            gl.glNormal3dv(normals[f], 0);
            gl.glBegin(GL2.GL_QUADS);
            for (int v = 0; v < 4; v++) {
                gl.glTexCoord2dv(texCoords1[f][v], 0);
                gl.glVertex3dv(vertices[faces[f][v]], 0);
            }
            gl.glEnd();
        }

        gl.glPopMatrix();
    }

    private void drawTexturedCylinder(GL2 gl) {
        gl.glRotated(90, 1, 0, 0); // rotate upright to Y axis
        glu.gluQuadricTexture(quadric, true);
        // let height be -1 to avoid mirrored texture coordinates
        glu.gluCylinder(quadric, 0.25, 0.25, -1.0, 40, 20);
    }

    private void drawTexturedSphere(GL2 gl) {
        gl.glPushMatrix();

        if (activeTexture == 7) {
            // skydome texture: disable lighting and enlarge sphere
            gl.glDisable(GL2.GL_LIGHTING);
            gl.glColor3d(1, 1, 1);
            gl.glScaled(3.0, 3.0, 3.0);
        }

        gl.glRotated(180, 0, 1, 0); // rotate upright to Y axis
        gl.glRotated(-90, 1, 0, 0);
        glu.gluQuadricTexture(quadric, true);
        glu.gluSphere(quadric, 1.0, 40, 20);

        gl.glPopMatrix();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_L: {
                moveLight = !moveLight;
                break;
            }
            case KeyEvent.VK_T: {
                rotateTexture = !rotateTexture;
                break;
            }

            case KeyEvent.VK_RIGHT: {
                clampS = !clampS;
                break;
            }
            case KeyEvent.VK_UP: {
                clampT = !clampT;
                break;
            }

            case KeyEvent.VK_F: {
                activeFiltering = activeFiltering.nextMode();
                System.out.println("Filtering Mode: " + activeFiltering);
                break;
            }

            case KeyEvent.VK_PAGE_UP: {
                scaleTexture *= 1.1;
                break;
            }
            case KeyEvent.VK_PAGE_DOWN: {
                scaleTexture /= 1.1;
                break;
            }

            case KeyEvent.VK_1: {
                activeTexture = 0;
                break;
            }
            case KeyEvent.VK_2: {
                activeTexture = 1;
                break;
            }
            case KeyEvent.VK_3: {
                activeTexture = 2;
                break;
            }
            case KeyEvent.VK_4: {
                activeTexture = 3;
                break;
            }
            case KeyEvent.VK_5: {
                activeTexture = 4;
                break;
            }
            case KeyEvent.VK_6: {
                activeTexture = 5;
                break;
            }
            case KeyEvent.VK_7: {
                activeTexture = 6;
                break;
            }
            case KeyEvent.VK_8: {
                activeTexture = 7;
                break;
            }
            case KeyEvent.VK_0: {
                activeTexture = -1;
                break;
            }

            case KeyEvent.VK_P: {
                activeObject = Object.PLANE;
                break;
            }
            case KeyEvent.VK_Y: {
                activeObject = Object.CYLINDER;
                break;
            }
            case KeyEvent.VK_S: {
                activeObject = Object.SPHERE;
                break;
            }
            case KeyEvent.VK_C: {
                activeObject = Object.CUBE;
                break;
            }
            case KeyEvent.VK_R: {
                rotateTexture = !rotateTexture;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private enum Object {
        PLANE, CYLINDER, CUBE, SPHERE;
    }

    private enum Filtering {
        NEAREST(GL2.GL_NEAREST, GL2.GL_NEAREST), LINEAR(GL2.GL_LINEAR, GL2.GL_LINEAR), NEAREST_MIPMAP_LINEAR(
                GL2.GL_NEAREST, GL2.GL_NEAREST_MIPMAP_LINEAR), NEAREST_MIPMAP_NEAREST(GL2.GL_NEAREST,
                GL2.GL_NEAREST_MIPMAP_NEAREST), LINEAR_MIPMAP_LINEAR(GL2.GL_LINEAR,
                GL2.GL_LINEAR_MIPMAP_LINEAR), LINEAR_MIPMAP_NEAREST(GL2.GL_LINEAR,
                GL2.GL_LINEAR_MIPMAP_NEAREST);

        private Filtering(int mag, int min) {
            minMode = min;
            magMode = mag;
        }

        public int getMinMode() {
            return minMode;
        }

        public int getMagMode() {
            return magMode;
        }

        public Filtering nextMode() {
            switch (this) {
                case NEAREST:
                    return LINEAR;
                case LINEAR:
                    return NEAREST_MIPMAP_LINEAR;
                case NEAREST_MIPMAP_LINEAR:
                    return NEAREST_MIPMAP_NEAREST;
                case NEAREST_MIPMAP_NEAREST:
                    return LINEAR_MIPMAP_LINEAR;
                case LINEAR_MIPMAP_LINEAR:
                    return LINEAR_MIPMAP_NEAREST;
                default:
                    return NEAREST;
            }
        }

        private int minMode;
        private int magMode;
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // TODO Auto-generated method stub

    }

}