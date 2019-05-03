package objects;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import component.DroneComponent;
import utils.Colour;
import utils.Drawable;
import utils.Rand;


/**
 * @author Jack Hosking
 * studentID 16932920
 */

public class Drone implements Drawable {

    private final Colour fish = new Colour(1.0f, 0.65490f, 0.14901f, 1.0f);
    private final Colour sclera = new Colour(1.0f, 1.0f, 1.0f, 1.0f);
    private final Colour pupil = new Colour(0.0f, 0.0f, 0.0f, 1.0f);

    public float rotation = 0;
    public float x;
    public float y;
    public float z;
    private float vx = Rand.getFloatBetween(0.002f, 0.005f), vy = Rand.getFloatBetween(0.002f, 0.005f), vz = Rand.getFloatBetween(0.002f, 0.005f);

    private float height;
    public static float radius;
    private float tailRotation = 0;
    private boolean tailRotationFlipped = false;

    private double[] eqn0 = {0, 0.0, 1.0, 0};
    private double[] eqn1 = {0, 0.0, -1.0, 0};
    private double[] eqn2 = {0, 0.5, 0.0, 0};

    private DroneComponent root;

    public Drone(float size) {
        radius = size * 0.5f;
        height = size * 0.25f;

        //Drone Body :: is the root
        root = new FishBody(radius, height);

        drawleftFin();
        drawRightFin();
        drawLeftEye();
        drawRightEye();
        drawSail();

        FishTailTop tailTop = new FishTailTop(radius, height);
        tailTop.setTranslations(-radius, 0, 0);
        tailTop.setEqn(eqn2);
        root.addChild(tailTop);

        FishTailBottom tailBottom = new FishTailBottom(radius, height);
        tailBottom.setTranslations(0, 0, 0);
        double[] eqn3 = {0, -0.5, 0.0, 0};
        tailBottom.setEqn(eqn3);
        tailTop.addChild(tailBottom);

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
        return  rotation;
    }

    private void drawleftFin() {
        //Fin :: Child of body
        FishFinLeft leftFin = new FishFinLeft(radius, height);
        leftFin.setTranslations(0, 0, height * 0.8);
        leftFin.setEqn(eqn0);
        root.addChild(leftFin);
    }

    private void drawRightFin() {
        //Fin :: Child of Body
        FishFinRight rightFin = new FishFinRight(radius, height);
        rightFin.setTranslations(0, 0, -height * 0.8);
        rightFin.setEqn(eqn1);
        root.addChild(rightFin);
    }

    private void drawLeftEye() {
        //Left eye ::  Child of the body
        FishEye leftEyeSclera = new FishEye(radius, height);
        leftEyeSclera.setTranslations(radius * 0.8, height * 0.5, (radius * 2) * 0.1);
        root.addChild(leftEyeSclera);

        // Eye Pupil :: Child of the Sclera
        FishEyePupil leftEyePupil = new FishEyePupil(radius * 0.8, height * 0.8);
        leftEyePupil.setTranslations(height * 0.19, 0, 0);
        leftEyeSclera.addChild(leftEyePupil);
    }

    private void drawRightEye() {
        // Right eye ::  Child of the body
        FishEye rightEyeSclera = new FishEye(radius, height);
        rightEyeSclera.setTranslations(radius * 0.8, height * 0.5, -(radius * 2) * 0.1);
        root.addChild(rightEyeSclera);

        //Eye Pupil ::  Child of the Sclera
        FishEyePupil rightEyePupil = new FishEyePupil(radius * 0.8, height * 0.8);
        rightEyePupil.setTranslations(height * 0.19, 0, 0);
        rightEyeSclera.addChild(rightEyePupil);
    }

