
import cameras.ThirdPersonCamera;
import cameras.TrackballCamera;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import component.Movement;
import objects.Drone;
import objects.Skybox;
import utils.Colour;
import utils.Guide;
import utils.Lighting;
import utils.Material;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import objects.SurfaceMapping;
import objects.TerrainHeightMap;

/**
 * @author Jack Hosking studentID 16932920
 */
public class DroneScene implements GLEventListener, KeyListener {

    private Guide guide;
    private Drone drone;

    private static final float[] FOG_COLOUR = new float[]{0.0f, 0.89803f, 0.98823f, 0.2f};
    private final Colour WATER_COLOUR = new Colour(0.73333f, 0.87058823529f, 0.98823f, 0.2f);
    private static GLCanvas canvas;

    private GLUT glut;
    private GLU glu;
    private GLUquadric quadric;
    private boolean filled = true, cameraSwitch = false, guideEnabled = false;
    private float animatorSpeed = 1.0f;
    private final long TIME_DELAY = 300L;
    public Material materials = new Material();

    private TrackballCamera trackballCamera = new TrackballCamera(canvas);
    private ThirdPersonCamera camera = new ThirdPersonCamera(canvas);
    private Skybox skybox;
    private Lighting lighting;
    private SurfaceMapping waterSurfaceTexture;
    private TerrainHeightMap terrainMap;

    private DroneScene() {
        drone = new Drone(1.0f);
        glut = new GLUT();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        // calculate the position from the trackballCamera for fog.
        float positionRelativeToCam = (float) trackballCamera.getDistance() * (float) trackballCamera.getFieldOfView();

        // select and clear the model-view matrix
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        productionMode(gl, cameraSwitch);

        // change the rendering style based on key presses
        int style = filled ? GLU.GLU_FILL : GLU.GLU_LINE;
        glu.gluQuadricDrawStyle(quadric, style);

        float[] spotLightPosition = {0, 0, 0, 1f};
        spotLightPosition[0] = (float) (drone.getX() + 2.0f * Math.sin(Math.toRadians(drone.getRotation())));
        spotLightPosition[1] = (drone.getY());
        spotLightPosition[2] = (float) (drone.getZ() + 2.0f * Math.cos(Math.toRadians(drone.getRotation())));
        lighting.drawDroneSpotlight(gl, spotLightPosition);

        skybox.draw(gl, glu, quadric, filled);
        drone.animate(animatorSpeed);
        drone.draw(gl, glu, quadric, filled);

        gl.glEnable(GL2.GL_BLEND);
        waterSurfaceTexture.draw(gl, glu, quadric, filled);
        gl.glDisable(GL2.GL_BLEND);

        drawSurface(gl, 2);
        if (guideEnabled) {
            guide.draw(gl, glu, quadric, filled);
        }

        if (drone.getY() + 1.0 < 0) {
            setUpFog(gl, positionRelativeToCam);

        } else if (drone.getY() + 1.0 > 0) {
            gl.glDisable(GL2.GL_FOG);
        }

        gl.glFlush();
    }

    private final Colour white = new Colour(1.0f, 1.0f, 1.0f, 1.0f);

