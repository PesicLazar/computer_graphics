package ogl2.project;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.FPSAnimator;

public class OpenGlCylinder implements GLEventListener {

    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 400;
    private static final String TITLE = "OpenGl Cylinder";
    private static final int FPS = 60;

    public static void main(String[] args) {
        // gathers information about the current hardware and software configuration
        // and it provides access to the OpenGL API which allows us to render
        // graphics to our screen
        GLProfile glp = GLProfile.getDefault();

        // define the capabilites that a rendering context must support,
        // such as color depth
        GLCapabilities caps = new GLCapabilities(glp);

        // sets the number of bits requested for the color buffer's alpha component
        caps.setAlphaBits(8);

        // set the number of bits requested for the depth buffer
        caps.setDepthBits(24);

        // creates the window and pass the GLcapabilities in the create parameter
        GLWindow window = GLWindow.create(caps);
        final FPSAnimator animator = new FPSAnimator(window, FPS, true);

        // add the window listener and as an input, we created an anonymous
        // function.
        window.addWindowListener(new WindowAdapter() {

            // if the window has been requested to be closed, we'll stop the animator
            @Override
            public void windowDestroyNotify(WindowEvent windowEvent) {
                new Thread(){
                    @Override
                    public void run(){
                        animator.stop();
                        System.exit(0);
                    }
                }.start();
            }
        });

        window.addGLEventListener(new OpenGlCylinder()); // add this listener to the EventListener
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT); // set the W and H of the window
        window.setVisible(true); // make the window visible
        window.setTitle(TITLE); // set the title
        animator.start(); // starts the animator
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glEnable(GL.GL_DEPTH_TEST); // Enable depth testing
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
        // ... (No changes needed)
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity(); // Reset the modelview matrix

        // Set up the camera position and orientation
        double cameraX = 0;
        double cameraY = -1;  // Move the camera slightly downwards
        double cameraZ = -5;
        double centerX = 0;   // Look at the center of the scene
        double centerY = 0;
        double centerZ = 0;
        double upX = 0;
        double upY = 1;
        double upZ = 0;

        gluLookAt(gl, cameraX, cameraY, cameraZ, centerX, centerY, centerZ, upX, upY, upZ);

        // Example cylinder properties
        int numSegments = 32;
        double radius = 1.0;
        double height = 2.0;

        drawCylinder(gl, radius, height, numSegments);
    }

    // Define gluLookAt manually
    private void gluLookAt(GL2 gl, double eyeX, double eyeY, double eyeZ,
                           double centerX, double centerY, double centerZ,
                           double upX, double upY, double upZ) {
        double[] forward = {centerX - eyeX, centerY - eyeY, centerZ - eyeZ};
        double[] up = {upX, upY, upZ};

        double[] f = normalize(forward);

        /* Side = forward x up */
        double[] s = cross(f, up);
        double[] u = cross(s, f);

        double[] M = new double[16];
        M[0] = s[0];   M[4] = s[1];   M[8] = s[2];   M[12] = 0.0;
        M[1] = u[0];   M[5] = u[1];   M[9] = u[2];   M[13] = 0.0;
        M[2] = -f[0];  M[6] = -f[1];  M[10] = -f[2]; M[14] = 0.0;
        M[3] = 0.0;    M[7] = 0.0;    M[11] = 0.0;   M[15] = 1.0;

        gl.glMultMatrixd(M, 0);

        gl.glTranslated(-eyeX, -eyeY, -eyeZ);
    }

    // Helper method to normalize a vector
    private double[] normalize(double[] v) {
        double length = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        return new double[]{v[0] / length, v[1] / length, v[2] / length};
    }

    // Helper method to compute the cross product of two vectors
    private double[] cross(double[] a, double[] b) {
        return new double[]{a[1] * b[2] - a[2] * b[1],
                a[2] * b[0] - a[0] * b[2],
                a[0] * b[1] - a[1] * b[0]};
    }
    private void drawCylinder(GL2 gl, double radius, double height, int numSegments) {
        // Bottom Cap
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glColor3d(1, 0, 0); // Set color to red for bottom cap
        gl.glVertex3d(0.0, 0.0, 0.0);
        for (int i = 0; i <= numSegments; i++) {
            double angle = 2 * Math.PI * i / numSegments;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            gl.glVertex3d(x, 0.0, z);
        }
        gl.glEnd();

        // Top Cap
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glColor3d(1, 0, 0); // Set color to red for top cap
        gl.glVertex3d(0.0, height, 0.0);
        for (int i = 0; i <= numSegments; i++) {
            double angle = 2 * Math.PI * i / numSegments;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            gl.glVertex3d(x, height, z);
        }
        gl.glEnd();

        // Generate side vertices and normals
        for (int i = 0; i < numSegments; i++) {
            double angle = 2 * Math.PI * i / numSegments;
            double nextAngle = 2 * Math.PI * (i + 1) / numSegments;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            double nextX = radius * Math.cos(nextAngle);
            double nextZ = radius * Math.sin(nextAngle);

            // Calculate normal
            double normalX = (x + nextX) / 2;
            double normalZ = (z + nextZ) / 2;
            double normalLength = Math.sqrt(normalX * normalX + normalZ * normalZ);
            normalX /= normalLength;
            normalZ /= normalLength;

            // Side quad
            gl.glBegin(GL2.GL_QUAD_STRIP);
            gl.glColor3d(0, 1, 0); // Set color to green for side
            gl.glNormal3d(normalX, 0.0, normalZ); // Set normal for the side
            gl.glVertex3d(x, 0.0, z);
            gl.glVertex3d(x, height, z);
            gl.glVertex3d(nextX, 0.0, nextZ);
            gl.glVertex3d(nextX, height, nextZ);
            gl.glEnd();
        }
    }



    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int w, int h) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        // Example perspective projection
        double fov = 75.0;
        double aspect = (double) w / h;
        double near = 0.1;
        double far = 10.0;

        double top = Math.tan(Math.toRadians(fov / 2.0)) * near;
        double bottom = -top;
        double right = top * aspect;
        double left = -right;

        gl.glFrustum(left, right, bottom, top, near, far);  // Perspective projection

        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }
}