    private void drawSail() {
        //Sail front :: Child of Body
        FishSail frontSail = new FishSail(radius, height);
        frontSail.setTranslations(radius * 0.55, height * 0.7, 0);
        frontSail.setEqn(eqn2);
        root.addChild(frontSail);

        //Sail Second :: Child of first sail
        FishSail secondSail = new FishSail(radius, height);
        secondSail.setTranslations(-radius * 0.2, height * 0.1, 0);
        secondSail.setEqn(eqn2);
        frontSail.addChild(secondSail);

        //Sail Third :: Child of second sail
        FishSail thirdSail = new FishSail(radius, height);
        thirdSail.setTranslations(-radius * 0.2, height * 0.1, 0);
        thirdSail.setEqn(eqn2);
        secondSail.addChild(thirdSail);

        //Sail Fourth :: Child of third sail
        FishSail fourthSail = new FishSail(radius, height);
        fourthSail.setTranslations(-radius * 0.2, 0, 0);
        fourthSail.setEqn(eqn2);
        thirdSail.addChild(fourthSail);

        //Sail Fifth :: Child of fourth sail
        FishSail fifthSail = new FishSail(radius, height);
        fifthSail.setTranslations(-radius * 0.2, 0, 0);
        fifthSail.setEqn(eqn2);
        fourthSail.addChild(fifthSail);

        //Sail Sixth :: Child of fifth sail
        FishSail sixthSail = new FishSail(radius, height);
        sixthSail.setTranslations(-radius * 0.2, -height * 0.1, 0);
        sixthSail.setEqn(eqn2);
        fifthSail.addChild(sixthSail);
    }

    @Override
    public void draw(GL2 gl, GLU glu, GLUquadric glUquadric, boolean filled) {
        gl.glPushMatrix();

        gl.glTranslated(x, y, z);
        gl.glRotated(0 + rotation, 0, 1, 0);

        root.draw(gl, glu, glUquadric, filled);
        gl.glPopMatrix();
    }

    public void animate(float speed) {

        x += vx * speed;
        y += vy * speed;
        z += vz * speed;

        rotation += 0.3f;

        if (tailRotation >= 45) {
            tailRotationFlipped = false;
        } else if (tailRotation <= -45) {
            tailRotationFlipped = true;
        }


        if (tailRotationFlipped) {
            tailRotation += 1 * speed;
        } else {
            tailRotation -= 1 * speed;
        }

    }

    public class FishBody extends DroneComponent {
        FishBody(double radius, double height) {
            super(radius, height);
        }

        @Override
        public void drawNode(GL2 gl, GLU glu, GLUquadric glUquadric, boolean filled) {
            gl.glPushMatrix();
            Colour.setColourRGBA(fish, gl);
            gl.glScaled(radius, height, height);
            glu.gluSphere(glUquadric, 1, 25, 20);
            gl.glPopMatrix();
        }
    }

    public class FishFinLeft extends DroneComponent {
        FishFinLeft(double radius, double height) {
            super(radius, height);
        }

        @Override
        public void drawNode(GL2 gl, GLU glu, GLUquadric glUquadric, boolean filled) {
            gl.glPushMatrix();

            gl.glClipPlane(GL2.GL_CLIP_PLANE0, eqn, 0);
            gl.glEnable(GL2.GL_CLIP_PLANE0);
            Colour.setColourRGBA(fish, gl);
            gl.glRotated(tailRotation, 1, 0, 0);
            gl.glScaled(radius * 0.5, height * 0.1, height * 0.5);
            glu.gluSphere(glUquadric, 1, 25, 20);

            gl.glDisable(GL2.GL_CLIP_PLANE0);
            gl.glPopMatrix();
        }
    }

    public class FishFinRight extends DroneComponent {
        FishFinRight(double radius, double height) {
            super(radius, height);
        }

