package ogl2.project;

// import all the necessary libraries

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.FPSAnimator;


public class OpenGlTriangle implements GLEventListener{

    // define the window width, height, fps
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;
    private static final String TITLE = "OpenGL Triangle";

    private static final int FPS = 60;

    public static void main(String[] args) {

        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);

        caps.setAlphaBits(8);
        caps.setDepthBits(24);

        GLWindow window = GLWindow.create(caps);
        final FPSAnimator animator = new FPSAnimator(window, FPS, true);

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyNotify(WindowEvent windowEvent) {
               new Thread(){
                    public void run(){
                        animator.stop();
                        System.exit(0);
                    }
               }.start();
            }
        });

        window.addGLEventListener(new OpenGlTriangle());
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setVisible(true);
        window.setTitle(TITLE);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // set background
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        drawTriangle(glAutoDrawable);
    }

    private void drawTriangle(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3d(0, 1, 0);

        // draw the triangle
        gl.glBegin(GL.GL_TRIANGLES);
            gl.glVertex2f(-0.5f, -0.5f);
            gl.glVertex2f(0.5f, -0.5f);
            gl.glVertex2f(0f, 0.5f);
        gl.glEnd();
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int w, int h) {}
}
