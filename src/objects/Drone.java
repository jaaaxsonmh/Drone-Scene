package objects;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import component.Axis;
import component.DroneComponent;
import component.Movement;
import utils.Colour;
import utils.Drawable;
import utils.Material;

/**
 * @author Jack Hosking
 * studentID 16932920
 * scale: 1 unit : 1 meter
 */
public class Drone implements Drawable {

    private final Colour orange = new Colour(1.0f, 0.65490f, 0.14901f, 1.0f);
    private final Colour gray = new Colour(0.3f, 0.3f, 0.3f, 1.0f);
    public Material materials = new Material();
    private float x;
    private float y = 5;
    private float z;
    private float height;
    private float radius;
    private float bladeRotation = 0, droneRootRotation = 0;
    private DroneComponent root;
    private DroneArm droneRightArm, droneLeftArm;
    private DroneArmElbow droneRightArmElbow, droneLeftArmElbow;
    private DroneArmForearm droneArmForearm;
    private Movement turningState = Movement.HOVER, horizontalMovementState = Movement.HOVER, verticalMovementState = Movement.HOVER;

    public Drone(float size) {
        radius = size * 0.5f;
        height = size * 0.25f;

        //Drone Body :: is the root
        root = new DroneBody(radius, height, Axis.X);

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
        materials.clearMaterials(gl);
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
        }

        if (turningState == Movement.RIGHT_TURN) {
            droneRootRotation -= 1.0f * speed;
        } else if (turningState == Movement.LEFT_TURN) {
            droneRootRotation += 1.0f * speed;
        }

        if (horizontalMovementState == Movement.FORWARD) {
            x += 0.05f * Math.sin(Math.toRadians(droneRootRotation)) * speed;
            z += 0.05f * Math.cos(Math.toRadians(droneRootRotation)) * speed;
        } else if (horizontalMovementState == Movement.BACKWARD) {
            x -= 0.05f * Math.sin(Math.toRadians(droneRootRotation)) * speed;
            z -= 0.05f * Math.cos(Math.toRadians(droneRootRotation)) * speed;
        }

        if (horizontalMovementState != Movement.HOVER
                || turningState != Movement.HOVER
                || verticalMovementState != Movement.HOVER)
            bladeRotation += 4.0f * speed;
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
            gl.glScaled(height, height, radius);
            materials.setColourMaterial(gl, gray);
            glu.gluSphere(glUquadric, 1, 25, 20);
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


            gl.glRotated(90, 0, 1, 0);
            gl.glScaled(radius * 0.1, radius * 0.1, radius * 0.5);
            materials.setColourMaterial(gl, gray);
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
            gl.glScaled(radius * 0.1, radius * 0.1, radius * 0.1);
            materials.setColourMaterial(gl, gray);
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


            gl.glRotated(-90, 1, 0, 0);
            gl.glScaled(radius * 0.1, radius * 0.1, radius * 0.5);
            materials.setColourMaterial(gl, gray);
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
            materials.setColourMaterial(gl, orange);
            glu.gluSphere(glUquadric, 2 * radius / 3, 20, 20);
            gl.glPopMatrix();
        }
    }
}
