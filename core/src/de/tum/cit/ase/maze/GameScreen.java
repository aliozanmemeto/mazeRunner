package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import com.badlogic.gdx.graphics.Color;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {

    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private float sinusInput = 0f;

    // Different Game Elements
    private final WallManager wallManager; // Responsible for all 3 kinds of maze walls
    private final Road road; // Responsible for all walkable paths in the maze
    private Character character; // The main character in the game
    private EntryPoint entryPoint; // Initial spawn location of the character
    private final Exit exit; // Responsible for all exists out of the maze
    private final Trap trap; // Responsible for all stationary traps/obstacles
    private final Key key; //Responsible for all the keys in the game
    private final List<Enemy> enemies; // Moving Enemies


    private final Properties map;

    // Every 8 second change the direction of enemies. Half of the time towards the character.
    private float countDownEnemy = 16;
    private boolean enemyIntelligent; // Whether enemy has intelligent movement
    private final Random random;

    // Extra Game Elements/collectibles (Bonus) in the game
    private final TextureRegion speedUpTx; // Texture for character speedUp collectible
    private final TextureRegion enemySpeedUpTx; // Texture for enemy speedUp collectible
    private final TextureRegion heartRegion; // Texture for character lives increase collectible
    private final List<Coordinate<Number>> speedUpCoordinates; // List of all character speedUp collectibles
    private final List<Coordinate<Number>> enemySpeedUpCoordinates; // List of all enemy speedUp collectibles
    private final List<Coordinate<Number>> heartCoordinates; // List of all character lives increase collectibles

    //Maximum value of the x-coordinate/row number in the properties file
    private final int mapsize;

    // Game Sounds
    private final Sound speedUpAudio; // Played when player collects a speedUp collectible
    private final Sound enSpeedUpAudio; // Played when player steps on an enemy speedUp collectible
    private final Sound keyAudio; // Played when player collects the key
    private final Sound hit; // Played when player hits an enemy or any trap
    private final Sound win; // Played if game won
    private final Sound gameOver; // Played if game lost

    private final int totalKeys; //Total number of available keys in the game
    private boolean isGameLost;
    private boolean isGameWon;
    private float gameEndTimer = 2f; // Initial value of timer when displaying gameWon/Lost status.

    private final Texture gameOverText;
    private final Texture gameWinText;

    /**
     * Constructor for GameScreen. Sets up the camera, font, sounds, basic game elements
     * and extra collectibles in the game.
     *
     * @param game             The main game class, used to access global resources and methods.
     * @param propertiesConfig filepath of the chosen maze file.
     */
    public GameScreen(MazeRunnerGame game, String propertiesConfig) throws IOException {
        this.game = game;

        // Create and configure the camera for the game view
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.zoom = 1.0f;

        // Get the font from the game's skin
        BitmapFont font = game.getSkin().getFont("font");

        //Initialize the required Sound attributes
        speedUpAudio = Gdx.audio.newSound(Gdx.files.internal("speedUp.wav"));
        keyAudio = Gdx.audio.newSound(Gdx.files.internal("key.wav"));
        hit = Gdx.audio.newSound(Gdx.files.internal("lifelost.ogg"));
        gameOver = Gdx.audio.newSound(Gdx.files.internal("death.wav"));
        enSpeedUpAudio = Gdx.audio.newSound(Gdx.files.internal("enemySpeedUp.wav"));
        win = Gdx.audio.newSound(Gdx.files.internal("win.wav"));


        // Initialize the game elements
        wallManager = new WallManager();
        road = new Road();
        exit = new Exit();
        trap = new Trap();
        key = new Key();
        // ArrayList is better for randomly accessing elements for the movements.
        enemies = new ArrayList<>();


        random = new Random();

        map = new Properties();
        FileInputStream fileInputStream = new FileInputStream(propertiesConfig);
        map.load(fileInputStream);
        fileInputStream.close();

        // Initializing the extra Collectibles' textures
        heartRegion = new TextureRegion(new Texture(Gdx.files.internal("objects.png")), 0, 3 * 16, 16, 16);
        speedUpTx = new TextureRegion(new Texture(Gdx.files.internal("basictiles.png")), 4 * 16, 5 * 16, 16, 16);
        enemySpeedUpTx = new TextureRegion(new Texture(Gdx.files.internal("basictiles.png")), 3 * 16, 5 * 16, 16, 16);


        // Maximum value of the x-coordinate/row number in the properties file.
        // It is necessary instead of looping through the keySet, because we need to create road objects too.
        mapsize = map.stringPropertyNames().stream().parallel().map(mapKey -> mapKey.split(",")).mapToInt(parts -> Integer.parseInt(parts[0])).reduce(0, Integer::max);


        //Using mapSize to loop through the keySets and adding game elements according to the value of each keySet in the properties file
        for (int width = 0; width <= mapsize; width++) {
            for (int height = 0; height <= mapsize; height++) {
                String value = map.getProperty(width + "," + height);
                if (value == null) {
                    // Adding roads for keySets within the mapSize but not present in the properties file.
                    road.add(width * 64, height * 64);
                } else {
                    switch (value) {
                        case "0" -> {
                            // Adding wall for value 0
                            String objBelow = map.getProperty(width + "," + (height - 1));

                            if (isMiddleWall(width, height)) {
                                // Check all sides, if all objects are walls, then middle wall.
                                wallManager.addWall(width * 64, height * 64, WallType.MIDDLE);
                            } else if (height == 0 || isMiddleWall(width, height - 1) || objBelow == null || !objBelow.equals("0")) {
                                // else if it is the bottom wall or there is a road/middle wall below it then perspective wall
                                wallManager.addWall(width * 64, height * 64, WallType.PERSPECTIVE);
                            } else {
                                // else regular wall (top-view)
                                wallManager.addWall(width * 64, height * 64, WallType.REGULAR);
                            }
                        }

                        case "1" -> {
                            // Adding the entryPoint and main character for value 1
                            entryPoint = new EntryPoint(width * 64, height * 64);
                            character = new Character(width * 64, height * 64);
                        }
                        case "2" -> exit.add(width * 64, height * 64); // Adding exits for value 2
                        case "3" -> {
                            // Adding stationary trap and also road(beneath the trap) for value 3
                            trap.add(width * 64, height * 64);
                            road.add(width * 64, height * 64);
                        }
                        case "4" -> {
                            // Adding enemy and also road (beneath the enemy) for value 4
                            Enemy enemy = new Enemy(width * 64, height * 64);
                            enemies.add(enemy);
                            road.add(width * 64, height * 64);
                        }
                        case "5" -> {
                            key.add(width * 64, height * 64);
                            road.add(width * 64, height * 64);
                        }
                    }
                }

            }
        }

        //Total number of available keys in the game
        totalKeys = key.getKeys().size();

        Collections.shuffle(road.getRoads());

        //Adding the Extra (Bonus) collectibles for the player in the maze
        if ((float) (road.getRoads().size() / (wallManager.getMiddleWalls().size() + 1)) < 25) {
            // Select random 4 road coordinates for each type of collectible.
            List<Coordinate<Number>> selectedObjects = road.getRoads().subList(0, 12);
            speedUpCoordinates = new ArrayList<>(selectedObjects.subList(0, 4));
            enemySpeedUpCoordinates = new ArrayList<>(selectedObjects.subList(4, 8));
            heartCoordinates = new ArrayList<>(selectedObjects.subList(8, 12));
        }
        // Smaller maps get 1 of each type of collectible.
        else {
            // Select random 1 road coordinate for each type of collectible.
            List<Coordinate<Number>> selectedObjects = road.getRoads().subList(0, 3);
            speedUpCoordinates = new ArrayList<>(selectedObjects.subList(0, 1));
            enemySpeedUpCoordinates = new ArrayList<>(selectedObjects.subList(1, 2));
            heartCoordinates = new ArrayList<>(selectedObjects.subList(2, 3));

        }

        gameOverText = new Texture(("gameover-removebg-preview.jpg")); //https://opengameart.org/content/game-over-5
        gameWinText = new Texture(Gdx.files.internal("gamewin-removebg-preview.jpg"));

    }


    /**
     * Method responsible for rendering game elements, updating camera, detecting interactions between
     * character and other game elements, setting enemy movement directions and checking user input.     *
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {

        // Check for escape key press to go back to the MenuScreen
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.goToMenu(true); //Since by pressing ESCAPE we are pausing the game.
        }

        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen

        camera.update(); // Update the camera


        sinusInput += delta; // Increases with each render

        // Set up and begin drawing with the sprite batch
        game.getSpriteBatch().setProjectionMatrix(camera.combined);

        // Decreases with each render. Used to change the direction of the enemies every 8 seconds.
        countDownEnemy -= delta;

        game.getSpriteBatch().begin();// Important to call this before drawing anything
        // Draws the walls, roads and exits.

        //Regular walls (top-view)
        for (Coordinate<Number> wall : wallManager.getRegularWalls()) {
            game.getSpriteBatch().draw(wallManager.getRegularTr(), wall.getX(), wall.getY(), 64, 64);
        }

        //Perspective walls
        for (Coordinate<Number> wall : wallManager.getPerspectiveWalls()) {
            game.getSpriteBatch().draw(wallManager.getPerspectiveTr(), wall.getX(), wall.getY(), 64, 64);
        }

        //Middle walls (Walls surrounded by other walls on all sides)
        for (Coordinate<Number> wall : wallManager.getMiddleWalls()) {
            game.getSpriteBatch().draw(wallManager.getMiddleTr(), wall.getX(), wall.getY(), 64, 64);
        }

        for (Coordinate<Number> roadXy : road.getRoads()) {
            game.getSpriteBatch().draw(road.getTextureRegion(), roadXy.getX(), roadXy.getY(), 64, 64);
        }

        for (Coordinate<Number> exitXy : exit.getExits()) {
            game.getSpriteBatch().draw(exit.getTextureRegion(), exitXy.getX(), exitXy.getY(), 64, 64);
        }


        // Draws the Traps, Enemies and Keys

        //Traps
        for (Coordinate<Number> trapXy : trap.getTraps()) {
            game.getSpriteBatch().draw(trap.getAnimation().getKeyFrame(sinusInput, true), trapXy.getX(), trapXy.getY(), 64, 64);

            // Detects character's collision with the trap.
            if (character.detectCollision(trapXy.getX(), trapXy.getY())) {

                //Checks if the coolDownTimer for character collision with enemies/traps is 0,
                // i.e. no other collision with traps/enemies in the past 3 seconds.
                if (character.getCoolDownTimer() == 0.0f) {

                    // Decreases character lives by 1 ( only if >1)
                    if (character.decreaseCharacterLives()) {
                        hit.play();

                    } else {
                        isGameLost = true;
                        gameOver.play();
                    }
                }
            }
        }

        //Enemies
        for (Enemy enemy : enemies) {

            //Moves the enemies only if the game in not paused
            if (game.isPaused()) {
                moveEnemy(enemy);
            }

            // Detects character's collision with the enemy.
            if (character.detectCollision(enemy.getX(), enemy.getY())) {

                //Checks if the coolDownTimer for character collision with enemies/traps is 0,
                // i.e. no other collision with traps/enemies in the past 3 seconds.
                if (character.getCoolDownTimer() == 0.0f) {

                    // Decreases character lives by 1 ( only if >1)
                    if (character.decreaseCharacterLives()) {
                        hit.play();

                    } else {
                        isGameLost = true;
                        gameOver.play();
                    }
                }
            }

        }

        // Keys
        List<Coordinate<Number>> keys = this.key.getKeys();
        for (int i = 0; i < keys.size(); i++) {
            Coordinate<Number> keyCo = keys.get(i);

            // Detects character's collision with the key.
            if (character.detectCollision(keyCo.getX(), keyCo.getY())) {
                keyAudio.play();

                // Remove the key from the list
                keys.remove(keyCo);
                character.setNumKeys(character.getNumKeys() + 1);// Increases number of keys with the character
            }

            game.getSpriteBatch().draw(key.getTextureRegion(), keyCo.getX(), keyCo.getY(), 64, 64);
        }

        //Extra(Bonus) Collectibles

        // Enemy speedUp Collectibles
        if (enemySpeedUpCoordinates != null) {
            for (int i = 0; i < enemySpeedUpCoordinates.size(); i++) {
                game.getSpriteBatch().draw(enemySpeedUpTx, enemySpeedUpCoordinates.get(i).getX(), enemySpeedUpCoordinates.get(i).getY(), 64, 64);

                //Detects character's collision with the Collectible.
                if (character.detectCollision(enemySpeedUpCoordinates.get(i).getX(), enemySpeedUpCoordinates.get(i).getY())) {
                    character.setCoolDownEnemy(8f); // Sets a coolDown Timer for enemy speed up
                    enSpeedUpAudio.play(); // Plays speed up sound

                    //Increases the speed of all enemies by 75%
                    for (Enemy enemy : enemies) {
                        enemy.setSpeed(enemy.getSpeed() * 1.75f);
                    }
                    // Removes the collectible with which the collision took place
                    enemySpeedUpCoordinates.remove(enemySpeedUpCoordinates.get(i));
                }
            }
        }


        // Character lives increase collectibles
        if (heartCoordinates != null) {
            for (int i = 0; i < heartCoordinates.size(); i++) {
                game.getSpriteBatch().draw(heartRegion, heartCoordinates.get(i).getX(), heartCoordinates.get(i).getY(), 64, 64);

                //Detects character's collision with the Collectible.
                if (character.detectCollision(heartCoordinates.get(i).getX(), heartCoordinates.get(i).getY())) {
                    character.setCharacterLives(character.getCharacterLives() + 1); // Increases character lives by 1
                    speedUpAudio.play();
                    // Removes the collectible with which the collision took place
                    heartCoordinates.remove(heartCoordinates.get(i));
                }
            }
        }

        // Character speedUp collectibles
        if (speedUpCoordinates != null) {
            for (int i = 0; i < speedUpCoordinates.size(); i++) {
                game.getSpriteBatch().draw(speedUpTx, speedUpCoordinates.get(i).getX(), speedUpCoordinates.get(i).getY(), 64, 64);

                //Detects character's collision with the Collectible.
                if (character.detectCollision(speedUpCoordinates.get(i).getX(), speedUpCoordinates.get(i).getY())) {
                    speedUpAudio.play();

                    character.setCoolDownSpeedUp(8f); // Sets a Cool down timer for character speed up
                    character.setSpeed(character.getSpeed() * 1.75f); // Increases Character speed by 75%

                    // Removes the collectible with which the collision took place
                    speedUpCoordinates.remove(speedUpCoordinates.get(i));
                }
            }
        }


        // Every 8 second change the direction of enemies. Half of the time towards the character.
        if (countDownEnemy < 0) {
            // Generate a random number between 0 and 3, every 8 seconds for enemy direction.
            for (Enemy enemy : enemies) {
                enemy.setDirection(random.nextInt(4));
            }
            enemyIntelligent = false;
            countDownEnemy = 16;
        } else if (!enemyIntelligent && countDownEnemy < 8) {
            for (Enemy enemy : enemies) {
                //Comparing character's position relative to the enemy and setting the enemy movement direction accordingly.

                //First we take the difference between the x-coordinates of both character
                //and enemy (difX) and then take difference between their y-coordinates (difY).
                float difX = character.getX() - enemy.getX();
                float difY = character.getY() - enemy.getY();

                //Next, we compare the absolute values of both difX and difY.
                if (Math.abs(difX) >= Math.abs(difY)) {
                    if (difX <= 0) {
                        enemy.setDirection(0); //enemy moves Left
                    } else {
                        enemy.setDirection(1); //enemy moves Right
                    }
                } else {
                    if (difY <= 0) {
                        enemy.setDirection(3); //enemy moves Down
                    } else {
                        enemy.setDirection(2);//enemy moves Up
                    }
                }
            }

            countDownEnemy = 8;
            enemyIntelligent = true;
        }


        //Checks if the game is already won or lost.
        if (isGameLost) {
            dispose();

            // To go MenuScreen after displaying the Game Over status
            if (gameEndTimer <= 0) {
                game.goToMenu(false);
                gameOverText.dispose();
                gameWinText.dispose();
            }


            game.getSpriteBatch().draw(gameOverText, (Gdx.graphics.getWidth() - gameOverText.getWidth()) / 2f,
                    (Gdx.graphics.getHeight() - gameOverText.getHeight()) / 2f, gameOverText.getWidth(), gameOverText.getHeight());
            game.backGroundGameStop(); //Stops game background music

            gameEndTimer -= delta; //Decreasing the timer's initial value
            camera.position.set((Gdx.graphics.getWidth()) / 2f, (Gdx.graphics.getHeight()) / 2f, 0); //Sets camera position to the centre
            game.getSpriteBatch().end();
            return;
        } else if (isGameWon) {
            dispose();

            // To go MenuScreen after displaying the Game Won status
            if (gameEndTimer <= 0) {
                game.goToMenu(false);

                gameOverText.dispose();
                gameWinText.dispose();
            }

            game.getSpriteBatch().draw(gameWinText, (Gdx.graphics.getWidth() - gameWinText.getWidth()) / 2f,
                    (Gdx.graphics.getHeight() - gameWinText.getHeight()) / 2f, gameWinText.getWidth(), gameWinText.getHeight());
            game.backGroundGameStop();//Stops game background music

            gameEndTimer -= delta; //Decreasing the timer's initial value
            camera.position.set((Gdx.graphics.getWidth()) / 2f, (Gdx.graphics.getHeight()) / 2f, 0); //Sets camera position to the centre
            game.getSpriteBatch().end();
            return;

        }

        //Resetting enemy speed once the cool down timer for enemy speedup reaches zero
        if (!character.enemyCooldown(delta)) {
            for (Enemy enemy : enemies) {
                enemy.setSpeed(180);
            }
        }


        for (Coordinate<Number> exitCo : exit.getExits()) {
            //Checks if the winning condition is satisfied
            if (character.getNumKeys() == totalKeys && character.detectCollision(exitCo.getX(), exitCo.getY())) {
                win.play();
                isGameWon = true;
            }
        }

        //Draws the entryPoint
        game.getSpriteBatch().draw(entryPoint.getTextureRegion(), entryPoint.getX(), entryPoint.getY(), 64, 64);

        //Draws the HUD elements
        renderHUD();

        //Moves the main character if the game is not paused
        if (game.isPaused()) {
            moveCharacter(delta);
        }

        // Position the camera based on the character
        camera.position.set(character.getX(), character.getY(), 0);
        game.getSpriteBatch().end(); // Important to call this after drawing everything
    }


    /**
     * Method responsible for the moving enemy, detecting their collision with maze walls
     * and drawing the enemy texture/animation accordingly.
     *
     * @param enemy Refers to the individual enemy that has to be moved.
     */
    private void moveEnemy(Enemy enemy) {
        Animation<TextureRegion> currentAnimation = null;

        if (Math.sin(sinusInput) >= enemy.getIdSinus() && Math.sin(sinusInput) < enemy.getIdSinus() + Enemy.enemyPower) {
            // LEFT Direction
            int direction = enemy.getDirection();
            if (direction == 0) {
                currentAnimation = enemy.moveLeft();
            } else if (direction == 1) {
                currentAnimation = enemy.moveRight();
            } else if (direction == 2) {
                currentAnimation = enemy.moveUp();
            } else {
                currentAnimation = enemy.moveDown();
            }

            if (wallManager.wallCollision(enemy.getX(), enemy.getY()) ||
                    (direction == 0 && enemy.getX() < 0) ||
                    (direction == 1 && enemy.getX() > mapsize * 64) ||
                    (direction == 2 && enemy.getY() > mapsize * 64) ||
                    (direction == 3 && enemy.getY() < 0)) {
                enemy.setX(enemy.getPrevX());
                enemy.setY(enemy.getPrevY());
            } else {
                if (direction == 0 || direction == 1) {
                    enemy.setPrevX(enemy.getX());
                } else {
                    enemy.setPrevY(enemy.getY());
                }
            }
        }

        if (currentAnimation != null) {
            //Draws the animation set in the above steps.
            game.getSpriteBatch().draw(currentAnimation.getKeyFrame(sinusInput, true), enemy.getX(), enemy.getY(), 64, 64);
        } else {
            //Draws the texture currently set to the enemy.
            game.getSpriteBatch().draw(enemy.getCurrentTr(), enemy.getX(), enemy.getY(), 64, 64);
        }
    }

    /**
     * Method responsible for checking and applying user input for character movement,
     * detecting character's collision with maze walls and drawing character texture/
     * animation accordingly. Also, changes character's colors if cool down timer for
     * character speed up and/or collision with enemies/traps is currently active and
     * decreases the timer value accordingly.
     *
     * @param delta The time in seconds since last render.
     */

    private void moveCharacter(float delta) {
        Animation<TextureRegion> currentAnimation = null;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            currentAnimation = character.moveLeft();

            //Resetting character x,y coordinates to previous coordinates if wall collision detected (or)
            // character is outside the leftmost x-coordinate of the maze.
            if (wallManager.wallCollision(character.getX(), character.getY()) || character.getX() < 0) {
                character.setX(character.getPrevX());
                character.setY(character.getPrevY());
            } else {
                //Updating character PrevX value.
                character.setPrevX(character.getX());
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            currentAnimation = character.moveRight();

            //Resetting character x,y coordinates to previous coordinates if wall collision detected (or)
            // character is outside the rightmost x-coordinate of the maze.
            if (wallManager.wallCollision(character.getX(), character.getY()) || (int) character.getX() > mapsize * 64) {
                character.setX(character.getPrevX());
                character.setY(character.getPrevY());
            } else {
                //Updating character PrevX value.
                character.setPrevX(character.getX());
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            currentAnimation = character.moveUp();

            //Resetting character x,y coordinates to previous coordinates if wall collision detected (or)
            // character is outside the uppermost y-coordinate of the maze.
            if (wallManager.wallCollision(character.getX(), character.getY()) || (int) character.getY() > mapsize * 64) {
                character.setX(character.getPrevX());
                character.setY(character.getPrevY());
            } else {
                //Updating character PrevY value.
                character.setPrevY(character.getY());
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            currentAnimation = character.moveDown();

            //Resetting character x,y coordinates to previous coordinates if wall collision detected (or)
            // character is outside the bottom most y-coordinate of the maze.
            if (wallManager.wallCollision(character.getX(), character.getY()) || character.getY() < 0) {
                character.setX(character.getPrevX());
                character.setY(character.getPrevY());
            } else {
                //Updating character PrevY value.
                character.setPrevY(character.getY());
            }
        }

        // Change the color of the character when cool down timer for character speed up and/or collision with an enemy/trap is active.
        // Also, decreases the value(s) of the active timer(s) by the delta.

        if (character.speedUpCooldown(delta) && character.startCoolDownTimer(delta)) {
            game.getSpriteBatch().setColor(1.0f, 0.5f, 0.5f, 1.0f); //Reddish
        } else if (character.speedUpCooldown(delta)) {
            game.getSpriteBatch().setColor(0.5f, 1.0f, 0.5f, 1.0f);//Greenish
        } else if (character.startCoolDownTimer(delta)) {
            game.getSpriteBatch().setColor(1.0f, 0.5f, 0.5f, 1.0f);//Reddish
        } else {
            game.getSpriteBatch().setColor(Color.WHITE);//White
        }


        if (currentAnimation != null) {
            //Draws the animation set to the character in the above steps.
            game.getSpriteBatch().draw(currentAnimation.getKeyFrame(sinusInput, true), character.getX(), character.getY(), 64, 128);
        } else {
            //Draws the texture currently set to the character.
            game.getSpriteBatch().draw(character.getCurrentTr(), character.getX(), character.getY(), 64, 128);
        }
        game.getSpriteBatch().setColor(Color.WHITE);
    }


    /**
     * Method responsible for drawing all the HUD elements in the game like
     * character lives left and whether key has been collected.
     */
    private void renderHUD() {
        float itemSize = 32; //Determines the width and height of each HUD element
        float padding = 10; //Padding between the elements
        int totalHearts = character.getCharacterLives();
        int keysCollected = character.getNumKeys();

        float totalWidth = totalHearts * itemSize + (totalHearts - 1) * padding;

        float startX = character.getX() - totalWidth / 2;
        float keyY = character.getY() + 2 * (itemSize + padding) + 40;

        //Drawing character lives left
        for (int i = 0; i < totalHearts; i++) {
            float heartX = startX + i * (itemSize + padding);
            float heartY = character.getY() + 2 * (itemSize + padding) + 10;

            game.getSpriteBatch().draw(heartRegion, heartX, heartY, itemSize, itemSize);
        }

        // Drawing total number of keys required with a shaded color.
        for (int i = 0; i < totalKeys; i++) {
            float keyX = startX + i * (itemSize + padding);

            Color shadingColor = new Color(0.2f, 0.2f, 0.2f, 1f);
            game.getSpriteBatch().setColor(shadingColor);
            game.getSpriteBatch().draw(key.getTextureRegion(), keyX, keyY, itemSize, itemSize);
            game.getSpriteBatch().setColor(Color.WHITE);
        }

        //Drawing keys collected by the player
        for (int i = 0; i < keysCollected; i++) {
            float keyX = startX + i * (itemSize + padding);
            game.getSpriteBatch().draw(key.getTextureRegion(), keyX, keyY, itemSize, itemSize);
        }
    }

    /**
     * Called when GameScreen is resized. Sets the camera position
     * to character's coordinates.
     *
     * @param width  the new width of the screen after resize.
     * @param height the new height of the screen after resize.
     */
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false);
        camera.position.set(character.getX(), character.getY(), 0);
    }

    /**
     * Disposes all GameScreen resources.
     */
    @Override
    public void dispose() {

        character.disposeTx();

        for (Enemy enemy : enemies) {
            enemy.disposeTx();
        }
        key.disposeTx();
        entryPoint.disposeTx();
        road.disposeTx();
        trap.disposeTx();
        wallManager.disposeTx();
        exit.disposeTx();

        enemySpeedUpTx.getTexture().dispose();
        speedUpTx.getTexture().dispose();
        heartRegion.getTexture().dispose();

    }


    /**
     * Checks if a wall (value 0) exists for the given keypair in the maze file.
     *
     * @param width  the first digit in the keypair.
     * @param height the second digit in the keypair.
     * @return true if wall (value 0) exists for the given keypair.
     */
    private boolean isWall(int width, int height) {
        String key = width + "," + height;
        String value = this.map.getProperty(key);
        return value != null && value.equals("0");
    }

    /**
     * Checks if the wall(value 0) stored in the given keypair represents
     * a middle wall (wall surrounded by other walls on all sides).
     *
     * @param width  the first digit in the keypair.
     * @param height the second digit in the keypair.
     * @return true if wall stored in the given keypair is a middle wall.
     */
    private boolean isMiddleWall(int width, int height) {
        boolean rightSide = isWall(width + 1, height); //Checks for wall on the right side of given wall.
        boolean leftSide = isWall(width - 1, height); //Checks for wall on the left side of given wall.
        boolean belowSide = isWall(width, height - 1);//Checks for wall below the given wall.
        boolean upSide = isWall(width, height + 1); //Checks for wall above the given wall.

        return rightSide && leftSide && belowSide && upSide;

    }

    // The following methods are part of the Screen interface but are not used in this screen.
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

}