        @Override
        public void drawNode(GL2 gl, GLU glu, GLUquadric glUquadric, boolean filled) {
            gl.glPushMatrix();

            gl.glClipPlane(GL2.GL_CLIP_PLANE0, eqn, 0);
            gl.glEnable(GL2.GL_CLIP_PLANE0);
            Colour.setColourRGBA(fish, gl);
            gl.glRotated(-tailRotation, 1, 0, 0);
            gl.glScaled(radius * 0.5, height * 0.1, height * 0.5);
            glu.gluSphere(glUquadric, 1, 25, 20);

            gl.glDisable(GL2.GL_CLIP_PLANE0);
            gl.glPopMatrix();
        }
    }


    public class FishTailTop extends DroneComponent {
        FishTailTop(double radius, double height) {
            super(radius, height);
        }

        @Override
        public void drawNode(GL2 gl, GLU glu, GLUquadric glUquadric, boolean filled) {
            gl.glPushMatrix();

            gl.glClipPlane(GL2.GL_CLIP_PLANE0, eqn, 0);
            gl.glEnable(GL2.GL_CLIP_PLANE0);
            Colour.setColourRGBA(fish, gl);

            gl.glRotated(60, 0, 0, 1);
            gl.glRotated(tailRotation, 1, 0, 0);

            gl.glScaled(radius * 0.3, height * 1.2, height * 0.1);
            glu.gluSphere(glUquadric, 1, 25, 20);

            gl.glDisable(GL2.GL_CLIP_PLANE0);
            gl.glPopMatrix();
        }
    }

    public class FishTailBottom extends DroneComponent {
        FishTailBottom(double radius, double height) {
            super(radius, height);
        }

        @Override
        public void drawNode(GL2 gl, GLU glu, GLUquadric glUquadric, boolean filled) {
            gl.glPushMatrix();

            gl.glClipPlane(GL2.GL_CLIP_PLANE0, eqn, 0);
            gl.glEnable(GL2.GL_CLIP_PLANE0);
            Colour.setColourRGBA(fish, gl);

            gl.glRotated(-60, 0, 0, 1);
            gl.glRotated(-tailRotation, 1, 0, 0);

            gl.glScaled(radius * 0.3, height * 1.2, height * 0.1);
            glu.gluSphere(glUquadric, 1, 25, 20);

            gl.glDisable(GL2.GL_CLIP_PLANE0);
            gl.glPopMatrix();
        }
    }


    public class FishSail extends DroneComponent {
        FishSail(double radius, double height) {
            super(radius, height);
        }

        @Override
        public void drawNode(GL2 gl, GLU glu, GLUquadric glUquadric, boolean filled) {
            gl.glPushMatrix();

            gl.glClipPlane(GL2.GL_CLIP_PLANE0, eqn, 0);
            gl.glEnable(GL2.GL_CLIP_PLANE0);
            Colour.setColourRGBA(fish, gl);

            gl.glRotated(60, 0, 0, 1);
            gl.glScaled(radius * 0.1, height * 0.3, height * 0.1);
            glu.gluSphere(glUquadric, 1, 25, 20);

            gl.glDisable(GL2.GL_CLIP_PLANE0);
            gl.glPopMatrix();
        }
    }

    public class FishEye extends DroneComponent {
        FishEye(double radius, double height) {
            super(radius, height);
        }

        @Override
        public void drawNode(GL2 gl, GLU glu, GLUquadric glUquadric, boolean filled) {
            gl.glPushMatrix();
            Colour.setColourRGBA(sclera, gl);
            gl.glScaled(radius * 0.1, radius * 0.1, radius * 0.1);
            glu.gluSphere(glUquadric, 1, 25, 20);
            gl.glPopMatrix();
        }
    }

    public class FishEyePupil extends DroneComponent {
        FishEyePupil(double radius, double height) {
            super(radius, height);
        }

        @Override
        public void drawNode(GL2 gl, GLU glu, GLUquadric glUquadric, boolean filled) {
            gl.glPushMatrix();
            Colour.setColourRGBA(pupil, gl);
            gl.glScaled(radius * 0.02, radius * 0.04, radius * 0.04);
            glu.gluSphere(glUquadric, 1, 25, 20);
            gl.glPopMatrix();
        }
    }
}
