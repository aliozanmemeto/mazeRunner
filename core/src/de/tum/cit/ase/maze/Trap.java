package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

/**
 * Trap class is responsible for holding all the stationary traps in the game and their animation.
 * It implements Add and DisposeTextureRegion enabling the addition of traps to the existing
 * list and disposal of animation textures when required.
 */
public class Trap implements Add<Number>, DisposeTextureRegion {

    private final Animation<TextureRegion> animation; // since traps can be animated
    private final List<Coordinate<Number>> traps; // list of all traps in the game

    /**
     * Constructor of Trap class. Initializes the list traps. Also, creates an animation
     * using an Array of required textureRegions from the .png file and then assigns it
     * to the animation attribute.
     */
    public Trap() {

        traps = new ArrayList<>();

        Texture walkSheet = new Texture(Gdx.files.internal("objects.png"));

        Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);
        for (int col = 4; col < 10; col++) {
            walkFrames.add(new TextureRegion(walkSheet, col * 16, 3 * 16, 16, 16));
        }

        animation = new Animation<>(0.1f, walkFrames);
    }


    /**
     * Creates and adds a Coordinate type object to the traps list
     *
     * @param x sets the x-coordinate of the newly created Coordinate type object.
     * @param y sets the y-coordinate of the newly created Coordinate type object.
     */
    @Override
    public void add(Number x, Number y) {
        traps.add(new Coordinate<>(x, y));
    }

    /**
     * Disposes the trap animation textures
     */
    @Override
    public void disposeTx() {
        for (TextureRegion frame : animation.getKeyFrames()) {
            frame.getTexture().dispose();
        }
    }


    // getters for the attributes
    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    public List<Coordinate<Number>> getTraps() {
        return traps;
    }
}
