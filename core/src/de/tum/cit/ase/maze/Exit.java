package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.List;

/**
 * Exit class is responsible for holding all the exists in the game and their texture.
 * It also implements Add and DisposeTextureRegion enabling addition of exits to the existing
 * list and disposal of texture when required.
 */
public class Exit implements Add<Number>, DisposeTextureRegion {

    private final TextureRegion textureRegion;

    private final List<Coordinate<Number>> exits; // List of all exits in the game

    /**
     * Constructor for Exit. Initializes exits list and assigns the correct textureRegion
     */
    public Exit() {
        exits = new ArrayList<>();
        textureRegion = new TextureRegion(new Texture(Gdx.files.internal("things.png")), 0, 0, 16, 16);
    }

    /**
     * Creates and adds a Coordinate type object to the exits list
     *
     * @param x sets the x-coordinate of the newly created Coordinate type object.
     * @param y sets the y-coordinate of the newly created Coordinate type object.
     */
    @Override
    public void add(Number x, Number y) {
        exits.add(new Coordinate<>(x, y));
    }

    /**
     * Disposes the exit texture
     */
    @Override
    public void disposeTx() {
        textureRegion.getTexture().dispose();
    }


    // getters for the attributes
    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public List<Coordinate<Number>> getExits() {
        return exits;
    }


}
