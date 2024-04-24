package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * The DynamicCoordinate class is the superclass for all non-stationary/moving gameObject classes(Enemy & Character class)
 * and responsible for movement animations and coordinate changes along with collision detection and texture disposing.
 * It extends the Coordinate class and implements DisposeTextureRegion Interface.
 */

public class DynamicCoordinate<T extends Number> extends Coordinate<T> implements DisposeTextureRegion {

    // These are used for the character animations
    private final Animation<TextureRegion> DownAnimation;
    private final Animation<TextureRegion> UpAnimation;
    private final Animation<TextureRegion> LeftAnimation;
    private final Animation<TextureRegion> RightAnimation;
    private final TextureRegion[] textureRegions; //Stores textures for left, right, up, down
    private TextureRegion currentTr;// The current texture for the enemy/character

    private float Speed;

    // The previous x,y coordinates of the enemy/character before movement.
    private float prevX;
    private float prevY;


    /**
     * Constructor for DynamicCoordinate. Passes x,y coordinates to Coordinate
     * class and sets up animations, textures and default speed along with initial
     * values for prevX, prevY  and currentTr.
     *
     * @param x           x-coordinate of the DynamicCoordinate
     * @param y           y- coordinate of the DynamicCoordinate
     * @param isCharacter decides which animations and textures to be assigned.
     */
    public DynamicCoordinate(T x, T y, boolean isCharacter) {
        super(x, y);
        textureRegions = new TextureRegion[4];
        this.prevX = x.floatValue();
        this.prevY = y.floatValue();


        if (isCharacter) {
            DownAnimation = returnAnimation(0, 4, 16, 32, 0, "character.png");
            UpAnimation = returnAnimation(0, 4, 16, 32, 2, "character.png");
            LeftAnimation = returnAnimation(0, 4, 16, 32, 3, "character.png");
            RightAnimation = returnAnimation(0, 4, 16, 32, 1, "character.png");
            textureRegions[0] = new TextureRegion(new Texture(Gdx.files.internal("character.png")), 0, 3 * 32, 16, 32);
            textureRegions[1] = new TextureRegion(new Texture(Gdx.files.internal("character.png")), 0, 32, 16, 32);
            textureRegions[2] = new TextureRegion(new Texture(Gdx.files.internal("character.png")), 0, 2 * 32, 16, 32);
            textureRegions[3] = new TextureRegion(new Texture(Gdx.files.internal("character.png")), 0, 0, 16, 32);

        } else {
            DownAnimation = returnAnimation(0, 3, 16, 16, 4, "mobs.png");
            UpAnimation = returnAnimation(0, 3, 16, 16, 7, "mobs.png");
            LeftAnimation = returnAnimation(0, 3, 16, 16, 5, "mobs.png");
            RightAnimation = returnAnimation(0, 3, 16, 16, 6, "mobs.png");

            textureRegions[0] = new TextureRegion(new Texture(Gdx.files.internal("mobs.png")), 16, 5 * 16, 16, 16);
            textureRegions[1] = new TextureRegion(new Texture(Gdx.files.internal("mobs.png")), 16, 6 * 16, 16, 16);
            textureRegions[2] = new TextureRegion(new Texture(Gdx.files.internal("mobs.png")), 16, 7 * 16, 16, 16);
            textureRegions[3] = new TextureRegion(new Texture(Gdx.files.internal("mobs.png")), 16, 4 * 16, 16, 16);

        }

        currentTr = textureRegions[3];
        Speed = 180.0f;
    }

    // getters and setters for different attributes
    public float getPrevX() {
        return prevX;
    }

    public void setPrevX(float prevX) {
        this.prevX = prevX;
    }

    public float getPrevY() {
        return prevY;
    }

    public void setPrevY(float prevY) {
        this.prevY = prevY;
    }


    /**
     * Increases the y-coordinate of DynamicObject and assigns 'UP' texture
     *
     * @return animation depicting upward movement
     */
    public Animation<TextureRegion> moveUp() {
        Y += Gdx.graphics.getDeltaTime() * getSpeed();
        currentTr = textureRegions[2];
        return UpAnimation;
    }


