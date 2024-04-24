package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;
import games.spooky.gdx.nativefilechooser.NativeFileChooserIntent;

import java.io.IOException;

/**
 * The MazeRunnerGame class represents the core of the Maze Runner game.
 * Extends the Game class and manages the screens and global resources
 * like SpriteBatch, Skin etc.
 */
public class MazeRunnerGame extends Game {
    // Screens
    private MenuScreen menuScreen;
    private GameScreen gameScreen;

    // Sprite Batch for rendering
    private SpriteBatch spriteBatch;

    // UI Skin
    private Skin skin;

    //Background Music
    private Music backgroundGame;
    private Music backgroundMenu;

    private final NativeFileChooser fileChooser;
    private final NativeFileChooserConfiguration fileChooserConfig;
    private String filePath;


    /**
     * Constructor for MazeRunnerGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
        this.fileChooser = fileChooser;
        fileChooserConfig = new NativeFileChooserConfiguration();
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch,Skin and background music
     * for the gameScreen and menuScreen.
     */
    @Override
    public void create() {

        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json")); // Load UI skin

        // Play some background music
        // Background sound
        backgroundGame = Gdx.audio.newMusic(Gdx.files.internal("background.mp3"));
        backgroundGame.setVolume(0.5f);
        backgroundMenu = Gdx.audio.newMusic(Gdx.files.internal("i_want_to_go_home_MENU.wav"));
        backgroundMenu.setLooping(true);
        backgroundGame.setLooping(true);
        goToMenu(false); // Navigate to the menu screen
    }

    /**
     * Switches the screen to the menu screen, stops gameScreen background music (if playing)
     * and plays menuScreen background music.
     */
    public void goToMenu(boolean isPaused) {
        this.menuScreen = new MenuScreen(this, isPaused);
        this.setScreen(menuScreen); // Set the current screen to MenuScreen

        if (backgroundGame.isPlaying()) {
            backgroundGame.stop();
        }
        backgroundMenu.play();

    }


    /**
     * Stops gameScreen background music (if playing).
     */
    public void backGroundGameStop() {
        if (backgroundGame.isPlaying()) {
            backgroundGame.stop();
        }
    }

    /**
     * Stops menuScreen background music (if playing).
     */
    public void backGroundMenuStop() {
        if (backgroundMenu.isPlaying()) {
            backgroundMenu.stop();
        }
    }

    /**
     * Plays gameScreen background music.
     */
    public void backGroundGamePlay() {
        backgroundGame.play();
    }

    /**
     * Checks whether the game is currently paused.
     *
     * @return true if menuScreen is not null and
     * created after pausing the game else false.
     */
    public boolean isPaused() {
        if (menuScreen != null) {
            return !menuScreen.isPaused();
        } else {
            return true;
        }
    }

    /**
     * Opens a new window for choosing a maze file (.properties). If
     * appropriate file is chosen, initializes a new gameScreen with
     * the chosen maze file and switches to it.Also,disposes the menuScreen
     * resources and sets it to null(if not null)and switches background music.
     *
     * @throws IOException If any I/O error occurs during the file handling.
     */
    public void goToGame() throws IOException {


        fileChooserConfig.title = "Pick a maze file"; // Title of the window that will be opened
        fileChooserConfig.intent = NativeFileChooserIntent.OPEN; // We want to open a file
        fileChooserConfig.nameFilter = (file, name) -> name.endsWith("properties"); // Only accept .properties files
        fileChooserConfig.directory = Gdx.files.absolute(System.getProperty("user.home")); // Open at the user's home directory


        fileChooser.chooseFile(fileChooserConfig, new NativeFileChooserCallback() {
            @Override
            public void onFileChosen(FileHandle fileHandle) {
                // Do something with fileHandle
                filePath = fileHandle.path();

                // Here filePath is effective?
                if (filePath != null && filePath.endsWith("properties")) {
                    //Creates new GameScreen using the chosen file and switches to it.
                    try {
                        gameScreen = new GameScreen(MazeRunnerGame.this, filePath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    setScreen(gameScreen);

                    //Switches the background music to game background music.
                    backgroundMenu.stop();
                    backgroundGame.play();

                    if (menuScreen != null) {
                        menuScreen.dispose();
                        menuScreen = null;
                    }
                }
            }

            @Override
            public void onCancellation() {
                // User closed the window, don't need to do anything
            }

            @Override
            public void onError(Exception exception) {
                System.err.println("Error picking maze file: " + exception.getMessage());
            }
        });


    }

    /**
     * Cleans up resources when the game is disposed.
     */
    @Override
    public void dispose() {
        getScreen().hide(); // Hide the current screen
        getScreen().dispose(); // Dispose the current screen
        spriteBatch.dispose(); // Dispose the spriteBatch
        skin.dispose(); // Dispose the skin
    }

    // Getter methods
    public Skin getSkin() {
        return skin;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }


}
