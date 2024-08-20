package ogl2.exam;

import com.jogamp.opengl.GL2;

public class Shape {
    public static void cylinderBig(GL2 gl) {
        cylinderBig(gl, 2, 1.5, 16, 10, 5, true);
    }
    public static void tower(GL2 gl) {
        tower(gl, 0.2, 2, 16, 10, 5, true);
    }
    public static void cylinderSmall(GL2 gl) {
        cylinderSmall(gl, 1, 0.5, 16, 5, 5, true);
    }
    public static void bigDome(GL2 gl) {
        bigDome(gl, 1, 10, 10, true);
    } // draw the big dome

    public static void lilDome(GL2 gl) {
        lilDome(gl, 0.2, 10, 10, true);
    }

    public static void lilDome(GL2 gl, double radius, int slices, int stacks, boolean makeTextureCoordinate) {
        double x, y, z, theta;
        double dtheta = 2.0 * Math.PI / slices;

        // Draw the cone sides
        for (int i = 0; i < stacks; i++) {
            double h1 = (double)i / stacks;
            double h2 = (double)(i + 1) / stacks;
            double r1 = radius * (1 - h1);
            double r2 = radius * (1 - h2);

            gl.glBegin(GL2.GL_QUAD_STRIP);
            for (int j = 0; j <= slices; j++) {
                theta = j * dtheta;

                x = Math.cos(theta);
                z = Math.sin(theta);
                y = h1;

                if (makeTextureCoordinate) {
                    gl.glTexCoord2d((double) j / slices, h1);
                }
                gl.glNormal3d(x, y, z);
                gl.glVertex3d(r1 * x, h1, r1 * z);

                y = h2;

                if (makeTextureCoordinate) {
                    gl.glTexCoord2d((double) j / slices, h2);
                }
                gl.glNormal3d(x, y, z);
                gl.glVertex3d(r2 * x, h2, r2 * z);
            }
            gl.glEnd();
        }

        // Draw the flat bottom
        gl.glBegin(GL2.GL_POLYGON);
        for (int j = 0; j <= slices; j++) {
            theta = j * dtheta;
            x = radius * Math.cos(theta);
            z = radius * Math.sin(theta);
            y = 0;

            if (makeTextureCoordinate) {
                gl.glTexCoord2d((double) j / slices, 0);
            }
            gl.glNormal3d(0, -1, 0); // Normal pointing down
            gl.glVertex3d(x, y, z);
        }
        gl.glEnd();
    }

