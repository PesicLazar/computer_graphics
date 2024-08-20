package ogl2.project;

// import all the necessary libraries

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.FPSAnimator;

public class OpenGlSquare implements GLEventListener{

    // define the window width, height, title, FPS
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 400;
    private static final String TITLE = "OpenGl Square";
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

        window.addGLEventListener(new OpenGlSquare()); // add this listener to the EventListener
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT); // set the W and H of the window
        window.setVisible(true); // make the window visible
        window.setTitle(TITLE); // set the title
        animator.start(); // starts the animator
    }

    //
    @Override
    public void init(GLAutoDrawable glAutoDrawable) {

        // get's the OpenGL graphics context
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // RGBA

        // used commonly within glBegin and glEnd to give each vertex a color
        gl.glColor3f(1.0f, 1.0f, 1.0f);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    // This section is called whenever we want to draw something onto our screen
    @Override
    public void display(GLAutoDrawable glAutoDrawable) {

        // draw a square
        drawSquare(glAutoDrawable);
    }

    private void drawSquare(GLAutoDrawable glAutoDrawable) {

        GL2 gl = glAutoDrawable.getGL().getGL2(); // get the OpenGL graphics context
        gl.glClear(GL.GL_COLOR_BUFFER_BIT); // clear the background

        // draw the shape or object in color red
        gl.glColor3d(1, 0, 0); //RGB

        gl.glBegin(GL2.GL_POLYGON);

            // glVertex commands are used within glBegin/glEnd
            // pairs to specify point, line, polygon vertices, etc
            gl.glVertex2d(1, 1); // x,y (coordinate)
            gl.glVertex2d(1, 3);
            gl.glVertex2d(3, 3);
            gl.glVertex2d(3, 1);

        gl.glEnd();
    }

    // this function is called by the GlAutoDrawable interface
    // during the first repaint after the component is resized
    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int w, int h) {

        GL2 gl = glAutoDrawable.getGL().getGL2();

        // set the view port (display area) to cover the entire screen
        gl.glViewport(Math.max(0, (w-h)/2),
                      Math.max(0, (h-w)/2),
                      Math.min(w, h),
                      Math.min(w, h));

        gl.glMatrixMode(GL2.GL_PROJECTION); // choose project matrix
        gl.glLoadIdentity(); // reset project matrix
        gl.glOrthof(0, 4, 4, 0, 0, 1); // left, right, buttom, top, near, far
    }
}
