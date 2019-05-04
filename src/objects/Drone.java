package objects;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;
import component.Axis;
import component.DroneComponent;
import component.Movement;
import utils.Colour;
import utils.Drawable;
import utils.Material;
import utils.Rand;

/**
 * @author Jack Hosking studentID 16932920
 */
public class Drone implements Drawable {

    private final Colour fish = new Colour(1.0f, 0.65490f, 0.14901f, 1.0f);
    private final Colour white = new Colour(1.0f, 1.0f, 1.0f, 1.0f);
    private final Colour pupil = new Colour(0.0f, 0.0f, 0.0f, 1.0f);

    private float rotation = 0;
    private float x;
    private float y;
    private float z;
    private float vx = Rand.getFloatBetween(0.002f, 0.005f), vy = Rand.getFloatBetween(0.002f, 0.005f), vz = Rand.getFloatBetween(0.002f, 0.005f);

    private float height;
    private float radius;
    private float bladeRotation = 0, droneRootRotation = 0;

    private double[] eqn0 = {0, 0.0, 1.0, 0};
    private double[] eqn1 = {0, 0.0, -1.0, 0};
    private double[] eqn2 = {0, 0.5, 0.0, 0};

    private GLUT glut = new GLUT();
    private DroneComponent root;
    private DroneLightHolder droneLightHolder;
    private DroneArm droneRightArm, droneLeftArm;
    private DroneArmElbow droneRightArmElbow, droneLeftArmElbow;
    private DroneArmForearm droneArmForearm, droneLeftArmForearm;
    private Movement turningState = Movement.HOVER, horizontalMovementState = Movement.HOVER, verticalMovementState = Movement.HOVER;

    public Material materials = new Material();

    public Drone(float size) {
        radius = size * 0.5f;
        height = size * 0.25f;

        //Drone Body :: is the root
        root = new DroneBody(radius, height, Axis.X);

//        drawDroneLightHolder();
//        drawDroneLight();
        // Construct the right arm shape
        drawDroneRightArm();
        drawDroneRightArmElbow();
        drawDroneRightArmForearm();
        drawDroneArmBlades();

        // construct the left arm shape
        drawDroneLeftArm();
        drawDroneLeftArmElbow();
        drawDroneLeftArmForearm();
        drawDroneArmBlades();

    }

    private void drawDroneArmBlades() {
        DroneBlades droneBlades = new DroneBlades(radius, height, Axis.Y);
        droneBlades.setTranslations(0, height, 0);
        droneArmForearm.addChild(droneBlades);

        DroneBlades droneBlades1 = new DroneBlades(radius, height, Axis.Y);
        droneBlades1.setTranslations(0, height, 0);
        // rotate blade1 a further 90 degrees first so that they form the quadcopter shape.
        droneBlades1.setRotation(90);
        droneArmForearm.addChild(droneBlades1);
    }

    private void drawDroneRightArm() {
        droneRightArm = new DroneArm(radius, height, Axis.X);
        droneRightArm.setTranslations(height * 0.95f, 0, 0);
        root.addChild(droneRightArm);
    }

    private void drawDroneRightArmElbow() {
        droneRightArmElbow = new DroneArmElbow(radius, height, Axis.X);
        droneRightArmElbow.setTranslations(height * 0.95f, 0, 0);
        droneRightArm.addChild(droneRightArmElbow);
    }

    private void drawDroneRightArmForearm() {
        droneArmForearm = new DroneArmForearm(radius, height, Axis.X);
        droneArmForearm.setTranslations(0, 0, 0);
        droneRightArmElbow.addChild(droneArmForearm);
    }

    private void drawDroneLeftArm() {
        droneLeftArm = new DroneArm(radius, height, Axis.X);
        droneLeftArm.setTranslations(-height * 1.95, 0, 0);
        root.addChild(droneLeftArm);
    }

    private void drawDroneLeftArmElbow() {
        droneLeftArmElbow = new DroneArmElbow(radius, height, Axis.X);
        droneLeftArmElbow.setTranslations(0, 0, 0);
        droneLeftArm.addChild(droneLeftArmElbow);
    }

    private void drawDroneLeftArmForearm() {
        droneArmForearm = new DroneArmForearm(radius, height, Axis.X);
        droneArmForearm.setTranslations(0, 0, 0);
        droneLeftArmElbow.addChild(droneArmForearm);
    }

    private void drawDroneLightHolder() {
        droneLightHolder = new DroneLightHolder(radius, height, Axis.X);
        droneLightHolder.setTranslations(0, -height * 0.5, radius);
        root.addChild(droneLightHolder);
    }

