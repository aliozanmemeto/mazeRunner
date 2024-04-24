package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

import java.util.Random;

/**
 * Enemy class represents the moving enemies in the game.
 * It extends the DynamicCoordinate class,sets enemy
 * attributes and defines additional enemy functionality.
 */
public class Enemy extends DynamicCoordinate<Number> {

    /**
     * enemyPower, idSinus this is a floating number [-0.9, 0.9 - enemyPower]
     * enemyPower should be given such that it is between 0 and 1
     * Enemy only moves when sinusInput is between idSinus and idSinus +
     * enemyPower
     */
    private final float idSinus;
    public static float enemyPower = 0.9f;

    /**
     * 0 left, 1 right, 2 up, 3 down in accordance with DynamicCoordinate's textureRegions' indexes.
     */
    private int direction;


    /**
     * Constructor for Enemy. Passes x,y coordinates and isCharacter = false to superclass.
     * Also, assigns a random float value to idSinus and sets initial movement direction down.
     * @param x coordinate of enemy in game
     * @param y coordinate of enemy in game
     */
    public Enemy(float x, float y) {
        super(x, y, false);

        Random random = new Random();
        this.idSinus = random.nextFloat() * (1.8f - enemyPower) - 0.9f;
        direction = 3;
    }


    // getter and setters for different attributes.
    public float getIdSinus() {
        return idSinus;
    }

    public static void setEnemyPower(float enemyPower) {
        Enemy.enemyPower = enemyPower;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }


    public int getDirection() {
        return direction;
    }

}