    /**
     * Decreases the y-coordinate of DynamicObject and assigns 'DOWN' texture
     *
     * @return animation depicting downward movement
     */

    public Animation<TextureRegion> moveDown() {
        Y -= Gdx.graphics.getDeltaTime() * getSpeed();
        currentTr = textureRegions[3];
        return DownAnimation;
    }


    /**
     * Increases the x-coordinate of DynamicObject and assigns 'RIGHT' texture
     *
     * @return animation depicting rightward movement
     */
    public Animation<TextureRegion> moveRight() {
        X += Gdx.graphics.getDeltaTime() * getSpeed();
        currentTr = textureRegions[1];
        return RightAnimation;
    }

    /**
     * Decreases the x-coordinate of DynamicObject and assigns 'LEFT' texture
     *
     * @return animation depicting leftward movement
     */
    public Animation<TextureRegion> moveLeft() {
        X -= Gdx.graphics.getDeltaTime() * getSpeed();
        currentTr = textureRegions[0];
        return LeftAnimation;
    }


    /**
     * Used to assign appropriate animations to the DynamicCoordinate.
     *
     * @param startIndex  first texture column  containing the required textureRegions in the  .png
     * @param endIndex    last texture column containing the required textureRegions in the .png
     * @param frameWidth  the width of each texture region in the .png
     * @param frameHeight the height of each texture region in the .png
     * @param row         texture row number containing the required textureRegions in the .png
     * @param filePath    location of the .png file
     * @return Animation<TextureRegion> with frame duration of 0.1f.
     */
    public Animation<TextureRegion> returnAnimation(int startIndex, int endIndex, int frameWidth, int frameHeight,
                                                    int row, String filePath) {
        Texture walkSheet = new Texture(Gdx.files.internal(filePath));


        // libGDX internal Array instead of ArrayList because of performance
        Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);

        // Add all frames to the animation
        for (int col = startIndex; col < endIndex; col++) {
            walkFrames.add(new TextureRegion(walkSheet, col * frameWidth, row * frameHeight, frameWidth, frameHeight));
        }

        return new Animation<>(0.1f, walkFrames);
    }


    /**
     * Detects overlap between DynamicCoordinate object and another object present at x,y coordinates using Rectangle class
     *
     * @param x x-coordinate of object checked for overlap with DynamicCoordinate object
     * @param y y-coordinate of object checked for overlap with DynamicCoordinate object
     * @return true if there is an overlap between rectangle created at the DynamicCoordinate's coordinates
     * and rectangle created at the checked x,y coordinates
     */
    public boolean detectCollision(float x, float y) {
        // rectangle created at the checked x,y coordinates
        Rectangle checkedRectangle = new Rectangle((int) x, (int) y, 32, 32);

        // rectangle created at the DynamicCoordinate's  x,y coordinates
        Rectangle charRectangle = new Rectangle((int) getX(), (int) getY(), 64, 64);

        //checking for overlap
        return charRectangle.overlaps(checkedRectangle);
    }


    /**
     * Disposes all the textures.
     */
    @Override
    public void disposeTx() {

        for (TextureRegion frame : DownAnimation.getKeyFrames()) {
            frame.getTexture().dispose();
        }

        for (TextureRegion frame : UpAnimation.getKeyFrames()) {
            frame.getTexture().dispose();
        }

        for (TextureRegion frame : LeftAnimation.getKeyFrames()) {
            frame.getTexture().dispose();
        }

        for (TextureRegion frame : RightAnimation.getKeyFrames()) {
            frame.getTexture().dispose();
        }

        for (TextureRegion textureRegion : textureRegions) {
            textureRegion.getTexture().dispose();
        }

    }

    // getter and setters for different attributes.

    public float getSpeed() {
        return Speed;
    }

    public void setSpeed(float speed) {
        Speed = speed;
    }

    public TextureRegion getCurrentTr() {
        return currentTr;
    }


}