    // build the big cylinder
    public static void cylinderBig(GL2 gl, double radius, double height, int slices, int stacks, int rings,
                                boolean makeTextureCoordinates) {
        if (radius <= 0) throw new IllegalArgumentException("Radius must be positive");
        if (height <= 0) throw new IllegalArgumentException("Height must be positive");
        if (slices < 3) throw new IllegalArgumentException("Number of slices must be at least 3.");
        if (stacks < 2) throw new IllegalArgumentException("Number of stacks must be at least 2.");

        // body
        for (int j = 0; j < stacks; j++) {
            double x1 = (height / stacks) * j;
            double x2 = (height / stacks) * (j + 1);
            gl.glBegin(GL2.GL_QUAD_STRIP);
            for (int i = 0; i <= slices; i++) {
                double longitude = (2 * Math.PI / slices) * i;
                double sinLongitude = Math.sin(longitude);
                double cosineLongitude = Math.cos(longitude);
                double z = cosineLongitude * radius;
                double y = sinLongitude * radius;
                double normalZ = cosineLongitude;
                double normalY = sinLongitude;
                if (makeTextureCoordinates) {
                    gl.glTexCoord2d(1.0 / slices * i, 1.0 / stacks * (j + 1));
                }
                gl.glNormal3d(0, normalY, normalZ); // Set normal for the side
                gl.glVertex3d(y, x2, z); // Adjust vertex coordinates for xz-plane
                if (makeTextureCoordinates) {
                    gl.glTexCoord2d(1.0 / slices * i, 1.0 / stacks * j);
                }
                gl.glVertex3d(y, x1, z); // Adjust vertex coordinates for xz-plane
            }
            gl.glEnd();
        }

        // draw the top and bottom
        if (rings > 0) {
            // Draw the bottom
            gl.glNormal3d(0, -1, 0); // Set normal direction downwards
            for (int j = 0; j < rings; j++) {
                double d1 = (1.0 / rings) * j;
                double d2 = (1.0 / rings) * (j + 1);
                gl.glBegin(GL2.GL_QUAD_STRIP);
                for (int i = 0; i <= slices; i++) {
                    double angle = (2 * Math.PI / slices) * i;
                    double sin = Math.sin(angle);
                    double cosine = Math.cos(angle);
                    if (makeTextureCoordinates) {
                        gl.glTexCoord2d(1 * (1 + cosine * d1), 0.5 * (1 + sin * d1));
                    }
                    gl.glVertex3d(radius * sin * d1, 0, radius * cosine * d1); // Adjust vertex coordinates for xz-plane
                    if (makeTextureCoordinates) {
                        gl.glTexCoord2d(1 * (1 + cosine * d2), 0.5 * (1 + sin * d2));
                    }
                    gl.glVertex3d(radius * sin * d2, 0, radius * cosine * d2); // Adjust vertex coordinates for xz-plane
                }
                gl.glEnd();
            }

            // Draw the top
            gl.glNormal3d(0, 1, 0); // Set normal direction upwards
            for (int j = 0; j < rings; j++) {
                double d1 = (1.0 / rings) * j;
                double d2 = (1.0 / rings) * (j + 1);
                gl.glBegin(GL2.GL_QUAD_STRIP);
                for (int i = 0; i <= slices; i++) {
                    double angle = (2 * Math.PI / slices) * i;
                    double sin = Math.sin(angle);
                    double cosine = Math.cos(angle);
                    if (makeTextureCoordinates) {
                        gl.glTexCoord2d(0.5 * (1 + cosine * d2), 0.5 * (1 + sin * d2));
                    }
                    gl.glVertex3d(radius * sin * d2, height, radius * cosine * d2); // Adjust vertex coordinates for xz-plane
                    if (makeTextureCoordinates) {
                        gl.glTexCoord2d(0.5 * (1 + cosine * d1), 0.5 * (1 + sin * d1));
                    }
                    gl.glVertex3d(radius * sin * d1, height, radius * cosine * d1); // Adjust vertex coordinates for xz-plane
                }
                gl.glEnd();
            }
        }
    }
    // draw the smaller cylinder
    public static void cylinderSmall(GL2 gl, double radius, double height, int slices, int stacks, int rings,
                                   boolean makeTextureCoordinates) {
        if (radius <= 0) throw new IllegalArgumentException("Radius must be positive");
        if (height <= 0) throw new IllegalArgumentException("Height must be positive");
        if (slices < 3) throw new IllegalArgumentException("Number of slices must be at least 3.");
        if (stacks < 2) throw new IllegalArgumentException("Number of stacks must be at least 2.");

        // body
        for (int j = 0; j < stacks; j++) {
            double x1 = (height / stacks) * j;
            double x2 = (height / stacks) * (j + 1);
            gl.glBegin(GL2.GL_QUAD_STRIP);
            for (int i = 0; i <= slices; i++) {
                double longitude = (2 * Math.PI / slices) * i;
                double sinLongitude = Math.sin(longitude);
                double cosineLongitude = Math.cos(longitude);
                double z = cosineLongitude * radius;
                double y = sinLongitude * radius;
                double normalZ = cosineLongitude;
                double normalY = sinLongitude;
                if (makeTextureCoordinates) {
                    gl.glTexCoord2d(1.0 / slices * i, 1.0 / stacks * (j + 1));
                }
                gl.glNormal3d(0, normalY, normalZ); // Set normal for the side
                gl.glVertex3d(y, x2, z); // Adjust vertex coordinates for xz-plane
                if (makeTextureCoordinates) {
                    gl.glTexCoord2d(1.0 / slices * i, 1.0 / stacks * j);
                }
                gl.glVertex3d(y, x1, z); // Adjust vertex coordinates for xz-plane
            }
            gl.glEnd();
        }

        // draw the top and bottom
        if (rings > 0) {
            // Draw the bottom
            gl.glNormal3d(0, -1, 0); // Set normal direction downwards
            for (int j = 0; j < rings; j++) {
                double d1 = (1.0 / rings) * j;
                double d2 = (1.0 / rings) * (j + 1);
                gl.glBegin(GL2.GL_QUAD_STRIP);
                for (int i = 0; i <= slices; i++) {
                    double angle = (2 * Math.PI / slices) * i;
                    double sin = Math.sin(angle);
                    double cosine = Math.cos(angle);
                    if (makeTextureCoordinates) {
                        gl.glTexCoord2d(1 * (1 + cosine * d1), 0.5 * (1 + sin * d1));
                    }
                    gl.glVertex3d(radius * sin * d1, 0, radius * cosine * d1); // Adjust vertex coordinates for xz-plane
                    if (makeTextureCoordinates) {
                        gl.glTexCoord2d(1 * (1 + cosine * d2), 0.5 * (1 + sin * d2));
                    }
                    gl.glVertex3d(radius * sin * d2, 0, radius * cosine * d2); // Adjust vertex coordinates for xz-plane
                }
                gl.glEnd();
            }

            // Draw the top
            gl.glNormal3d(0, 1, 0); // Set normal direction upwards
            for (int j = 0; j < rings; j++) {
                double d1 = (1.0 / rings) * j;
                double d2 = (1.0 / rings) * (j + 1);
                gl.glBegin(GL2.GL_QUAD_STRIP);
                for (int i = 0; i <= slices; i++) {
                    double angle = (2 * Math.PI / slices) * i;
                    double sin = Math.sin(angle);
                    double cosine = Math.cos(angle);
                    if (makeTextureCoordinates) {
                        gl.glTexCoord2d(0.5 * (1 + cosine * d2), 0.5 * (1 + sin * d2));
                    }
                    gl.glVertex3d(radius * sin * d2, height, radius * cosine * d2); // Adjust vertex coordinates for xz-plane
                    if (makeTextureCoordinates) {
                        gl.glTexCoord2d(0.5 * (1 + cosine * d1), 0.5 * (1 + sin * d1));
                    }
                    gl.glVertex3d(radius * sin * d1, height, radius * cosine * d1); // Adjust vertex coordinates for xz-plane
                }
                gl.glEnd();
            }
        }
    }

