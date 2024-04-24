package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.IOException;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */
public class MenuScreen implements Screen {

    // Stage for the Menu UI elements
    private final Stage stage;

    //Menu Buttons
    private final TextButton goToGameButton; // To load a maze file
    private final TextButton exitButton; // To close the game
    private TextButton resumeGameButton; // To go back to the paused game


    // Add functionality to the Buttons
    private final ChangeListener goToGame;
    private final ChangeListener exitGame;
    private ChangeListener resumeGame;

    private boolean isPaused; // Whether the game is paused


    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game     The main game class, used to access global resources and methods.
     * @param isPaused Whether the game is currently paused.
     */
    public MenuScreen(MazeRunnerGame game, boolean isPaused) {
        var camera = new OrthographicCamera();
        camera.zoom = 1.5f; // Set camera zoom for a closer view

        this.isPaused = isPaused;

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        // Add a label as a title
        table.add(new Label("Maze Runner Game!", game.getSkin(), "title")).padBottom(80).row();

        // Adding a resumeGameButton only if the MenuScreen is created after pausing the game.
        if (this.isPaused) {
            resumeGameButton = new TextButton("Resume", game.getSkin());
            table.add(resumeGameButton).width(300).row();

            //Creating functionality for resumeGameButton.
            resumeGame = new ChangeListener() {
                //Sets screen to GameScreen, disposes MenuScreen resources and switches background music.
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.setScreen(game.getGameScreen());
                    game.backGroundGamePlay();
                    game.backGroundMenuStop();
                    dispose();

                }
            };
            resumeGameButton.addListener(resumeGame); // Adding functionality to resumeGameButton.
            this.isPaused = false;
        }


        // Adding a button to go to the game screen after choosing valid maze file.
        goToGameButton = new TextButton("Choose a file", game.getSkin());
        table.add(goToGameButton).width(300).row();

        // Creating functionality for the goToGameButton.
        goToGame = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    game.goToGame();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        goToGameButton.addListener(goToGame); //Adding functionality to goToGameButton.

        // Adding a button to close the game.
        exitButton = new TextButton("Exit", game.getSkin());
        table.add(exitButton).width(300).row();

        //Creating functionality for the exitButton.
        exitGame = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        };
        exitButton.addListener(exitGame); // Adding functionality to exitButton.
    }

    // Getter for isPaused
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Called when the MenuScreen should render itself. Clears the screen and
     * then updates and draws the stage.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        stage.draw(); // Draw the stage
    }


    /**
     * Called when MenuScreen should release all resources. Disposes
     * the stage and removes all the listeners for MenuScreen buttons.
     */
    @Override
    public void dispose() {
        // Dispose of the stage when screen is disposed
        stage.dispose();

        //Removal of listeners for MenuScreen buttons.
        goToGameButton.removeListener(goToGame);
        exitButton.removeListener(exitGame);
        if (resumeGameButton != null) {
            resumeGameButton.removeListener(resumeGame);
        }

    }

    /**
     * Called when MenuScreen is resized. Updates the stage viewport on resize.
     *
     * @param width  the new width of the screen after resize.
     * @param height the new height of the screen after resize.
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
    }

    /**
     * Called when MenuScreen becomes the current screen in the Game. Sets
     * the input processor so the stage can receive input events.
     */
    @Override
    public void show() {
        // Set the input processor so the stage can receive input events
        Gdx.input.setInputProcessor(stage);
    }


    // The following methods are part of the Screen interface but are not used in this screen.
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}