    private void drawDroneLight() {
        DroneLight droneLight = new DroneLight(radius, height, Axis.X);
        droneLight.setTranslations(0, height * 1.3, radius * 0.65);
        droneLightHolder.addChild(droneLight);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getRotation() {
        return droneRootRotation;
    }

    @Override
    public void draw(GL2 gl, GLU glu, GLUquadric glUquadric, boolean filled) {
        gl.glPushMatrix();

        gl.glTranslated(x, y, z);
        gl.glRotated(0 + droneRootRotation, 0, 1, 0);

        root.draw(gl, glu, glUquadric, filled);
        gl.glPopMatrix();
    }

    public void animate(float speed) {
        bladeRotation += 4.0f;

        //If downwards state, then decrease Y axis
        //else upwards state then decrease Y axis
        //if state is hovering then we dont want to change y values
        //so no extra condition for hover state -> null state / do nothing 
        if (verticalMovementState == Movement.DOWNWARDS) {
            y -= 0.02f * speed;
        } else if (verticalMovementState == Movement.UPWARDS) {
            y += 0.02f * speed;
            // increase blade rotation speed by double
            // imitate increase in revolutions in rotors for increased lift (upwards movement)
            bladeRotation += 4.0f * speed;
        }

        if (turningState == Movement.RIGHT_TURN) {
            droneRootRotation -= 1.0f * speed;

        } else if (turningState == Movement.LEFT_TURN) {
            droneRootRotation += 1.0f * speed;
        }

        if (horizontalMovementState == Movement.FORWARD) {
            x += 0.05f * Math.sin(Math.toRadians(droneRootRotation));
            z += 0.05f * Math.cos(Math.toRadians(droneRootRotation));
            bladeRotation += 4.0f * speed;
        } else if (horizontalMovementState == Movement.BACKWARD) {
            x -= 0.05f * Math.sin(Math.toRadians(droneRootRotation));
            z -= 0.05f * Math.cos(Math.toRadians(droneRootRotation));

            // increase blade rotation speed by double
            // imitate increase in revolutions in rotors for increased lift (upwards movement)
            bladeRotation += 4.0f * speed;
        }


    }

    public void setHorizontalMovement(Movement horizontalMovementState) {
        this.horizontalMovementState = horizontalMovementState;
    }

    public void setTurningMovement(Movement turningState) {
        this.turningState = turningState;
    }

    public void setVerticalMovement(Movement verticalMovementState) {
        this.verticalMovementState = verticalMovementState;
    }

    public class DroneBody extends DroneComponent {

        DroneBody(double radius, double height, Axis axis) {
            super(radius, height, axis);
        }

        @Override
        public void drawNode(GL2 gl, GLU glu, GLUquadric glUquadric, boolean filled) {
            gl.glPushMatrix();
            Colour.setColourRGBA(white, gl);
            gl.glScaled(height, height, radius);
            glu.gluSphere(glUquadric, 1, 25, 20);
            gl.glPopMatrix();
        }
    }

    public class DroneLight extends DroneComponent {

        DroneLight(double radius, double height, Axis axis) {
            super(radius, height, axis);
        }

        @Override
        public void drawNode(GL2 gl, GLU glu, GLUquadric glUquadric, boolean filled) {
            gl.glPushMatrix();
            gl.glRotated(-45, 1, 0, 0);
            gl.glScaled(radius, radius, radius);
            glut.glutSolidCone(1, 1, 20, 10);
            gl.glPopMatrix();
        }
    }

    public class DroneLightHolder extends DroneComponent {

        DroneLightHolder(double radius, double height, Axis axis) {
            super(radius, height, axis);
        }

        @Override
        public void drawNode(GL2 gl, GLU glu, GLUquadric glUquadric, boolean filled) {
            gl.glPushMatrix();
            gl.glRotated(45, 1, 0, 0);
            gl.glScaled(radius * 0.1, radius * 0.1, radius * 0.3);
            glu.gluCylinder(glUquadric, 1, 1, 1, 10, 10);
            gl.glPopMatrix();
        }
    }

    public class DroneArm extends DroneComponent {

        DroneArm(double radius, double height, Axis axis) {
            super(radius, height, axis);
        }

        @Override
        public void drawNode(GL2 gl, GLU glu, GLUquadric glUquadric, boolean filled) {
            gl.glPushMatrix();

            Colour.setColourRGBA(fish, gl);

            gl.glRotated(90, 0, 1, 0);
            gl.glScaled(radius * 0.1, radius * 0.1, radius * 0.5);
            glu.gluCylinder(glUquadric, 1, 1, 1, 10, 10);

            gl.glPopMatrix();
        }
    }

    public class DroneArmElbow extends DroneComponent {

        DroneArmElbow(double radius, double height, Axis axis) {
            super(radius, height, axis);
        }

        @Override
        public void drawNode(GL2 gl, GLU glu, GLUquadric glUquadric, boolean filled) {
            gl.glPushMatrix();
            Colour.setColourRGBA(fish, gl);
            gl.glScaled(radius * 0.1, radius * 0.1, radius * 0.1);
            glu.gluSphere(glUquadric, 1, 25, 20);
            gl.glPopMatrix();
        }
    }

    public class DroneArmForearm extends DroneComponent {

        DroneArmForearm(double radius, double height, Axis axis) {
            super(radius, height, axis);
        }

        @Override
        public void drawNode(GL2 gl, GLU glu, GLUquadric glUquadric, boolean filled) {
            gl.glPushMatrix();

            Colour.setColourRGBA(fish, gl);

            gl.glRotated(-90, 1, 0, 0);
            gl.glScaled(radius * 0.1, radius * 0.1, radius * 0.5);
            glu.gluCylinder(glUquadric, 1, 1, 1, 10, 10);

            gl.glPopMatrix();
        }
    }

    private class DroneBlades extends DroneComponent {

        DroneBlades(double radius, double height, Axis axis) {
            super(radius, height, axis);
        }

        @Override
        public void drawNode(GL2 gl, GLU glu, GLUquadric glUquadric, boolean filled) {
            gl.glPushMatrix();
            gl.glRotated(bladeRotation, 0, 1, 0);
            gl.glScaled(radius / 5, radius / 8, 1);
            glu.gluSphere(glUquadric, 2 * radius / 3, 20, 20);
            gl.glPopMatrix();
        }
    }
}
