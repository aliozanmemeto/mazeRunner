package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.List;

/**
 * Road class is responsible for holding all the walkable paths in the game and their texture.
 * It implements Add and DisposeTextureRegion enabling the addition of roads to the existing
 * list and disposal of texture when required.
 */
public class Road implements Add<Number>, DisposeTextureRegion{

    private final TextureRegion textureRegion;
    private final List<Coordinate<Number>> roads; // list of all roads in the game

    /**
     * Constructor for Road. Initializes the list roads and assigns the correct textureRegion.
     */
    public Road() {
        roads = new ArrayList<>();
        textureRegion = new TextureRegion(new Texture(Gdx.files.internal("basictiles.png")), 16, 9 * 16, 16, 16);
    }

    /**
     * Creates and adds a Coordinate type object to the roads list
     * @param x sets the x-coordinate of the newly created Coordinate type object.
     * @param y sets the y-coordinate of the newly created Coordinate type object.
     */
    public void add(Number x, Number y) {
        roads.add(new Coordinate<>(x, y));
    }

    /**
     * Disposes the road texture.
     */
    @Override
    public void disposeTx() {
        textureRegion.getTexture().dispose();
    }


    // getters for the attributes
    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public List<Coordinate<Number>> getRoads() {
        return roads;
    }



}
