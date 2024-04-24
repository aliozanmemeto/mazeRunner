package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.List;

/**
 * Key class is responsible for holding all the keys in the game and their texture.
 * It implements Add and DisposeTextureRegion enabling addition of keys to the
 * existing list and disposal of key texture when required.
 */
public class Key implements Add<Number>, DisposeTextureRegion {
    private final TextureRegion textureRegion;

    private final List<Coordinate<Number>> keys; // list of all keys in the game

    /**
     * Constructor for Key. Initializes the list keys an assigns the correct textureRegion.
     */
    public Key() {
        keys = new ArrayList<>();
        textureRegion = new TextureRegion(new Texture(Gdx.files.internal("key.png")), 0, 0, 16, 16);
    }


    /**
     * Creates and adds a new Coordinate type object to the keys list.
     * @param x Sets the x-coordinate of the newly created Coordinate type object.
     * @param y Sets the y-coordinate of the newly created Coordinate type object.
     */
    public void add(Number x, Number y) {
        keys.add(new Coordinate<>(x, y));
    }


    /**
     * Disposes the key texture.
     */
    @Override
    public void disposeTx() {
        textureRegion.getTexture().dispose();
    }


    // getters for the attributes
    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public List<Coordinate<Number>> getKeys() {
        return keys;
    }

}
