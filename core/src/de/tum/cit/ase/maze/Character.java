package de.tum.cit.ase.maze;

import com.badlogic.gdx.audio.Sound;

/**
 * The Character class represents the main character in the game.
 * It extends the DynamicCoordinate class, sets main character
 * attributes and defines additional functionality for the main character.
 */

public class Character extends DynamicCoordinate<Number> {
    private float coolDownTimer; // Cool down timer for enemy or trap collision
    private int characterLives; // Lives left for the character
    private float coolDownSpeedUp;// Cool down timer for character speed up
    private float coolDownEnemy;//Cool down timer for enemy speed up
    private int numKeys;// Number of keys obtained by the character


    /**
     * Constructor for Character. Sets up character coordinates, initial number
     * of lives, keys obtained and initial values for different timers.
     * @param x coordinate of the Character in game
     * @param y coordinate of the Character in game
     */
    public Character(float x, float y) {
        super(x, y, true);
        coolDownTimer = 0.0f;
        coolDownSpeedUp = 0.0f;
        characterLives = 3;
        numKeys = 0;
    }


    /**
     * Used to set coolDownTimer and decrease character lives on collision with
     * traps or enemies (if characterLives is greater than one).
     * @return true if characterLives is greater than 1 else false.
     */
    public boolean decreaseCharacterLives() {

        coolDownTimer = 3.0f;
        if (characterLives > 1) {
            this.characterLives--;
            return true;
        } else {
            return false;
        }
    }


    /**
     * Deceases value of coolDownTimer (for character's collision with enemy/trap)
     * by timeDel on each render if it is greater than zero.
     * @param timeDel the time in seconds since the last render.
     * @return true if coolDownTimer is greater than zero else false.
     */
    public boolean startCoolDownTimer(float timeDel) {
        if (coolDownTimer <= 0) {
            coolDownTimer = 0.0f;
            return false;
        } else {
            coolDownTimer -= timeDel;
            return true;
        }
    }


    /**
     * Decreases value of coolDownSpeedUp (Cool down timer for character speed up)
     * by timeDel on each render if it is greater than zero
     * otherwise resets character's speed to default (180).
     *
     * @param timeDel the time in seconds since the last render.
     * @return true if coolDownSpeedUp is greater than zero else false.
     */
    public boolean speedUpCooldown(float timeDel) {
        if (coolDownSpeedUp <= 0) {
            coolDownSpeedUp = 0.0f;
            setSpeed(180);
            return false;
        } else {
            coolDownSpeedUp -= timeDel;
            return true;
        }
    }



    /**
     * Decreases value of coolDownEnemy (Cool down timer for enemy speed up)
     * by timeDel on each render if it is greater than zero otherwise sets it to zero.
     *
     * @param timeDel the time in seconds since the last render.
     * @return true if coolDownEnemy is greater than zero else false.
     */

    public boolean enemyCooldown(float timeDel) {
        if (coolDownEnemy <= 0) {
            coolDownEnemy = 0.0f;
            return false;
        } else {
            coolDownEnemy -= timeDel;
            return true;
        }
    }



    // getters and setters for different attributes.
    public void setCoolDownEnemy(float coolDownEnemy) {
        this.coolDownEnemy = coolDownEnemy;
    }

    public void setCoolDownSpeedUp(float coolDownSpeedUp) {
        this.coolDownSpeedUp = coolDownSpeedUp;
    }

    public int getCharacterLives() {
        return characterLives;
    }

    public void setCharacterLives(int characterLives) {
        this.characterLives = characterLives;
    }


    public float getCoolDownTimer() {
        return coolDownTimer;
    }


    public int getNumKeys() {
        return numKeys;
    }

    public void setNumKeys(int numKeys) {
        this.numKeys = numKeys;
    }


}

