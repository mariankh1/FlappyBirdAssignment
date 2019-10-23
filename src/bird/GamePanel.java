package bird; /**
 * bird.GamePanel.java
 * Main game panel
 *
 *
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class GamePanel extends JPanel implements KeyListener, MouseListener {

	private Random rand;
	private Calendar cal;
	
	////////////////////
	// Game variables //
	////////////////////
	
	// Fonts
	private Font flappyFontBase, 
				 flappyFontReal, 
				 flappyScoreFont,
				 flappyMiniFont = null;

	// Textures
	public static HashMap<String, Texture> textures = new Sprites().getGameTextures();	
	
	// Moving base effect
	private static int baseSpeed    = 2;
	private static int[] baseCoords = { 0, 435 };

	// Game states
	final static int MENU = 0;
	final static int GAME = 1;
	private int gameState = MENU;  

	private int pipeDistTracker; // Distance between pipes
	
	public boolean ready = false;                   // If game has loaded
	private boolean inStartGameState = false;       // To show instructions scren
	private Point clickedPoint = new Point(-1, -1); // Store point when player clicks
	public ArrayList<Pipe> pipes;                   // Arraylist of bird.Pipe objects

	private Bird bird;


	public GamePanel () {

		rand = new Random();

		// Try to load ttf file
		try {
			InputStream is = new BufferedInputStream(new FileInputStream("res/fonts/flappy-font.ttf"));
			flappyFontBase = Font.createFont(Font.TRUETYPE_FONT, is);

			// Header and sub-header fonts
			flappyScoreFont = flappyFontBase.deriveFont(Font.PLAIN, 50);
			flappyFontReal  = flappyFontBase.deriveFont(Font.PLAIN, 20);
			flappyMiniFont  = flappyFontBase.deriveFont(Font.PLAIN, 15);

		} catch (Exception ex) {

			// Exit is font cannot be loaded
			ex.printStackTrace();
			System.err.println("Could not load Flappy Font!");
			System.exit(-1);
		}

		restart(); // Reset game variables

		// Input listeners
		addKeyListener(this);
		addMouseListener(this);

	}

	/**
	 * To start game after everything has been loaded
	 */
	public void addNotify() {
		super.addNotify();
		requestFocus();
		ready = true;
	}

	/**
	 * Restarts game by resetting game variables
	 */
	public void restart () {

		// Game bird
		bird = new Bird(172, 250);

		// Remove old pipes
		pipes = new ArrayList<Pipe>();

	}

	/**
	 * Checks if point is in rectangle
	 * 
	 * @param      r     Rectangle
	 * @return           Boolean if point collides with rectangle
	 */
	private boolean isTouching (Rectangle r) {
		return r.contains(clickedPoint);
	}

	@Override
	public void paintComponent (Graphics g) {
		super.paintComponent(g);

		// Set font and color
		g.setFont(flappyFontReal);
		g.setColor(Color.white);

		// Only move screen if bird is alive
		if (bird.isAlive()) {

			// Move base
			baseCoords[0] = baseCoords[0] - baseSpeed < -435 ? 435 : baseCoords[0] - baseSpeed;
			baseCoords[1] = baseCoords[1] - baseSpeed < -435 ? 435 : baseCoords[1] - baseSpeed;

		}



		// Draw bird
		bird.renderBird(g);

		switch (gameState) {

			case MENU:

				drawBase(g);
				drawMenu(g);

				bird.menuFloat();

				break;

			case GAME:

				if (bird.isAlive()) {

					// Start at instructions state
					if (inStartGameState) {
						startGameScreen(g);

					} else {
						// Start game
						pipeHandler(g);
						bird.inGame();
					}

					drawBase(g);                      // Draw base over pipes

				} else {

					pipeHandler(g);
					drawBase(g);

					// Draw game over assets
					gameOver(g);

				}

				break;
		}

	}

	/////////////////////////
	// All drawing methods //
	/////////////////////////

	/**
	 * Draws a string centered based on given restrictions
	 * 
	 * @param s     String to be drawn
	 * @param w     Constraining width
	 * @param h     Constraining height
	 * @param y     Fixed y-coordiate
	 */
	public void drawCentered (String s, int w, int h, int y, Graphics g) {
		FontMetrics fm = g.getFontMetrics();

		// Calculate x-coordinate based on string length and width
		int x = (w - fm.stringWidth(s)) / 2;
		g.drawString(s, x, y);
	}

	/**
	 * Needs to be called differently based on screen
	 */
	public void drawBase (Graphics g) {

		// Moving base effect
		g.drawImage(textures.get("base").getImage(), baseCoords[0], textures.get("base").getY(), null);
		g.drawImage(textures.get("base").getImage(), baseCoords[1], textures.get("base").getY(), null);

	}

	////////////////
	// Menuscreen //
	////////////////

	private void drawMenu (Graphics g) {

		// Title
		g.drawImage(textures.get("titleText").getImage(),
			textures.get("titleText").getX(),
			textures.get("titleText").getY(), null);

		// Buttons
		g.drawImage(textures.get("playButton").getImage(),
			textures.get("playButton").getX(),
			textures.get("playButton").getY(), null);
	}

	/////////////////
	// Game screen //
	/////////////////

	public void startGameScreen (Graphics g) {

		// Set bird's new position
		bird.setGameStartPos();

		// Get ready text
		g.drawImage(textures.get("getReadyText").getImage(),
			textures.get("getReadyText").getX(),
			textures.get("getReadyText").getY(), null);

		// Instructions image
		g.drawImage(textures.get("instructions").getImage(), 
			textures.get("instructions").getX(),
			textures.get("instructions").getY(), null);

	}


	/**
	 * Moves and repositions pipes
	 */
	public void pipeHandler (Graphics g) {

		// Decrease distance between pipes
		if (bird.isAlive()) {
			pipeDistTracker --;
		}

		// Initialize pipes as null
		Pipe topPipe = null;
		Pipe bottomPipe = null;

		// If there is no distance,
		// a new pipe is needed
		if (pipeDistTracker < 0) {

			// Reset distance
			pipeDistTracker = Pipe.PIPE_DISTANCE;

			for (Pipe p : pipes) {

				// If pipe is out of screen
				if (p.getX() < 0) {
					if (topPipe == null) { 
						topPipe = p;
					}
					else if (bottomPipe == null) {
						bottomPipe = p;
					}
				}
			}

			Pipe currentPipe; // New pipe object for top and bottom pipes

			// Move and handle initial creation of top and bottom pipes

			if (topPipe == null) {
				currentPipe = new Pipe("top");
				topPipe = currentPipe;
				pipes.add(topPipe);
			} else {
				topPipe.reset();
			}

			if (bottomPipe == null) {
				currentPipe = new Pipe("bottom");
				bottomPipe = currentPipe;
				pipes.add(bottomPipe);

				// Avoid doubling points when passing initial pipes
			} else {
				bottomPipe.reset();
			}

			// Set y-coordinate of bottom pipe based on 
			// y-coordinate of top pipe
			bottomPipe.setY(topPipe.getY() + Pipe.PIPE_SPACING);

		}

		// Move and draw each pipe

		for (Pipe p : pipes) {

			// Move the pipe
			if (bird.isAlive()) {
				p.move();
			}

			// Draw the top and bottom pipes
			if (p.getY() <= 0) {
				g.drawImage(textures.get("pipe-top").getImage(), p.getX(), p.getY(), null);
			} else {
				g.drawImage(textures.get("pipe-bottom").getImage(), p.getX(), p.getY(), null);
			}

			// Check if bird hits pipes
			if (bird.isAlive()) {
				if (p.collide(
						bird.getX(),
						bird.getY(),
						bird.BIRD_WIDTH,
						bird.BIRD_HEIGHT
				)) {
					// Kill bird and play sound
					bird.kill();

				} else {


				}
			}
		}
	}

	public void gameOver (Graphics g) {
		g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 50));
		g.setColor(Color.BLACK);
		g.drawString("Game Over", 62, 100);
	}


	//////////////////////
	// Keyboard actions //
	//////////////////////

	public void keyTyped (KeyEvent e) {}
	public void keyReleased (KeyEvent e) {}

	public void keyPressed (KeyEvent e) {

		int keyCode = e.getKeyCode();

		if (gameState == MENU) {

			// Start game on 'enter' key
			if (keyCode == KeyEvent.VK_ENTER) {
				gameState = GAME;
				inStartGameState = true;
			}

		} else if (gameState == GAME && bird.isAlive()) {

			if (keyCode == KeyEvent.VK_SPACE) {

				// Exit instructions state
				if (inStartGameState) {
					inStartGameState = false;
				}

				// Jump and play audio even if in instructions state
				bird.jump();

			}
		}
	}

	///////////////////
	// Mouse actions //
	///////////////////

	public void mouseExited (MouseEvent e) {}
	public void mouseEntered (MouseEvent e) {}
	public void mouseReleased (MouseEvent e) {}
	public void mouseClicked (MouseEvent e) {}

	public void mousePressed (MouseEvent e) {

		// Save clicked point
		clickedPoint = e.getPoint();

		if (gameState == MENU) {

			if (isTouching(textures.get("playButton").getRect())) {
				gameState = GAME;
				inStartGameState = true;
			}

		} else if (gameState == GAME) {

			if (bird.isAlive()) {

				// Allow jump with clicks
				if (inStartGameState) {
					inStartGameState = false;
				}
				bird.jump();


			}

		}
	}

}

