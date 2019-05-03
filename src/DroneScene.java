import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import objects.*;
import utils.Guide;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Jack Hosking
 * studentID 16932920
 */


public class DroneScene implements GLEventListener, KeyListener {

    private Guide guide;
    private Drone drone;

    private static final float[] fogColour = new float[]{0.95f, 0.95f, 0.95f};


    private static GLCanvas canvas;
    private SurfaceMapping waterSurfaceTexture, terrainSurfaceTexture;

    private GLUT glut;
    private GLU glu;
    private GLUquadric quadric;
    private boolean filled = true, animateEnabled = true, guideEnabled = false;
    private float animatorSpeed = 1.0f;
    private final long TIME_DELAY = 300L;
    private long prevTime = System.currentTimeMillis() - TIME_DELAY;

    private TrackballCamera camera = new TrackballCamera(canvas);

    private DroneScene() {

        drone = new Drone(1.5f);
        glut = new GLUT();
//        bub = new BubbleManager();

    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        // calculate the position from the camera for fog.
        float positionRelativeToCam = (float) camera.getDistance() * (float) camera.getFieldOfView();

        // select and clear the model-view matrix
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        productionMode(gl);


        // change the rendering style based on key presses
        int style = filled ? GLU.GLU_FILL : GLU.GLU_LINE;
        glu.gluQuadricDrawStyle(quadric, style);

//        drone.animate(animatorSpeed);
        drone.draw(gl, glu, quadric, filled);

        waterSurfaceTexture.draw(gl, glu, quadric, filled);
        terrainSurfaceTexture.draw(gl,glu,quadric,filled);

        if (guideEnabled) {
            guide.draw(gl, glu, quadric, filled);
        }

        setUpFog(gl, positionRelativeToCam);

        gl.glFlush();
    }

    private void productionMode(GL2 gl) {
        camera.draw(gl);
        camera.setLookAt(drone.getX(), drone.getY(), drone.getZ());
        camera.setDistance(10);
        camera.setCamZ(drone.getZ() - 4.0 * Math.cos(Math.toRadians(drone.getRotation())));
        camera.setCamX(drone.getX() - 4.0 * Math.sin(Math.toRadians(drone.getRotation())));
        camera.setCamY(drone.getY() + 0.9);
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

        gl.glShadeModel(GL2.GL_SMOOTH);
        camera = new TrackballCamera(canvas);
        glu = new GLU();
        quadric = glu.gluNewQuadric();
        guide = new Guide();
        waterSurfaceTexture = new SurfaceMapping(25, "src\\images\\water-pool-texture-seamless.jpg");
        waterSurfaceTexture.setTransparency(0.4f);
        terrainSurfaceTexture = new SurfaceMapping(-25, "src\\images\\sand-texture-seamless.jpg");
        camera.setDistance(15);
        camera.setFieldOfView(40);

        // use the lights
        this.lights(gl);

        gl.glEnable(GL2.GL_DEPTH_TEST);
    }

    private void lights(GL2 gl) {
        // lighting stuff
        float ambient[] = {0, 0, 0, 1};
        float diffuse[] = {1f, 1f, 1f, 1};
        float specular[] = {1, 1, 1, 1};

        float[] ambientLight = {0.1f, 0.1f, 0.1f, 0f}; // weak RED ambient
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_AMBIENT, ambientLight, 0);

        float position0[] = {5, 5, 5, 0};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position0, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specular, 0);

        float position1[] = {-10, -10, -10, 0};
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, position1, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, specular, 0);

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_LIGHT1);

        // lets use use standard color functions
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        // normalise the surface normals for lighting calculations
        gl.glEnable(GL2.GL_NORMALIZE);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }

    private void setUpFog(GL2 gl, float positionRelativeToCam) {

        float fogDensity = 0.0f;
//        if (positionRelativeToCam <= 600) {
//            fogDensity = 0.0f;
//        } else {
//            fogDensity = positionRelativeToCam / 10000;
//        }

        gl.glEnable(GL2.GL_FOG);
        gl.glFogfv(GL2.GL_FOG_COLOR, fogColour, 0);
        gl.glFogf(GL2.GL_FOG_MODE, GL2.GL_EXP2);
        gl.glFogf(GL2.GL_FOG_DENSITY, fogDensity);
    }

    public static void main(String[] args) {
        Frame frame = new Frame("Jack's 3D Drone(y) Tank");
        frame.setResizable(false);

        // key mapping console prints

        System.out.println("------- Key mapping -------");
        System.out.println("W: Wireframe");
        System.out.println("G: X/Y/Z Guides");
        System.out.println("SPACE: pause/restart");
        System.out.println("1: SLOW ANIMATION SPEED");
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

    private void setAnimate() {
        animateEnabled = !animateEnabled;
    }

    private void setGuide() {
        guideEnabled = !guideEnabled;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W) {

            if (filled) {
                System.out.println("Wireframe enabled");
            } else {
                System.out.println("Wireframe disabled");
            }

            setFilled();
        }

        if (key == KeyEvent.VK_SPACE) {

            if (animateEnabled) {
                System.out.println("Animation paused");
            } else {
                System.out.println("Animation restart");
            }

            setAnimate();
        }

        if (key == KeyEvent.VK_1) {
            if (animatorSpeed == 0.2f) {
                System.out.println("\nSlow animator already enabled");
                System.out.println("Animator Speed: " + animatorSpeed + "");
            } else {
                animatorSpeed = 0.2f;
                System.out.println("\nSlow animator enabled");
                System.out.println("Animator Speed: " + animatorSpeed + "");
            }
        }

        if (key == KeyEvent.VK_2) {
            if (animatorSpeed == 1.0f) {
                System.out.println("\nNormal animator already enabled");
                System.out.println("Animator Speed: " + animatorSpeed);
            } else {
                animatorSpeed = 1.0f;
                System.out.println("\nNormal animator enabled");
                System.out.println("Animator Speed: " + animatorSpeed);
            }
        }

        if (key == KeyEvent.VK_3) {
            if (animatorSpeed == 4.0f) {
                System.out.println("\nFast animator already enabled");
                System.out.println("Animator Speed: " + animatorSpeed);
            } else {
                animatorSpeed = 4.0f;
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
