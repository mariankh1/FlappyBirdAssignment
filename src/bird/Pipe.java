package bird;

import javax.swing.*;
import java.awt.*;

public class Pipe {

	// bird.Pipe coordinates
	private int x = FlappyBird.WIDTH + 5;
	private int y;

	// Placement (top or bottom) of pipe
	String location;

	// bird.Pipe constants
	public static final int WIDTH         = 67;
	public static final int HEIGHT        = 416;
	public static final int PIPE_DISTANCE = 150;          // Horizontal distance between pipes
	public static final int PIPE_SPACING  = HEIGHT + 170; // Vertical distance between pipes
	private static final int SPEED        = -2;

	public ImageIcon down = new ImageIcon("res/img/pipe2.png");
	public ImageIcon up = new ImageIcon("res/img/pipe1.png");
	public Pipe (String location) {
		this.location = location;
		reset();
		down=resize(down);
		up=resize(up);
	}

	public void reset () {
		x = FlappyBird.WIDTH + 5; // Reset x-coordinate

		// Set boundaries for top pipes
		// This y-coordinte + PIPE_SPACING will be for the bottom pipe
		if (location.equals("top")) {
			y = - Math.max((int) (Math.random() * 320) + 30, 140);
		}
	}

	/**
	 * Moves the pipe
	 */
	public void move () {
		x += SPEED;
	}


	/**
	 * Checks for bird colliding with pipe
	 *
	 * @param  nX     bird.Bird x-coordinate
	 * @param  nY     bird.Bird y-coordinate
	 * @param  nW     bird.Bird width
	 * @param  nH     bird.Bird height
	 * @return        If bird is colliding with the pipe
	 */
	public boolean collide (int nX, int nY, int nW, int nH) {

		// Do not allow bird to jump over pipe
		if (nX > x && nY < 0) {
			return true;
		}

		return nX < x + WIDTH &&
				nX + nW > x &&
				nY < y + HEIGHT &&
				nY + nH > y;

	}

	/**
	 * @return     bird.Pipe's x-coordinate
	 */
	public int getX () {
		return x;
	}

	/**
	 * @return     bird.Pipe's y-coordinate
	 */
	public int getY () {
		return y;
	}

	/**
	 * Set's pipe's y-coordinate (for bottom pipes)
	 *
	 * @param newY     New y-coordinate
	 */
	public void setY (int newY) {
		y = newY;
	}

	public ImageIcon resize(ImageIcon imageIcon){
		Image image = imageIcon.getImage(); // transform it
		Image newimg = image.getScaledInstance(imageIcon.getIconWidth()* 2, imageIcon.getIconHeight() *2,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
		imageIcon = new ImageIcon(newimg);  // transform it back
		return imageIcon;
	}
}