    private void drawSurface(GL2 gl, int height) {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                gl.glBegin(filled ? GL2.GL_QUADS : GL.GL_LINE_LOOP);

                Colour.setColourRGBA(WATER_COLOUR, gl);
                // makes a 1x1 square grid.
                gl.glNormal3f(0.0f, 1.0f, 0.0f);
                gl.glTexCoord2d(2, 1);
                gl.glVertex3f(i, height, j);

                gl.glNormal3f(0.0f, 1.0f, 0.0f);
                gl.glTexCoord2d(2, 2);
                gl.glVertex3d(i + 1, height, j);

                gl.glNormal3f(0, 1.0f, 0);
                gl.glTexCoord2d(1, 2);
                gl.glVertex3d(i + 1, height, j + 1);

                gl.glNormal3f(0, 1.0f, 0);
                gl.glTexCoord2d(1, 1);
                gl.glVertex3d(i, height, j + 1);

                gl.glEnd();
            }
        }
    }

    private void productionMode(GL2 gl, boolean cameraChoice) {
        if (cameraChoice) {
            trackballCamera.draw(gl);
            trackballCamera.setLookAt(drone.getX(), drone.getY(), drone.getZ());
            trackballCamera.setDistance(10);
        } else {
            camera.draw(gl);
            camera.setLookAt(drone.getX(), drone.getY(), drone.getZ());
            camera.setDistance(10);
            camera.setCamZ(drone.getZ() - 4.0 * Math.cos(Math.toRadians(drone.getRotation())));
            camera.setCamX(drone.getX() - 4.0 * Math.sin(Math.toRadians(drone.getRotation())));
            camera.setCamY(drone.getY() + 1.0);
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        glu.gluDeleteQuadric(quadric);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.95f, 0.95f, 0.95f, 1.0f);
        gl.setSwapInterval(1);

        lighting = new Lighting(gl);
        glu = new GLU();
        quadric = glu.gluNewQuadric();
        guide = new Guide();
        skybox = new Skybox();
        terrainMap = new TerrainHeightMap("src\\src\\images\\terrain.png");

        waterSurfaceTexture = new SurfaceMapping(0, "src\\src\\images\\water-pool-texture-seamless.jpg");
        waterSurfaceTexture.setTransparency(0.9f);
        trackballCamera.setDistance(15);
        trackballCamera.setFieldOfView(40);

        camera.setFieldOfView(40);
        camera.setDistance(15);
        // use the lights
        this.lighting.setSceneLighting(gl);

        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glEnable(GL2.GL_DEPTH_TEST);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }

    private void setUpFog(GL2 gl, float positionRelativeToCam) {
        float fogDensity = 0.03f;

        gl.glEnable(GL2.GL_FOG);
        gl.glFogfv(GL2.GL_FOG_COLOR, FOG_COLOUR, 0);
        gl.glFogf(GL2.GL_FOG_MODE, GL2.GL_EXP);
        gl.glFogf(GL2.GL_FOG_START, 1);
        gl.glFogf(GL2.GL_FOG_DENSITY, fogDensity);
    }

    public static void main(String[] args) {
        Frame frame = new Frame("Jack's 3D Drone(y) Scene");
        frame.setResizable(false);

        // key mapping console prints
        System.out.println("------- Key mapping -------");
        System.out.println("E: Wireframe");
        System.out.println("G: X/Y/Z Guides");
        System.out.println("SPACE: Switch Camera Mode");
        System.out.println("1: Slow");
        System.out.println("2: NORMAL ANIMATION SPEED");
        System.out.println("3: FAST ANIMATION SPEED");
        System.out.println("======= DISABLED =======");
        System.out.println("A / Z: Increase and decrease length");
        System.out.println("S / X: Increase and decrease width");
        System.out.println("D / C: Increase and decrease height\n");
        System.out.println("---- Console debugging ----");

        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        canvas = new GLCanvas(capabilities);

        DroneScene fish3D = new DroneScene();

        // add event listeners
        canvas.addGLEventListener(fish3D);
        canvas.addKeyListener(fish3D);
        frame.add(canvas);
        frame.setSize(500, 500);

        final FPSAnimator animator = new FPSAnimator(canvas, 60);
        animator.start();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Run this on another thread than the AWT event queue to
                // make sure the call to Animator.stop() completes before
                // exiting
                new Thread(() -> {
                    animator.stop();
                    System.exit(0);
                }).start();
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    // set the style, flip boolean value.
    private void setFilled() {
        filled = !filled;
    }

    private void setCamera() {
        cameraSwitch = !cameraSwitch;
    }

    private void setGuide() {
        guideEnabled = !guideEnabled;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_A) {
            drone.setTurningMovement(Movement.LEFT_TURN);
        }
        if (key == KeyEvent.VK_W) {
            drone.setHorizontalMovement(Movement.FORWARD);
        }
        if (key == KeyEvent.VK_S) {
            drone.setHorizontalMovement(Movement.BACKWARD);
        }
        if (key == KeyEvent.VK_D) {
            drone.setTurningMovement(Movement.RIGHT_TURN);
        }
        if (key == KeyEvent.VK_UP) {
            drone.setVerticalMovement(Movement.UPWARDS);
        }

        if (key == KeyEvent.VK_DOWN) {
            drone.setVerticalMovement(Movement.DOWNWARDS);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_A || key == KeyEvent.VK_D) {
            drone.setTurningMovement(Movement.HOVER);
        }

        if (key == KeyEvent.VK_W || key == KeyEvent.VK_S) {
            drone.setHorizontalMovement(Movement.HOVER);
        }
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) {
            drone.setVerticalMovement(Movement.HOVER);
        }

        if (key == KeyEvent.VK_E) {
            if (filled) {
                System.out.println("Wireframe enabled");
            } else {
                System.out.println("Wireframe disabled");
            }
            setFilled();
        }

        if (key == KeyEvent.VK_SPACE) {
            setCamera();
        }

        if (key == KeyEvent.VK_1) {
            if (animatorSpeed == 1.0) {
                System.out.println("\nSlow animator already enabled");
                System.out.println("Animator Speed: " + animatorSpeed + "");
            } else {
                animatorSpeed = 1.0f;
                System.out.println("\nSlow animator enabled");
                System.out.println("Animator Speed: " + animatorSpeed + "");
            }
        }

        if (key == KeyEvent.VK_2) {
            if (animatorSpeed == 2.0f) {
                System.out.println("\nNormal animator already enabled");
                System.out.println("Animator Speed: " + animatorSpeed);
            } else {
                animatorSpeed = 2.0f;
                System.out.println("\nNormal animator enabled");
                System.out.println("Animator Speed: " + animatorSpeed);
            }
        }

        if (key == KeyEvent.VK_3) {
            if (animatorSpeed == 3.0f) {
                System.out.println("\nFast animator already enabled");
                System.out.println("Animator Speed: " + animatorSpeed);
            } else {
                animatorSpeed = 3.0f;
                System.out.println("\nFast animator enabled");
                System.out.println("Animator Speed: " + animatorSpeed);
            }
        }

        if (key == KeyEvent.VK_G) {
            if (guideEnabled) {
                System.out.println("Guide disabled");
            } else {
                System.out.println("Guide enabled");
            }
            setGuide();
        }

        if (key != KeyEvent.VK_1
                && key != KeyEvent.VK_2
                && key != KeyEvent.VK_3
                && key != KeyEvent.VK_SPACE
                && key != KeyEvent.VK_W
                && key != KeyEvent.VK_A
                && key != KeyEvent.VK_S
                && key != KeyEvent.VK_D
                && key != KeyEvent.VK_Z
                && key != KeyEvent.VK_X
                && key != KeyEvent.VK_C
                && key != KeyEvent.VK_G) {
            System.out.println("\nNot a valid command");
        }
    }
}