    // Draw the big dome
    public static void bigDome(GL2 gl, double radius, int slices, int stacks, boolean makeTextureCoordinate) {
        double x, y, z, phi, theta;
        double dphi = Math.PI / stacks;
        double dtheta = 2.0 * Math.PI / slices;

        // Draw the dome
        for (int i = 0; i < stacks / 2; i++) {
            phi = i * dphi;
            gl.glBegin(GL2.GL_QUAD_STRIP);
            for (int j = 0; j <= slices; j++) {
                theta = j * dtheta;

                x = radius * Math.sin(phi) * Math.cos(theta);
                z = radius * Math.sin(phi) * Math.sin(theta); // Swap y and z
                y = radius * Math.cos(phi); // Swap y and z

                if (makeTextureCoordinate) {
                    gl.glTexCoord2d((double) j / slices, (double) i / stacks);
                }
                gl.glNormal3d(x / radius, y / radius, z / radius);
                gl.glVertex3d(x, y, z);

                x = radius * Math.sin(phi + dphi) * Math.cos(theta);
                z = radius * Math.sin(phi + dphi) * Math.sin(theta); // Swap y and z
                y = radius * Math.cos(phi + dphi); // Swap y and z

                if (makeTextureCoordinate) {
                    gl.glTexCoord2d((double) j / slices, (double) (i + 1) / stacks);
                }
                gl.glNormal3d(x / radius, y / radius, z / radius);
                gl.glVertex3d(x, y, z);
            }
            gl.glEnd();
        }

        // Draw the flat bottom
        gl.glBegin(GL2.GL_POLYGON);
        for (int j = 0; j <= slices; j++) {
            theta = j * dtheta;
            x = radius * Math.cos(theta);
            z = radius * Math.sin(theta); // Swap y and z
            y = 0; // Swap y and z

            if (makeTextureCoordinate) {
                gl.glTexCoord2d((double) j / slices, 0);
            }
            gl.glNormal3d(0, -1, 0); // Normal pointing down
            gl.glVertex3d(x, y, z);
        }
        gl.glEnd();
    }
    // build the tower
    public static void tower(GL2 gl, double radius, double height, int slices, int stacks, int rings, boolean makeTextureCoordinates) {
        if (radius <= 0) throw new IllegalArgumentException("Radius must be positive");
        if (height <= 0) throw new IllegalArgumentException("Height must be positive");
        if (slices < 3) throw new IllegalArgumentException("Number of slices must be at least 3.");
        if (stacks < 2) throw new IllegalArgumentException("Number of stacks must be at least 2.");

        // Enable texture
        gl.glEnable(GL2.GL_TEXTURE_2D);

        // body
        for (int j = 0; j < stacks; j++) {
            double x1 = (height / stacks) * j;
            double x2 = (height / stacks) * (j + 1);
            gl.glBegin(GL2.GL_QUAD_STRIP);
            for (int i = 0; i <= slices; i++) {
                double longitude = (2 * Math.PI / slices) * i;
                double sinLongitude = Math.sin(longitude);
                double cosineLongitude = Math.cos(longitude);
                double z = cosineLongitude * radius;
                double y = sinLongitude * radius;
                double normalZ = cosineLongitude;
                double normalY = sinLongitude;
                if (makeTextureCoordinates) {
                    gl.glTexCoord2d(1.0 / slices * i, 1.0 / stacks * (j + 1));
                }
                gl.glNormal3d(0, normalY, normalZ); // Set normal for the side
                gl.glVertex3d(y, x2, z); // Adjust vertex coordinates for xz-plane
                if (makeTextureCoordinates) {
                    gl.glTexCoord2d(1.0 / slices * i, 1.0 / stacks * j);
                }
                gl.glVertex3d(y, x1, z); // Adjust vertex coordinates for xz-plane
            }
            gl.glEnd();
        }

        // draw the top and bottom
        if (rings > 0) {
            // Draw the bottom
            gl.glNormal3d(0, -1, 0); // Set normal direction downwards
            for (int j = 0; j < rings; j++) {
                double d1 = (1.0 / rings) * j;
                double d2 = (1.0 / rings) * (j + 1);
                gl.glBegin(GL2.GL_QUAD_STRIP);
                for (int i = 0; i <= slices; i++) {
                    double angle = (2 * Math.PI / slices) * i;
                    double sin = Math.sin(angle);
                    double cosine = Math.cos(angle);
                    if (makeTextureCoordinates) {
                        gl.glTexCoord2d(1 * (1 + cosine * d1), 0.5 * (1 + sin * d1));
                    }
                    gl.glVertex3d(radius * sin * d1, 0, radius * cosine * d1); // Adjust vertex coordinates for xz-plane
                    if (makeTextureCoordinates) {
                        gl.glTexCoord2d(1 * (1 + cosine * d2), 0.5 * (1 + sin * d2));
                    }
                    gl.glVertex3d(radius * sin * d2, 0, radius * cosine * d2); // Adjust vertex coordinates for xz-plane
                }
                gl.glEnd();
            }

            // Draw the top
            gl.glNormal3d(0, 1, 0); // Set normal direction upwards
            for (int j = 0; j < rings; j++) {
                double d1 = (1.0 / rings) * j;
                double d2 = (1.0 / rings) * (j + 1);
                gl.glBegin(GL2.GL_QUAD_STRIP);
                for (int i = 0; i <= slices; i++) {
                    double angle = (2 * Math.PI / slices) * i;
                    double sin = Math.sin(angle);
                    double cosine = Math.cos(angle);
                    if (makeTextureCoordinates) {
                        gl.glTexCoord2d(0.5 * (1 + cosine * d2), 0.5 * (1 + sin * d2));
                    }
                    gl.glVertex3d(radius * sin * d2, height, radius * cosine * d2); // Adjust vertex coordinates for xz-plane
                    if (makeTextureCoordinates) {
                        gl.glTexCoord2d(0.5 * (1 + cosine * d1), 0.5 * (1 + sin * d1));
                    }
                    gl.glVertex3d(radius * sin * d1, height, radius * cosine * d1); // Adjust vertex coordinates for xz-plane
                }
                gl.glEnd();
            }
        }

        gl.glDisable(GL2.GL_TEXTURE_2D);
    }

    // draw a square in the (x,y) plane, with given side length
    public static void square(GL2 gl, double side, boolean makeTextureCoordinate) {

        double radius = side / 2;
        gl.glBegin(GL2.GL_POLYGON);

            // vector for lighting calculation
            gl.glNormal3f(0, 0, 1);

            // top left corner of a square
            if(makeTextureCoordinate){
                gl.glTexCoord2d(0, 1);
            }
            gl.glVertex2d(-radius, radius);

            // bottom left corner of a square
            if(makeTextureCoordinate){
                gl.glTexCoord2d(0, 0);
            }
            gl.glVertex2d(-radius, -radius);

            // bottom right corner of a square
            if(makeTextureCoordinate){
                gl.glTexCoord2d(1, 0);
            }
            gl.glVertex2d(radius, -radius);

            // top right corner of a square
            if(makeTextureCoordinate){
                gl.glTexCoord2d(1, 1);
            }
            gl.glVertex2d(radius, radius);

        gl.glEnd();
    }
}
