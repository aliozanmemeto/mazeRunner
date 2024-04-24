package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * EntryPoint class represents the entry point for the character in the maze.
 * It implements DisposeTextureRegion and extends the Coordinate class,
 * setting the textureRegion for entry point to the maze.
 */
public class EntryPoint extends Coordinate<Number> implements DisposeTextureRegion {
    private final TextureRegion textureRegion;

    /**
     * Constructor for EntryPoint. Passes x,y coordinates to superclass and assigns
     * the correct textureRegion.
     * @param x x-coordinate of EntryPoint in the game
     * @param y y-coordinate of EntryPoint in the game
     */
    public EntryPoint(float x, float y) {
        super(x, y);
        textureRegion = new TextureRegion(new Texture(Gdx.files.internal("basictiles.png")), 0, 112, 16, 16);
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    /**
     * Disposes EntryPoint texture
     */
    @Override
    public void disposeTx() {
        textureRegion.getTexture().dispose();
    }
}
