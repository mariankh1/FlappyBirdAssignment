package bird; /**
 * bird.Bird.java
 * Handles bird's state and actions
 *
 * @author  Paul Krishnamurthy
 */

import javax.swing.*;
import java.awt.*;

public class Bird extends JPanel {
	// bird.Bird attributes
	public String color;
	private int x, y;
	private boolean isAlive = true;
	
	// bird.Bird constants
	private int FLOAT_MULTIPLIER      = -1;
	public final int BIRD_WIDTH       = 44;
	public final int BIRD_HEIGHT      = 31;
	private final int BASE_COLLISION  = 521 - BIRD_HEIGHT - 5;
	private final int SHIFT           = 10;
	private final int STARTING_BIRD_X = 90;
	private final int STARTING_BIRD_Y = 343;
	
	// Physics variables
	private double velocity           = 0;
	private double gravity            = .41;
	private double delay              = 0;
	private double rotation           = 0;


    ImageIcon imageIcon = new ImageIcon("res/img/flappy.png"); // load the image to a imageIcon


	public Bird ( int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return     bird.Bird's x-coordinate
	 */
	public int getX () {
		return x;
	}

	/**
	 * @return     bird.Bird's y-coordinate
	 */
	public int getY () {
		return y;
	}

	/**
	 * @return     If bird is alive
	 */
	public boolean isAlive () {
		return isAlive;
	}

	/**
	 * Kills bird
	 */
	public void kill () {
		isAlive = false;
	}

	/**
	 * Set new coordinates when starting game
	 */
	public void setGameStartPos () {
		x = STARTING_BIRD_X;
		y = STARTING_BIRD_Y;
	}

	/**
	 * Floating bird effect on menu screen
	 */
	public void menuFloat () {

		y += FLOAT_MULTIPLIER;

		// Change direction within floating range
		if (y < 220) {
			FLOAT_MULTIPLIER *= -1;
		} else if (y > 280) {
			FLOAT_MULTIPLIER *= -1;
		}

	}

	/**
	 * bird.Bird jump
	 */
	public void jump () {

		if (delay < 1) {
			velocity = -SHIFT;
			delay = SHIFT;
		}

	}

	/**
	 * bird.Bird movement during the game
	 */
	public void inGame () {

		// If the bird did not hit the base, lower it
		if (y < BASE_COLLISION) {

			// Change and velocity
			velocity += gravity;

			// Lower delay if possible
			if (delay > 0) { delay--; }

			// Add rounded velocity to y-coordinate
			y += (int) velocity;

		} else {

			isAlive = false;
		}

	}

	/**
	 * Renders bird
	 */
	public void renderBird (Graphics g) {

		// Calculate angle to rotate bird based on y-velocity
		rotation = ((90 * (velocity + 25) / 25) - 90) * Math.PI / 180;
		
		// Divide for clean jump
		rotation /= 2;

		// Handle rotation offset
		rotation = rotation > Math.PI / 2 ? Math.PI / 2 : rotation;

		if (!isAlive()) {

			// Drop bird on death
			if (y < BASE_COLLISION - 10) {
				velocity += gravity;
				y += (int) velocity;
			}

		}

        g.drawImage(resize(imageIcon).getImage(),x,y, this);

	}

	public ImageIcon resize(ImageIcon imageIcon){
        Image image = imageIcon.getImage(); // transform it
        Image newimg = image.getScaledInstance(imageIcon.getIconWidth()* 2, imageIcon.getIconHeight() *2,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        imageIcon = new ImageIcon(newimg);  // transform it back
        return imageIcon;
    }
}
