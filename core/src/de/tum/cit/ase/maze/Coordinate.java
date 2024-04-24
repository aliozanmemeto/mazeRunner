package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Coordinate class is the superclass of Dynamic Coordinate class and EntryPoint class.
 * It is also used to represent individual game elements of different types like individual wall
 * in wall lists in WallManager class, individual road in roads list in Road class or individual
 * trap in traps list in the Trap class etc.
 * Coordinate class uses a bounded type parameter T
 */

public class Coordinate<T extends Number> {
    protected float X; // x-coordinate
    protected float Y; // y-coordinate

    /**
     * Constructor for Coordinate class.
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     */
    public Coordinate(T x, T y) {
        this.X = x.floatValue();
        this.Y = y.floatValue();
    }

    // Getters and Setters for the attributes.
    public float getX() {
        return X;
    }

    public float getY() {
        return Y;
    }

    public void setX(float x) {
        X = x;
    }

    public void setY(float y) {
        Y = y;
    }

}
