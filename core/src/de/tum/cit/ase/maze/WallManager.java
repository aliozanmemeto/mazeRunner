package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * WallManager class is responsible for holding different type of walls and
 * their textures. It implements DisposeTextureRegion enabling disposal of all
 * wall textures when required.
 */
public class WallManager implements DisposeTextureRegion {
    private final List<Coordinate<Number>> perspectiveWalls; // list of walls with sideView Texture
    private final List<Coordinate<Number>> regularWalls; //list of walls with topView texture
    private final List<Coordinate<Number>> middleWalls; // list of walls surrounded by other walls on all sides

    private final TextureRegion perspectiveTr;
    private final TextureRegion regularTr;
    private final TextureRegion middleTr;

    /**
     * Constructor for WallManager. Initializes all lists and assigns the correct textureRegions.
     */
    public WallManager() {
        perspectiveWalls = new ArrayList<>();
        regularWalls = new ArrayList<>();
        middleWalls = new ArrayList<>();

        perspectiveTr = new TextureRegion(new Texture(Gdx.files.internal("basictiles.png")), 32, 0, 16, 16);
        regularTr = new TextureRegion(new Texture(Gdx.files.internal("basictiles.png")), 16, 0, 16, 16);
        middleTr = new TextureRegion(new Texture(Gdx.files.internal("basictiles.png")), 32, 9 * 16, 16, 16);
    }

    /**
     * Creates and adds a Coordinate type object to one of the wall lists depending upon value of @param wallType
     *
     * @param x        sets the x-coordinate of the newly created Coordinate type object.
     * @param y        sets the y-coordinate of the newly created Coordinate type object.
     * @param wallType determines which wall list the Coordinate object will be added to.
     */
    public void addWall(Number x, Number y, WallType wallType) {
        switch (wallType) {
            case REGULAR -> regularWalls.add(new Coordinate<>(x, y));
            case PERSPECTIVE -> perspectiveWalls.add(new Coordinate<>(x, y));
            case MIDDLE -> middleWalls.add(new Coordinate<>(x, y));
            default -> throw new IllegalArgumentException("Invalid wallType: " + wallType);
        }
    }


    // getters for different attributes
    public List<Coordinate<Number>> getPerspectiveWalls() {
        return perspectiveWalls;
    }

    public List<Coordinate<Number>> getRegularWalls() {
        return regularWalls;
    }

    public List<Coordinate<Number>> getMiddleWalls() {
        return middleWalls;
    }

    public TextureRegion getPerspectiveTr() {
        return perspectiveTr;
    }

    public TextureRegion getRegularTr() {
        return regularTr;
    }

    public TextureRegion getMiddleTr() {
        return middleTr;
    }


    /**
     * Disposes all the wall textures
     */
    @Override
    public void disposeTx() {
        perspectiveTr.getTexture().dispose();
        regularTr.getTexture().dispose();
        middleTr.getTexture().dispose();
    }

    /**
     * Used to detect collision of any moving entity (Enemy/Character) with any of the maze walls using Rectangle class
     *
     * @param dynamicX x-coordinate of the moving entity (Enemy/Character)
     * @param dynamicY y-coordinate of the moving entity (Enemy/Character)
     * @return true if collision detected else false
     */
    public boolean wallCollision(float dynamicX, float dynamicY) {
        // rectangle created using moving entity's (Enemy/Character) coordinates
        Rectangle checkedRectangle = new Rectangle((int) dynamicX + 20, (int) dynamicY + 20, 32, 32);

        for (Coordinate<Number> wall : getRegularWalls()) {
            //rectangle created using wall's coordinates
            Rectangle wallRectangle = new Rectangle((int) wall.getX(), (int) wall.getY(), 64, 64);

            //checking for overlap
            if (wallRectangle.overlaps(checkedRectangle)) {
                return true;
            }
        }

        for (Coordinate<Number> wall : getPerspectiveWalls()) {
            //rectangle created using wall's coordinates
            Rectangle wallRectangle = new Rectangle((int) wall.getX(), (int) wall.getY(), 64, 64);
            //checking for overlap
            if (wallRectangle.overlaps((checkedRectangle))) {
                return true;
            }
        }

        for (Coordinate<Number> wall : getMiddleWalls()) {
            //rectangle created using wall's coordinates
            Rectangle wallRectangle = new Rectangle((int) wall.getX(), (int) wall.getY(), 64, 64);
            //checking for overlap
            if (wallRectangle.overlaps((checkedRectangle))) {
                return true;
            }
        }
        return false;
    }

}
