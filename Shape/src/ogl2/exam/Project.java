package ogl2.exam;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import com.sun.prism.impl.BufferUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Project extends GLCanvas implements GLEventListener, KeyListener, MouseListener {

    // create multiple checkboxes
    private JCheckBox lightOnOff;
    private JCheckBox globalAmbientLight;
    private JCheckBox diffuseLight;
    private JCheckBox specularLight;
    private JCheckBox ambientLight;

    // create multiple buttons
    private JButton removeButton;
    private JButton addButton;
    private JButton finishButton;
    private JButton helpButton;
    private JButton quitButton;
    private JButton newGameButton;

    // placeholder to display text
    private JLabel label;

    // the main gui where all the components like buttons,
    // checkboxes, etc.. are added
    private JFrame frame;

    // initialize the canvas for the window
    private GLCanvas canvas;
    private FPSAnimator animator;
    private int WINDOW_WIDTH = 640;
    private int WINDOW_HEIGHT = 480;
    private static final String TITLE = "The Shapes";
    private static final int FPS = 120;

    // provide access to opengl utility libraries like scaling, projections, etc..
    private GLU glu;

    // render bitmapped java 2d text unto our opengl window
    private TextRenderer textRenderer;
    private TextRenderer textMatch;

    // initialize our textures files
    private String [] textureFileNames = {
            "tower.png",
            "jerusalem.png",
            "background.jpg",
            "flag.png"
    };

    // help in loading textures from disk using opengl
    Texture [] textures = new Texture[textureFileNames.length];

    // initialize some global variables to randomly position our
    // shapes on the blueprint
    private int randomTop;
    private int randomLeft;
    private int randomRight;

    // initialize a nameId for picking the shapes
    private int nameId = 0;

    // id for the palette shapes insert to
    // the blueprint
    private int bigDome_idn = 0;
    private int cylinderSmall_idn = 0;
    private int lilDome_idn = 0;

    // color of the palette shape inserted into the blueprint
    private float addShapeRed = 0f;
    private float addShapeGreen = 0f;
    private float addShapeBlue = 0f;

    // default color for the shapes to be drawn
    private float defaultRed = 0.5f;
    private float defaultGreen = 0.5f;
    private float defaultBlue = 0.5f;

    private float redTop = defaultRed;
    private float greenTop = defaultGreen;
    private float blueTop = defaultBlue;

    private float redTopTwo = defaultRed;
    private float greenTopTwo = defaultGreen;
    private float blueTopTwo = defaultBlue;

    private float redLeft = defaultRed;
    private float greenLeft = defaultGreen;
    private float blueLeft = defaultBlue;

    private float redRight = defaultRed;
    private float greenRight = defaultGreen;
    private float blueRight = defaultBlue;

    private float redBottom = defaultRed;
    private float greenBottom = defaultGreen;
    private float blueBottom = defaultBlue;

    // the position of the shape on the blueprint
    private double topX;
    private double topY;
    private double topZ;
    private double topTwoX;
    private double topTwoY;
    private double topTwoZ;
    private double leftX;
    private double leftY;
    private double leftZ;
    private double rightX;
    private double rightY;
    private double rightZ;

    private double bottomX;
    private double bottomY;
    private double bottomZ;

    // initialize a variable to traverse through the blueprint
    private int traverse = 0;

    // a constant value for scaling the shapes in the blueprint (increase/decrease)
    private final float scaleDelta = 0.1f;

    // initialize the scale of the shapes inserted into the blueprint
    private float scaleTop = 0.5f;
    private float scaleLeft = 0.5f;
    private float scaleRight = 0.5f;

    // set the angle of the shapes inserted into the blueprint
    private int angleTopX = 90;
    private int angleTopY = 90;
    private int angleTopZ = 90;
    private int angleLeftX = 90;
    private int angleLeftY = 90;
    private int angleLeftZ = 90;
    private int angleRightX = 90;
    private int angleRightY = 90;
    private int angleRightZ = 90;


    // a constant to rotate the shapes inserted into the blueprint (at an angle)
    private float rotate = 1;

    // initialize the colors of the shape drawn on the palette
    private float paletteRed = 0.45f;
    private float paletteGreen = 0.20f;
    private float paletteBlue = 0.75f;

    // initialize the id for where the shapes will be placed
    private static final int TOP_ID = 1;
    private static final int LEFT_ID = 2;
    private static final int RIGHT_ID = 3;
    private static final int BOTTOM_ID = 4;
    private static final int TOP_TWO_ID = 5;


    // initialize variables for the shapes on the palette & blueprint
    Map<Integer, String> shapes = new HashMap<>();

    private static final int BigDome_ID = 1;
    private static final int CylinderSmall_ID = 2;
    private static final int LilDome_ID = 3;



    // total number of shapes (n+1)
    private static int TOTAL_NUM_OF_SHAPES = 0;

    // size of the buffer to store in memory, information about the selected shape
    private static final int BUFSIZE = 512;
    private IntBuffer selectBuffer;

    // used in selecting the object we want to draw on the blueprint
    // based on the (X,Y) coordinate
    private boolean inSelectionMode = false;
    private int xCursor = 0;
    private int yCursor = 0;

    // the current angle of the blueprint
    private int currentAngleOfRotationX = 0;
    private int currentAngleOfRotationY = 0;
    private int currentAngleOfVisibleField = 55; // camera

    // holds the value at which we want to rotate the shape
    private int angleDelta = 5;

    private float aspect; // calculate the aspect ratio of the background
    private float aspectP; // calculate the aspect ratio of the palette

    // indicate if we have finished the game or want to start a new game
    private boolean gameFinished = false;
    private boolean newGame = true;

    // translate the blueprint
    private float translateX;
    private float translateY;
    private float translateZ;

    // scale the shape added into the blueprint
    private float scale;
    private float scaleTopShape;
    private float scaleLeftShape;
    private float scaleRightShape;

    // manipulate the blueprint
    private double blueprintConstant = 0.1;

    // initialize our camera class
    private Camera camera;

    public Project(){

        // gathers information about the current hardware & software configuration
        // to allow us render graphics to our screen
        GLProfile profile = GLProfile.getDefault();

        // specify a set of capabilities that our rendering should support
        GLCapabilities caps = new GLCapabilities(profile);
        caps.setAlphaBits(8); // set the number of bits for the color buffer alpha component
        caps.setDepthBits(24); // set the number of bits for the depth buffer
        caps.setDoubleBuffered(true); // reduce flicking and provide smooth animation
        caps.setStencilBits(8); // mask pixels in an image to product special effects

        SwingUtilities.invokeLater(() -> {

            // create the openGL rendering canvas
            canvas = new GLCanvas();

            // set the desired frame size upon launch
            canvas.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

            // listen for functions to be called when a specific event happens
            canvas.addGLEventListener(this);
            canvas.addKeyListener(this); //receive keyboard events
            canvas.addMouseListener(this); // notify when the mouse state changes
            canvas.setFocusable(true); // get's the focus state of the component
            canvas.requestFocus(); // allow user input via the keyboard
            canvas.requestFocusInWindow(); // ensures the window gains focus once launched

            // initialize the FPSAnimator
            animator = new FPSAnimator(canvas, FPS, true);

            // initialize the jFrame constructor
            frame = new JFrame();

            // initialize the buttons, checkbox, labels and
            // set a preferred dimensions
            removeButton = new JButton("Remove");
            addButton = new JButton("Add");
            finishButton =  new JButton("Finish");
            quitButton = new JButton("Quit");
            helpButton = new JButton("Help");
            newGameButton = new JButton("New Game");

            removeButton.setPreferredSize(new Dimension(100, 20));
            addButton.setPreferredSize(new Dimension(100, 20));
            finishButton.setPreferredSize(new Dimension(100, 20));
            quitButton.setPreferredSize(new Dimension(100, 20));
            helpButton.setPreferredSize(new Dimension(100, 20));
            newGameButton.setPreferredSize(new Dimension(100, 20));

            // initialize the JLabel
            label = new JLabel("Click On The Help Button To Read Game Instructions");

            // initialize the JCheckbox
            lightOnOff = new JCheckBox("Turn Light On/Off", true);
            ambientLight = new JCheckBox("Ambient Light", false);
            globalAmbientLight = new JCheckBox("Global Ambient Light", false);
            specularLight = new JCheckBox("Specular Light", false);
            diffuseLight = new JCheckBox("Diffuse Light", false);

            // create a layout for the buttons (2,2) grid
            JPanel windowPanel = new JPanel();
            windowPanel.setLayout(new GridLayout(2, 2));

            // create the panel for the first row
            JPanel topPanel = new JPanel();
            topPanel.add(removeButton);
            topPanel.add(addButton);
            topPanel.add(globalAmbientLight);
            topPanel.add(lightOnOff);
            topPanel.add(ambientLight);
            topPanel.add(diffuseLight);
            topPanel.add(specularLight);

            windowPanel.add(topPanel);

            // create the panel for the second row
            JPanel bottomPanel = new JPanel();
            bottomPanel.add(label);
            bottomPanel.add(helpButton);
            bottomPanel.add(finishButton);
            bottomPanel.add(newGameButton);
            bottomPanel.add(quitButton);

            windowPanel.add(bottomPanel);

            // add the windowPanel to the frame
            frame.add(windowPanel, BorderLayout.SOUTH);

            // set the component to false so once it's clicked
            // an action is the performed
            ambientLight.setFocusable(false);
            lightOnOff.setFocusable(false);
            globalAmbientLight.setFocusable(false);
            diffuseLight.setFocusable(false);
            specularLight.setFocusable(false);

            addButton.addActionListener( e -> {
                // TODO: Implement to Pick Shapes from the Palette And Show Them on The Screen
                if(e.getSource() == addButton){
                    if(traverse == 1){
                        bigDome_idn = nameId;
                    } else if (traverse == 2) {
                        cylinderSmall_idn = nameId;
                    } else if (traverse == 3) {
                        lilDome_idn = nameId;
                    }
                }
                addButton.setFocusable(false);
            });

            removeButton.addActionListener(e -> {
                // TODO: Implement to Remove a Shapes from the Blueprint
                if(traverse == 1){
                    bigDome_idn = 0;
                } else if (traverse == 2) {
                     cylinderSmall_idn = 0;
                } else if (traverse == 3) {
                    lilDome_idn = 0;
                }
                removeButton.setFocusable(false);
            });

            finishButton.addActionListener(e -> {
                if(e.getSource() == finishButton){
                    gameFinished = true;
                    addShapeRed = 0;
                    addShapeGreen = 0.7f;
                    addShapeBlue = 1;
                    currentAngleOfVisibleField = 80;
                    translateY = -1;
                }
                finishButton.setFocusable(false);
            });

            helpButton.addActionListener( e -> {
                if (e.getSource() == helpButton) {

                    JOptionPane.showMessageDialog(frame, "Instructions: \n" +
                                    "W - traverse through the blueprint\n" +
                                    "A - reduce the scale of the shape inserted into the blueprint\n" +
                                    "S - increase the scale of the shape inserted into the blueprint\n" +
                                    "Z - increase the scale of the blueprint\n" +
                                    "X - reduce the scale of the blueprint\n" +
                                    "I - move the blueprint (translate) on the z-axis in positive direction\n" +
                                    "O - move the blueprint (translate) on the z-axis in negative direction\n" +
                                    "J - move the blueprint (translate) on the x-axis in positive direction \n" +
                                    "K - move the blueprint (translate) on the x-axis in negative direction\n" +
                                    "N - move the blueprint (translate) on the y-axis in positive direction\n" +
                                    "M - move the blueprint (translate) on the y-axis in negative direction\n" +


                                    "Add Button - after selecting a shape from the palette, you can add it to the selected blueprint shape by the Add button\n" +
                                    "Remove Button - after selecting a shape from the palette, you can remove it from the selected blueprint shape by the Remove button\n" +

                                    "Finish Button - after the game finished, by pressing on the finish button, you can see your results\n" +
                                    "New Game Button - generate a new game\n" +
                                    "Quit Button - quit from the game \n" +
                                    "Light - you can enable/disable different light models by checking/unchecking  the light chekboxes (global ambient light, ambient, diffuse and specular)\n" +


                                    "+ (Numerical Keypad 9)- zoom in\n" +
                                    "- (Numerical Keypad 9)- zoom out\n" +
                                    "Left arrow - negative rotation around the x-axis of the blueprint\n" +
                                    "Right arrow - positive rotation around the x-axis of the blueprint \n" +
                                    "Up arrow - negative rotation around the y-axis of the blueprint\n" +
                                    "Down arrow - positive rotation around the y-axis of the blueprint\n" +
                                    "1 (Numerical Keypad 1) - positive rotation around the x-axis of the shape inserted into the blueprint\n" +
                                    "3 (Numerical Keypad 3)- negative rotation around the x-axis of the shape inserted into the blueprint\n" +
                                    "4 (Numerical Keypad 4)- positive rotation around the y-axis of the shape inserted into the blueprint\n" +
                                    "6 (Numerical Keypad 6)- negative rotation around the y-axis of the shape inserted into the blueprint\n" +
                                    "7 (Numerical Keypad 7)- positive rotation around the z-axis of the shape inserted into the blueprint\n" +
                                    "9 (Numerical Keypad 9)- negative rotation around the z-axis of the shape inserted into the blueprint\n"
                            , "Help", JOptionPane.INFORMATION_MESSAGE);
                }
                helpButton.setFocusable(false);
            });

            quitButton.addActionListener(e -> {
                if(e.getSource() == quitButton){
                    animator.stop();
                    System.exit(0);
                }
                quitButton.setFocusable(false);
            });

            newGameButton.addActionListener(e -> {
                if(e.getSource() == newGameButton){
                    newGame = true;
                    gameFinished = false;
                }
                newGameButton.setFocusable(false);
            });

            frame.getContentPane().add(canvas);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    new Thread(() -> {
                        if(animator.isStarted()){
                            animator.stop();
                            System.exit(0);
                        }
                    }).start();
                }
            });

            // Initialize the camera, set the position of it
            camera = new Camera();
            camera.lookAt(-5, 0, 3, // look from camera XYZ
                    0, 0, 0, // look at the origin
                    0, 1, 0); // positive Y up vector (roll of the camera)

            // set the size of the shape while zoom in/out
            camera.setScale(15);

            // once the application starts, the window size will fit our
            // preferred layout
            frame.pack();
            frame.setTitle(TITLE);
            frame.setVisible(true);
            animator.start();
        });

    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();

        gl.glClearColor(0.95f, 0.95f, 1f, 0); // RGBA

        // enable the depth buffer to allow us represent depth information in 3d space
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_LIGHTING); // enable lighting calculation
        gl.glEnable(GL2.GL_LIGHT0); // initial value for light (1,1,1,1) -> RGBA
        gl.glEnable(GL2.GL_NORMALIZE);

        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, 1);
        gl.glMateriali(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 100);

        // initialize different light sources
        float [] ambient = {0.1f, 0.1f, 0.1f, 1.0f};
        float [] diffuse = {1.0f, 1.0f, 1.0f, 1.0f};
        float [] specular = {1.0f, 1.0f, 1.0f, 1.0f};

        // configure different light sources
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_SPECULAR, specular, 0);

        gl.glClearDepth(1.0f); // set clear depth value to farthest
        gl.glEnable(GL2.GL_DEPTH_TEST); // enable depth testing
        gl.glDepthFunc(GL2.GL_LEQUAL); // the type of depth test to do
        // perspective correction
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glShadeModel(GL2.GL_SMOOTH); // blend colors nicely & have smooth lighting

        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        // initialize the textures to use
        glu = GLU.createGLU(gl); // get Gl utilities

        for (int i=0; i<textureFileNames.length; i++){
            try{
                URL textureURL = getClass()
                        .getClassLoader()
                        .getResource("textures/"+textureFileNames[i]);

                if(textureURL != null){
                    BufferedImage image = ImageIO.read(textureURL);
                    ImageUtil.flipImageVertically(image);

                    textures[i] = AWTTextureIO.newTexture(GLProfile.getDefault(),
                            image,
                            true);

                    textures[i].setTexParameteri(gl,
                            GL2.GL_TEXTURE_WRAP_S,
                            GL2.GL_REPEAT);

                    textures[i].setTexParameteri(gl,
                            GL2.GL_TEXTURE_WRAP_T,
                            GL2.GL_REPEAT);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        // enables a texture by default
        textures[0].enable(gl);

        // initialize the font to use when rendering our text
        textRenderer = new TextRenderer(new Font("SansSerif", Font.PLAIN, 12));
        textMatch = new TextRenderer(new Font("SansSerif", Font.BOLD, 20));
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) { }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        // clears both the color and depth buffer before rendering
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        // check if we are in selection model
        if(inSelectionMode){
            // allow the user to pick a shape from the palette and add it to
            // the blueprint
            pickModels(glAutoDrawable);
        }else{
            palette(glAutoDrawable); // add the palette (left side)
            drawBlueprint(glAutoDrawable); // draws the blueprint (right side)
            drawBackground(glAutoDrawable); // draws the background (rainbow)
        }

        camera.apply(gl); // add the camera

        float [] zero = {0, 0, 0, 1};
        lights(gl, zero); // add the lights

        // turn on the global ambient light
        if(globalAmbientLight.isSelected()){ // if it's checked
            gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT,
                    new float[] {0.2F, 0.2F, 0.2F, 1},
                    0);

        }else{
            gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT,
                    zero,
                    0);
        }

        // add a specular light to make the object shiny
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK,
                GL2.GL_SPECULAR,
                new float[] {0.2F, 0.2F, 0.2F, 1}, 0);

        // check if the game is finished, print the total matched shapes
        if(gameFinished){
            printResult();
        }

        // print the match shapes, once we've inserted it correctly
        if(!gameFinished){
            printMatch();
        }

        // reset the game if the user decides
        if(newGame){
            newGame();
            newGame = false;
        }

    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int w, int h) {
        WINDOW_HEIGHT = h;
        WINDOW_WIDTH = w;
    }

    private void newGame() {

        // initialize the shapes to use & grab the total number
        shapes.put(0, " ");
        shapes.put(1, "BigDome");
        shapes.put(2, "CylinderSmall");
        shapes.put(3, "LilDome");

        TOTAL_NUM_OF_SHAPES = shapes.size()-1;

        // initialize for the shapes that we want to draw
        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 1; i <= TOTAL_NUM_OF_SHAPES; i++) {
            list.add(i);
        }

        // Add the shapes that needs to be drawn on our blueprint
        randomTop = list.get(0);
        randomLeft = list.get(1);
        randomRight = list.get(2);

        currentAngleOfVisibleField = 55;
        currentAngleOfRotationX = 0;
        currentAngleOfRotationY = 0;

        // reset the value for the blueprint
        translateX = 0;
        translateY = 0;
        translateZ = 0;

        scale = 1;
        nameId = 0;
        bigDome_idn = 0;
        lilDome_idn = 0;
        cylinderSmall_idn = 0;

        // reset/initialize the colors of the shapes inserted into the blueprint
        // as it changes once the user finishes the game
        addShapeRed = 1f;
        addShapeGreen = 0.7f;
        addShapeBlue = 0f;

        // remove the current selection
        redTop = defaultRed;
        greenTop = defaultGreen;
        blueTop = defaultBlue;

        redTopTwo = defaultRed;
        greenTopTwo = defaultGreen;
        blueTopTwo = defaultBlue;

        redLeft = defaultRed;
        greenLeft = defaultGreen;
        blueLeft = defaultBlue;

        redRight = defaultRed;
        greenRight = defaultGreen;
        blueRight = defaultBlue;

        redBottom = defaultRed;
        greenBottom = defaultGreen;
        blueBottom = defaultBlue;

        // position and scale for the template shapes on the blueprint
        topX = 0;
        topY = 2.0;
        topZ = 0;
        scaleTopShape = 1f;

        leftX = 0;
        leftY = 1.5;
        leftZ = 0;
        scaleLeftShape = 1f;

        rightX = 2.0;
        rightY = 2.0;
        rightZ = 0.0;
        scaleRightShape = 1f;

        // reset the pointer
        traverse = 0;

        // scale for the added shape on the blueprint (template)
        scaleTop = 0.5f;
        scaleLeft = 0.4f;
        scaleRight = 0.6f;

        // angle of the added shape on the blueprint (top, left....)
        angleTopX = 90;
        angleTopY = 90;
        angleTopZ = 90;

        angleLeftX = 90;
        angleLeftY = 90;
        angleLeftZ = 90;

        angleRightX = 90;
        angleRightY = 90;
        angleRightZ = 90;

    }
    private void lights(GL2 gl, float [] zero) {
        gl.glColor3d(0.5, 0.5, 0.5);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, zero, 0);

        if(lightOnOff.isSelected()){
            gl.glDisable(GL2.GL_LIGHTING);
        }else{
            gl.glEnable(GL2.GL_LIGHTING);
        }

        float [] ambient = {0.1f, 0.1f, 0.1f, 1};
        float [] diffuse = {1.0f, 1.0f, 1.0f, 1.0f};
        float [] specular = {1.0f, 1.0f, 1.0f, 1.0f};

        // ambient light
        if(ambientLight.isSelected()){
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, ambient, 0);
            gl.glEnable(GL2.GL_LIGHT0);
        }else{
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, zero, 0);
            gl.glDisable(GL2.GL_LIGHT0);
        }

        // diffuse light
        if(diffuseLight.isSelected()){
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, diffuse, 0);
            gl.glEnable(GL2.GL_LIGHT1);
        }else{
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, zero, 0);
            gl.glDisable(GL2.GL_LIGHT1);
        }

        // specular light
        if(specularLight.isSelected()){
            float [] shininess = {0.1f, 0.1f, 0.1f, 1};
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, specular, 0);
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, shininess, 0);
            gl.glEnable(GL2.GL_LIGHT2);
        }else{
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, zero, 0);
            gl.glDisable(GL2.GL_LIGHT2);
        }

        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, zero, 0);
    }
    private void drawBackground(GLAutoDrawable glAutoDrawable){
        try {
            GL2 gl = glAutoDrawable.getGL().getGL2();

            // define the characteristics of our camera such as clipping, point of view...
            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glLoadIdentity(); // reset the current matrix

            // set the window screen
            gl.glViewport(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

            // calculate the aspect ratio
            aspect = (float) WINDOW_HEIGHT / ((float) WINDOW_WIDTH);

            // define the orthographic view
            gl.glOrtho(
                    (float) -10/2, // left vertical clipping plane
                    (float) 10/2, // right vertical clipping plane
                    (-10*aspect) / 2, // bottom horizontal clipping plane
                    (10*aspect) / 2, // top horizontal clipping plane
                    0, // near depth clipping plane
                    100 // near farther clipping plane
            );

            // define the position orientation of the camera
            gl.glMatrixMode(GL2.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

            gl.glPushMatrix();
            gl.glEnable(GL2.GL_TEXTURE_2D);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
            gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);

            textures[3].bind(gl); // specify the texture to use

            gl.glTranslated(0, 0, -100);
            gl.glScalef(1.75f, 1, 1);
            gl.glColor3f(1f, 1f, 1f);

            double radius = 5;

            // add the texture to our background
            gl.glBegin(GL2.GL_POLYGON);
            gl.glNormal3f(0, 0, 1); // lighting calculation

            // top left corner of a square
            gl.glTexCoord2d(0, 1);
            gl.glVertex2d(-radius, radius);

            // bottom left corner of a square
            gl.glTexCoord2d(0, 0);
            gl.glVertex2d(-radius, -radius);

            // bottom right corner of a square
            gl.glTexCoord2d(1, 0);
            gl.glVertex2d(radius, -radius);

            // top right corner of a square
            gl.glTexCoord2d(1, 1);
            gl.glVertex2d(radius, radius);

            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glEnd();
            gl.glPopMatrix();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void drawBlueprint(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();

        // Define the point of view of the blueprint
        gl.glViewport(WINDOW_WIDTH / 8, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(currentAngleOfVisibleField,
                1.f * WINDOW_WIDTH / WINDOW_HEIGHT, 1, 100);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        setObserver();

        // Draw the blueprint
        gl.glPushMatrix();

        // Change the orientation of the blueprint
        gl.glTranslated(translateX, translateY, translateZ);
        gl.glScalef(scale, scale, scale);
        gl.glRotated(currentAngleOfRotationX, 1, 0, 0);
        gl.glRotated(currentAngleOfRotationY, 0, 1, 0);

        // Add some texture on the blueprint
        gl.glColor3f(1, 1, 1);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        // Bind and configure texture for cylinderBig
        textures[1].bind(gl);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        gl.glEnable(GL.GL_TEXTURE_2D);
        Shape.cylinderBig(gl); // This draws the base cylinder of the blueprint
        gl.glDisable(GL.GL_TEXTURE_2D);

        // Add tower shape next to cylinderBig with a different texture
        gl.glPushMatrix();
        gl.glTranslated(2.0, 0.0, 0.0); // Adjust the position as needed
        textures[0].bind(gl); // Binding tower.png for the tower
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        gl.glEnable(GL.GL_TEXTURE_2D);
        Shape.tower(gl);
        gl.glDisable(GL.GL_TEXTURE_2D); // Disable the texture after drawing the tower
        gl.glPopMatrix();

        // Draw random template shapes to be filled at different parts of the blueprint
        drawRandomShapeOnBlueprint(glAutoDrawable, 1); // Draw at on top
        drawRandomShapeOnBlueprint(glAutoDrawable, 2); // Draw at on left
        drawRandomShapeOnBlueprint(glAutoDrawable, 3); // Draw at on right

        // Allows the user to select a shape from the palette and deploy it onto the blueprint
        deployShapeFromPaletteToBlueprint(glAutoDrawable, 1); // Top side
        deployShapeFromPaletteToBlueprint(glAutoDrawable, 2); // Left side
        deployShapeFromPaletteToBlueprint(glAutoDrawable, 3); // Right side

        gl.glPopMatrix();
    }

    //here is were the new shape the ser make spawns
    private void deployShapeFromPaletteToBlueprint(GLAutoDrawable drawable, int choice){
        //  Allows the user to select a shape from the palette and deploy it unto the
        switch (choice){
            case 1: // top side
                addShapeToBlueprint(drawable,
                        addShapeRed, addShapeGreen, addShapeBlue, // colors
                        topX, topY, topZ, // translation
                        angleTopX, angleTopY, angleTopZ, // angle
                        scaleTop, rotate, // scale & rotate
                        bigDome_idn); // big dome shape to be drawn
                break;
            case 2: // left side
                addShapeToBlueprint(drawable,
                        0.4f, 0.6f, 1f, // colors
                        leftX, leftY, leftZ,  // translation
                        angleLeftX, angleLeftY, angleLeftZ, // angle
                        scaleLeft, rotate, // scale & rotate
                       cylinderSmall_idn); // shape to be drawn
                break;
            case 3: // right side
                addShapeToBlueprint(drawable,
                        0.4f, 0.6f, 1f, // colors
                        rightX, rightY, rightZ, // translation, adjust Y coordinate to be on top of the tower
                        angleRightX, angleRightY, angleRightZ, // angle
                        scaleRight, rotate, // scale & rotate
                        lilDome_idn); // shape to be drawn
                break;
            default:
                break;
        }
    }

    private void addShapeToBlueprint(GLAutoDrawable glAutoDrawable,
                                     float colorRed, float colorGreen, float colorBlue, // colors
                                     double tX, double tY, double tZ, // translation
                                     float angleX, float angleY, float angleZ, // angle
                                     float randomScale, float randomRotate, // scale & rotate
                                     int shapeId){ // shape to be drawn

        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glPushMatrix();
        gl.glColor3f(colorRed, colorGreen, colorBlue);
        gl.glTranslated(tX, tY, tZ);
        gl.glScalef(randomScale, randomScale, randomScale);
        gl.glRotatef(angleZ, 0, 0, randomRotate);
        gl.glRotatef(angleY, 0, randomRotate, 0);
        gl.glRotatef(angleX, randomRotate, 0, 0);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        pickShape(glAutoDrawable, shapeId);
        gl.glPopMatrix();
    }

    private void pickShape(GLAutoDrawable glAutoDrawable, int nameID){
        GL2 gl = glAutoDrawable.getGL().getGL2();
        switch (nameID){
            case BigDome_ID:
                Shape.bigDome(gl);
                break;
            case CylinderSmall_ID:
                Shape.cylinderSmall(gl);
                break;
            case LilDome_ID:
                Shape.lilDome(gl);
                break;
        }
    }

    //here are the blue print shape
    private void drawRandomShapeOnBlueprint(GLAutoDrawable drawable, int choice) {
        switch (choice){
            case 1: // draw random shape at the top
                drawRandomShapeOnBlueprint(drawable,
                        redTop, greenTop, blueTop, // the color
                        topX, topY, topZ, // translation
                        scaleTopShape, // the scale of the shape
                        randomTop); // the random shape to be drawn
                break;
            case 2: // draw random shape on the left side
                drawRandomShapeOnBlueprint(drawable,
                        redLeft, greenLeft, blueLeft, // the color
                        leftX, leftY, leftZ, // translation
                        scaleLeftShape, // the scale of the shape
                        randomLeft); // the random shape to be drawn
                break;
            case 3:
                drawRandomShapeOnBlueprint(drawable,
                        redRight, greenRight, blueRight, // the color
                        rightX, rightY, rightZ,  // translation to be on top of the tower
                        scaleRightShape, // the scale of the shape
                        randomRight); // the random shape to be drawn
                break;
        }
    }

    private void drawRandomShapeOnBlueprint(GLAutoDrawable drawable,
                                            float colorRed, float colorGreen, float colorBlue, // color
                                            double tX, double tY, double tZ,  // translation
                                            float randomScale, // scale
                                            int randomShape // random shape to be drawn
    ){
        GL2 gl = drawable.getGL().getGL2();

        if(randomRight == LilDome_ID){
            rightX = 2.0f;
        }

        // apply a consecutive series of transformations
        gl.glPushMatrix();
        gl.glColor3f(colorRed, colorGreen, colorBlue);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
        gl.glLineWidth(2); // define the width of the line
        gl.glTranslated(tX, tY, tZ);  // translate the shape
        gl.glScalef(randomScale, randomScale, randomScale); // scale it
        // draw a random shape at the top of the blueprint
        pickShape(drawable, randomShape);
        gl.glPopMatrix();
    }

    // set the position of the camera
    private void setObserver() {
        glu.gluLookAt(-1, 2, 10.0, // look from camera XYZ
                0.0, 0.0, 0.0, // look at the origin
                0.0, 1.0, 0.0); // positive Y up vector
    }

    // the palette drawn on the left side of the screen
    private void palette(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();

        // apply the subsequent matrix operation to the modelview matrix stack
        gl.glMatrixMode(GL2.GL_MODELVIEW); // convert local coordinates to world space
        gl.glLoadIdentity(); // reset the value

        // apply the subsequent matrix operation to the projection matrix stack
        gl.glMatrixMode(GL2.GL_PROJECTION); // add perspective to the current operation
        gl.glLoadIdentity();

        // specify the lower left of the viewport rectangle in pixel (0,0), width and height
        gl.glViewport(0, 0, WINDOW_WIDTH/3, WINDOW_HEIGHT);

        aspectP = (float) WINDOW_HEIGHT / ((float) WINDOW_WIDTH /3);

        // multiply the current matrix with an orthographic matrix
        gl.glOrtho(
                (float) -10/2, // left vertical clipping plane
                (float) 10 / 2, // right vertical clipping plane
                (-10 * aspectP) / 2, // bottom horizontal clipping plane
                (10 * aspectP) / 2, // top horizontal clipping plane
                1, // near depth clipping plane
                11 // near farther clipping plane
        );
        gl.glMatrixMode(GL2.GL_MODELVIEW);

        // draw the background of the palette
        paletteBackground(glAutoDrawable);
        gl.glLoadIdentity();

        // set the camera for the palette
        glu.gluLookAt(-1, 2, 10.0, // look from camera XYZ
                0.0, 0.0, 0.0, // look at the origin
                0.0, 1.0, 0.0); // positive Y up vector

        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        // draw the shape on top of the background palette
        drawPaletteShape(glAutoDrawable, 1); // draws a big dome
        drawPaletteShape(glAutoDrawable, 2); // draws a CylinderSmall
        drawPaletteShape(glAutoDrawable, 3); // draws a lil dome
    }
    private void paletteBackground(GLAutoDrawable glAutoDrawable) {
        try {
            GL2 gl = glAutoDrawable.getGL().getGL2(); // get the openGL graphics context
            gl.glPushMatrix();

            // enable the server-side GL capabilities for texture
            gl.glEnable(GL2.GL_TEXTURE_2D);

            // set different texture parameters
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
            gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);

            // set the texture to use
            textures[2].bind(gl);

            gl.glTranslated(-1.35f, -2f, -10f);
            gl.glScalef(3.5f, 5f, 0f);
            gl.glColor3f(1f, 1f, 1f);

            Shape.square(gl, 2, true); //this is for the menue pic

            gl.glDisable(GL2.GL_TEXTURE_2D);

            gl.glPopMatrix();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void drawPaletteShape(GLAutoDrawable drawable, int shapeChoice){
        switch (shapeChoice){
            case BigDome_ID: // draw a dome
                paletteShape(drawable, paletteRed, paletteGreen, paletteBlue,
                        -2.0f, -3.5f, 0f, 1f, 1);
                break;
            case CylinderSmall_ID: // draw a CylindersSmall
                paletteShape(drawable, paletteRed, paletteGreen, paletteBlue,
                        -2.0f, -1.5f, 0f, 0.8f,2);
                break;
            case LilDome_ID: // draw a cone
                paletteShape(drawable, paletteRed, paletteGreen, paletteBlue,
                        -2.0f, 0.5f, 0f, 1f,3);
                break;
        }
    }
    private void paletteShape(GLAutoDrawable drawable,
                              float colorRed, float colorGreen, float colorBlue, // color to display
                              float tX, float tY, float tZ, // translation
                              float randomScale, // the size of the shape
                              int shapeId // the choice of shape
    ){
        GL2 gl = drawable.getGL().getGL2();
        gl.glColor3f(colorRed, colorGreen, colorBlue);
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);

        // control the transformation applied to the object
        gl.glPushMatrix();
        gl.glTranslated(tX, tY, tZ); // add the translation matrix
        gl.glScalef(randomScale, randomScale, randomScale);
        pickShape(drawable, shapeId);
        gl.glPopMatrix();
    }

    /* Allows the user to picks object from the palette and draw it on the blueprint */
    private void pickModels(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();

        // start picking objects from the screen
        startPicking(glAutoDrawable);

        // enables the user pick objects from the palette and
        // deploy them to the blueprint
        palettePicking(glAutoDrawable);

        // TODO: For any new shape, you'll need to register them here
        gl.glPushName(BigDome_ID);
        drawPaletteShape(glAutoDrawable, 1); // draws a BigDome
        gl.glPopName();

        gl.glPushName(CylinderSmall_ID);
        drawPaletteShape(glAutoDrawable, 2); // draws a CylinderSmall
        gl.glPopName();

        gl.glPushName(LilDome_ID);
        drawPaletteShape(glAutoDrawable, 3); // draws a LilDome
        gl.glPopName();

        // we are done picking
        endPicking(glAutoDrawable);
    }
    private void startPicking(GLAutoDrawable glAutoDrawable){
        GL2 gl = glAutoDrawable.getGL().getGL2();
        // determine which shape are to be drawn on the blueprint
        selectBuffer = BufferUtil.newIntBuffer(BUFSIZE);
        gl.glSelectBuffer(BUFSIZE, selectBuffer);
        gl.glRenderMode(GL2.GL_SELECT);
        gl.glInitNames();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }
    private void palettePicking(GLAutoDrawable glAutoDrawable){
        GL2 gl = glAutoDrawable.getGL().getGL2();

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();

        gl.glLoadIdentity();
        int [] viewport = new int[4];
        float [] projectionMatrix = new float[16];

        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        viewport[0] = 0;
        viewport[1] = 0;
        viewport[2] = WINDOW_WIDTH / 3;
        viewport[3] = WINDOW_HEIGHT;

        gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, projectionMatrix, 0);

        // define the picking region
        glu.gluPickMatrix((double) xCursor,
                (double) (viewport[3] - yCursor),
                1.0,
                1.0,
                viewport,
                0);
        gl.glMultMatrixf(projectionMatrix, 0);
        gl.glOrtho((float) -10/2,
                (float) 10/2,
                (-10*aspectP) / 2,
                (10*aspectP) / 2,
                1,
                11);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        glu.gluLookAt(-1, 2, 10.0,
                0.0, 0.0, 0.0,
                0.0, 1.0, 0.0);
        gl.glPopMatrix();
    }
    private void endPicking(GLAutoDrawable glAutoDrawable){
        GL2 gl = glAutoDrawable.getGL().getGL2();

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glFlush();

        int numHits = gl.glRenderMode(GL2.GL_RENDER);
        processHits(glAutoDrawable, numHits);
        inSelectionMode = false;
    }
    private void processHits(GLAutoDrawable glAutoDrawable, int numHits) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        if(numHits == 0)  return;

        // store the Id's for what was selected
        int selectedNameId = 0;
        float smallestZ = -1.0f;
        boolean isFirstLoop = true;
        int offset = 0;

        for (int i = 0; i < numHits; i++) {
            int numNames = selectBuffer.get(offset);
            offset++;

            float minZ = getDepth(offset);
            offset++;

            // store the smallest z value
            if(isFirstLoop){
                smallestZ = minZ;
                isFirstLoop = false;
            }else{
                if(minZ < smallestZ){
                    smallestZ = minZ;
                }
            }

            float maxZ = getDepth(offset);
            offset++;

            for (int j = 0; j < numNames; j++) {
                nameId = selectBuffer.get(offset);
                System.out.println(idToString(nameId)+" \n");
                if(j == (numNames - 1)){
                    if(smallestZ == minZ){
                        selectedNameId = nameId;
                    }
                }
                offset++;
            }
        }
    }
    private String idToString(int nameId){
        // TODO: Update for new shapes
        if(nameId == BigDome_ID){
            return "palette_BigDome";
        }else if(nameId == CylinderSmall_ID){
            return "palette_CylinderSmall";
        }else if(nameId == LilDome_ID){
            return "palette_LilDome";
        } else{
            return "nameId: "+nameId;
        }
    }
    private float getDepth(int offset){
        long depth = (long) selectBuffer.get(offset);
        return (1.0f + ((float) depth / 0x7fffffff)); // 7'fs
    }
    private void colorShape(int traverse){
        // TODO: Ensure to update for selecting different templates when the key 'W' is pressed
        switch (traverse){
            case TOP_ID:
                redTop = 1;
                redTopTwo = 0;
                redLeft = 0;
                redRight = 0;
                redBottom = 0;
                break;
            case LEFT_ID:
                redTop = 0;
                redTopTwo = 0;
                redLeft = 1;
                redRight = 0;
                redBottom = 0;
                break;
            case RIGHT_ID:
                redTop = 0;
                redTopTwo = 0;
                redLeft = 0;
                redRight = 1;
                redBottom = 0;
                break;
            default:
                redLeft = 0;
                redTopTwo = 0;
                redTop = 0;
                redRight = 0;
                redBottom = 0;
                break;
        }
    }


    /* Valid the matched shapes and display to screen*/

    // check if the user has correctly added a shape from the palette to the blueprint
    // the scale, rotation, etc... is valid
    private void printMatch() {
        if(traverse == 1){
            // check if the shape matched
            boolean isShapeMatched = shapes.get(randomTop).equals(shapes.get(bigDome_idn));

            // check if the scale for the top shape matches
            String isScaleMatched = scaleCheck(scaleTop);

            // check if the rotation matches
            String isRotationMatched = rotationCheck(randomTop, angleTopX, angleTopY, angleTopZ);

            if(isShapeMatched & isScaleMatched.equals("appropriate") & isRotationMatched.equals("correct")){
                writeMatch(
                        (int) (WINDOW_WIDTH / 4f),
                        WINDOW_HEIGHT - 40);
            }
        }
        else if (traverse == 2) {
            boolean isShapeMatched = shapes.get(randomLeft).equals(shapes.get(cylinderSmall_idn)); // check if the shape matched
            String isRotationMatched = rotationCheck(randomLeft, angleLeftX, angleLeftY, angleLeftZ); // is rotation correct
            String isScaleMatched = scaleCheck(scaleLeft); // is scale appropriate

            if(isShapeMatched & isScaleMatched.equals("appropriate") & isRotationMatched.equals("correct")){
                writeMatch(
                        (int) (WINDOW_WIDTH / 4f),
                        WINDOW_HEIGHT - 40);
            }
        }else if(traverse == 3){
            boolean isShapeMatched = shapes.get(randomRight).equals(shapes.get(lilDome_idn)); // check if the shape matches
            String isRotationMatched = rotationCheck(randomRight,
                    angleRightX,
                    angleRightY,
                    angleRightZ); // is rotation correct
            String isScaleMatched = scaleCheck(scaleRight); // is scale appropriate

            if(isShapeMatched & isScaleMatched.equals("appropriate") & isRotationMatched.equals("correct")){
                writeMatch(
                        (int) (WINDOW_WIDTH / 4f),
                        WINDOW_HEIGHT - 40);
            }

        }
    }
    private void writeMatch(int x, int y){
        textMatch.beginRendering(WINDOW_WIDTH, WINDOW_HEIGHT);
        textMatch.setColor(0.3f, 0.3f, 0.3f, 1);
        textMatch.draw("Well done! Correct Shape, Rotation & Scaling", x, y);
        textMatch.endRendering();
    }
    private String scaleCheck(float value) {
        double scaling = Math.round(value * 100.0) / 100.0;
        String text = "";
        if(scaling == 1.0){
            text = "appropriate";
        }else{
            text = "not appropriate";
        }
        return text;
    }
    private String rotationCheck(int shape, int angleX, int angleY, int angleZ) {
        String text = "";
        if(shape == BigDome_ID){ // cube
            // rotation: x, y, z
            boolean checkX = (angleX == 0 || angleX == 90 || angleX == 180 || angleX == 270 || angleX == 360);
            boolean checkY = (angleY == 0 || angleY == 90 || angleY == 180 || angleY == 270 || angleY == 360);
            boolean checkZ = (angleZ == 0 || angleZ == 90 || angleZ == 180 || angleZ == 270 || angleZ == 360);

            if(checkX & checkY & checkZ){
                text = "correct";
            }else{
                text = "incorrect";
            }
        } else if (shape == CylinderSmall_ID) { // check for CylinderSmall
            // rotation: x, y, z
            boolean checkX = (angleX == 0 || angleX == 90 || angleX == 180 || angleX == 270 || angleX == 360);
            boolean checkY = (angleY == 0 || angleY == 180 || angleY == 360);
            boolean checkZ = (angleZ == 0 || angleZ == 180 || angleZ == 360);

            if(checkX & checkY & checkZ){
                text = "correct";
            }else{
                text = "incorrect";
            }
        }else if(shape == LilDome_ID){ // check for lil dome
            boolean checkX = (angleX == 0);
            boolean checkY = (angleY == 0 || angleY == 90 || angleY == 180 || angleY == 270 || angleY == 360);
            boolean checkZ = (angleZ == 0 || angleZ == 180 || angleZ == 360);

            boolean firstCheck = checkX && checkY && checkZ;
            boolean secondCheck = (angleX == 90 && angleY == 90 && angleZ == 90);
            boolean thirdCheck = (angleX == 90 && angleY == 270 || angleZ == 270);
            boolean fourthCheck = (angleX == 180 && checkY && angleZ == 180);

            if(firstCheck || secondCheck || thirdCheck || fourthCheck){
                text = "correct";
            }else{
                text = "incorrect";
            }
        }
        return text;
    }
    private void printResult(){
        String text = String.format("RESULT: %d/%d shapes matched correctly", matchedShape(), TOTAL_NUM_OF_SHAPES);
        writeText(text, (int) (WINDOW_WIDTH/3.5f), WINDOW_HEIGHT-40);
    }
    private void writeText(String text, int x, int y){
        textRenderer.beginRendering(WINDOW_WIDTH, WINDOW_HEIGHT);
        textRenderer.setColor(0.3f, 0.3f, 0.5f, 1);
        textRenderer.draw(text, x, y);
        textRenderer.endRendering();
    }
    private int matchedShape(){
        // TODO: Add a check for a particular placeholder to see if the correct shape was matched or not
        int match = 0;
        boolean isTopMatched = shapes.get(randomTop).equals(shapes.get(bigDome_idn));
        boolean isLeftMatched = shapes.get(randomLeft).equals(shapes.get(cylinderSmall_idn));
        boolean isRightMatched = shapes.get(randomRight).equals(shapes.get(lilDome_idn));
        if(isTopMatched) match++;
        if(isLeftMatched) match++;
        if(isRightMatched) match++;
        return match;
    }

    /* Event listeners for keyboard, mouse clicks, etc..*/
    @Override
    public void keyTyped(KeyEvent e) { }
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode(); // keyboard code for the pressed key

        // traverse through the blueprint
        if(key == KeyEvent.VK_W){
            traverse = traverse + 1;
            colorShape(traverse);
            if(traverse == TOTAL_NUM_OF_SHAPES+1){
                traverse = 0;
            }
        }
        // ============== BLUEPRINT CONTROLS =================
        else if(key == KeyEvent.VK_Z){ // increase the scale of the blueprint
            if(scale <= 2.1){
                scale += blueprintConstant;
            }
        }
        else if(key == KeyEvent.VK_X){ // decrease the scale of the blueprint
            if(scale >= 0.1){
                scale -= blueprintConstant;
            }
        }
        // move the blueprint (translate) on the z-axis in the positive direction
        else if(key == KeyEvent.VK_I){
            translateZ += blueprintConstant;
        }
        // move the blueprint (translate) on the z-axis in the negative direction
        else if (key == KeyEvent.VK_O) {
            translateZ -= blueprintConstant;
        }
        // move the blueprint (translate) on the x-axis in the positive direction
        else if (key == KeyEvent.VK_J) {
            translateX += blueprintConstant;
        }
        // move the blueprint (translate) on the x-axis in the negative direction
        else if (key == KeyEvent.VK_K) {
            translateX -= blueprintConstant;
        }
        // move the blueprint (translate) on the y-axis in the positive direction
        else if(key == KeyEvent.VK_N){
            translateY += blueprintConstant;
        }
        // move the blueprint (translate) on the y-axis in the negative direction
        else if(key == KeyEvent.VK_M){
            translateY -= blueprintConstant;
        }
        // zoom into our blueprint (The Z axis contains the point of view of the object)
        else if(key == KeyEvent.VK_ADD){
            if(currentAngleOfVisibleField > 10){
                currentAngleOfVisibleField--;
            }
        }
        // zoom out of our blueprint
        else if(key == KeyEvent.VK_SUBTRACT){
            if(currentAngleOfVisibleField < 175){
                currentAngleOfVisibleField++;
            }
        }
        // negative rotation around the x-axis of the blueprint
        else if(key == KeyEvent.VK_LEFT){
            currentAngleOfRotationY++;
        }
        // positive rotation around the x-axis of the blueprint
        else if(key == KeyEvent.VK_RIGHT){
            currentAngleOfRotationY--;
        }
        // negative rotation around the y-axis of the blueprint
        else if(key == KeyEvent.VK_UP){
            currentAngleOfRotationX--;
        }
        // positive rotation around the y-axis of the blueprint
        else if(key == KeyEvent.VK_DOWN){
            currentAngleOfRotationX++;
        }

        // =============== SHAPES ADDED INTO THE BLUEPRINT =====================
        // TODO: Ensure to provide support to scale, rotate the new template shape added to your blueprint
        else if(key == KeyEvent.VK_A){ // reduce the scale of the shape inserted into the blueprint
            if(traverse == 1){
                scaleTop -= scaleDelta;
            } else if (traverse == 2) {
                scaleLeft -= scaleDelta;
            } else if (traverse == 3) {
                scaleRight -= scaleDelta;
            }
        }
        else if(key == KeyEvent.VK_S){ // increase the scale of the shape inserted into the blueprint
            if(traverse == 1){
                scaleTop += scaleDelta;
            } else if (traverse == 2) {
                scaleLeft += scaleDelta;
            } else if (traverse == 3) {
                scaleRight += scaleDelta;
            }
        }

        // (Numerical Keypad 1) - positive rotation around the x-axis of the shape inserted into the blueprint
        else if(key == KeyEvent.VK_NUMPAD1){
            if(traverse == 1){
                if(angleTopX <= 360){
                    angleTopX += angleDelta;
                }
            } else if (traverse == 2) {
                if(angleLeftX <= 360){
                    angleLeftX += angleDelta;
                }
            }else if(traverse == 3){
                if(angleRightX <= 360){
                    angleRightX += angleDelta;
                }
            }

        }

        // (Numerical Keypad 3) - negative rotation around the x-axis of the shape inserted into the blueprint
        else if(key == KeyEvent.VK_NUMPAD3){
            if(traverse == 1){
                if(angleTopX >= 0){
                    angleTopX -= angleDelta;
                }
            } else if (traverse == 2) {
                if(angleLeftX >= 0){
                    angleLeftX -= angleDelta;
                }
            }else if(traverse == 3){
                if(angleRightX >= 0){
                    angleRightX -= angleDelta;
                }
            }

        }

        // (Numerical Keypad 4) - positive rotate around the y-axis of the shape inserted into the blueprint
        else if(key == KeyEvent.VK_NUMPAD4){
            if(traverse == 1){
                if(angleTopY <= 360){
                    angleTopY += angleDelta;
                }
            } else if (traverse == 2) {
                if(angleLeftY <= 360){
                    angleLeftY += angleDelta;
                }
            }else if(traverse == 3){
                if(angleRightY <= 360){
                    angleRightY += angleDelta;
                }
            }

        }

        // (Numerical Keypad 6) - negative rotation around the y-axis of the shape inserted into the blueprint
        else if(key == KeyEvent.VK_NUMPAD6){
            if(traverse == 1){
                if(angleTopY >= 0){
                    angleTopY -= angleDelta;
                }
            } else if (traverse == 2) {
                if(angleLeftY >= 0){
                    angleLeftY -= angleDelta;
                }
            } else if (traverse == 3){
                if(angleRightY >= 0){
                    angleRightY -= angleDelta;
                }
            }
        }

        // (Numerical Keypad 7) - positive rotation around the z-axis of the shape inserted into the blueprint
        else if(key == KeyEvent.VK_NUMPAD7){
            if(traverse == 1){
                if(angleTopZ <= 360){
                    angleTopZ += angleDelta;
                }
            } else if (traverse == 2) {
                if(angleLeftZ <= 360){
                    angleLeftZ += angleDelta;
                }
            } else if(traverse == 3){
                if(angleRightZ <= 360){
                    angleRightZ += angleDelta;
                }
            }
        }

        // (Numerical Keypad 9) - negative rotation around the z-axis of the shape inserted into the blueprint
        else if(key == KeyEvent.VK_NUMPAD9){
            if(traverse == 1){
                if(angleTopZ >= 0){
                    angleTopZ -= angleDelta;
                }
            } else if (traverse == 2) {
                if(angleLeftZ >= 0){
                    angleLeftZ -= angleDelta;
                }
            } else if (traverse == 3) {
                if(angleRightZ >= 0){
                    angleRightZ -= angleDelta;
                }
            }
        }

        // Escape key - stop the animator and exit the game
        else if(key == KeyEvent.VK_ESCAPE){
            animator.stop();
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }
    @Override
    public void mouseClicked(MouseEvent mouseEvent) { }
    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        switch (mouseEvent.getButton()){
            case MouseEvent.BUTTON1: { // left click
                xCursor = mouseEvent.getX();
                yCursor = mouseEvent.getY();
                inSelectionMode = true;
                break;
            }
        }
    }
    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }
    @Override
    public void mouseEntered(MouseEvent mouseEvent) { }
    @Override
    public void mouseExited(MouseEvent mouseEvent) { }

    public static void main(String[] args) {
        new Project();
    }
}
