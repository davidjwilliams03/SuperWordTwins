import java.awt.Image;
import java.util.ArrayList;


/**
    The Animation class manages a series of images (frames) and
    the amount of time to display each frame.
*/
public class Animation {

//    private GamePanel panel;					// JPanel on which animation is being displayed
    private ArrayList<AnimFrame> frames;			// collection of frames for animation
    private int currFrameIndex;					// current frame being displayed
    private long animTime;					// time that the animation has run for already
    private long startTime;					// start time of the animation or time since last update
    private long totalDuration;					// total duration of the animation

    private boolean loop;
    private boolean isActive;

    /**
        Creates a new, empty Animation.
    */
    public Animation(boolean loop) {
        frames = new ArrayList<AnimFrame>();
        totalDuration = 0;
	this.loop = loop;
	isActive = false;
    }


    /**
        Adds an image to the animation with the specified
        duration (time to display the image).
    */
    public synchronized void addFrame(Image image, long duration)
    {
        totalDuration += duration;
        frames.add(new AnimFrame(image, totalDuration));
    }


    /**
        Starts this animation over from the beginning.
    */
    public synchronized void start() {
	isActive = true;
        animTime = 0;						// reset time animation has run for to zero
        currFrameIndex = 0;					// reset current frame to first frame
	startTime = System.currentTimeMillis();			// reset start time to current time
    }


    /**
        Terminates this animation.
    */
    public synchronized void stop() {
	isActive = true;
    }


    /**
        Updates this animation's current image (frame), if
        neccesary.
    */
    public synchronized void update() {

	if (!isActive)
	    return;

        long currTime = System.currentTimeMillis();		// find the current time
	long elapsedTime = currTime - startTime;		// find how much time has elapsed since last update
	startTime = currTime;					// set start time to current time

        if (frames.size() > 1) {
            animTime += elapsedTime;				// add elapsed time to amount of time animation has run for
            if (animTime >= totalDuration) {			// if the time animation has run for > total duration
		if (loop) {
                    animTime = animTime % totalDuration;	// reset time animation has run for
                    currFrameIndex = 0;				// reset current frame to first frame
		}
		else { 
	            isActive = false;				// set to false to terminate animation
		}
            }

	    if (!isActive)
	       return;

            while (animTime > getFrame(currFrameIndex).endTime) {
                currFrameIndex++;				// set frame corresponding to time animation has run for
            }
        }
	
    }


    /**
        Gets this Animation's current image. Returns null if this
        animation has no images.
    */
    public synchronized Image getImage() {
        if (frames.size() == 0) {
            return null;
        }
        else {
            return getFrame(currFrameIndex).image;
        }
    }


    public int getNumFrames() {					// find out how many frames in animation
	return frames.size();
    }


    private AnimFrame getFrame(int i) {				// returns ith frame in the collection
        return frames.get(i);
    }


    public boolean isStillActive () {
	return isActive;
    }


    private class AnimFrame {					// inner class for the frames of the animation

        Image image;
        long endTime;

        public AnimFrame(Image image, long endTime) {
            this.image = image;
            this.endTime = endTime;
        }
    }

}

import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Image;
import javax.swing.JPanel;
import javax.swing.ImageIcon;

public class Background {
  	private Image bgImage;
  	private int bgImageWidth;      		// width of the background (>= panel Width)

	private Dimension dimension;

 	private int bgX;
	private int backgroundX;
	private int backgroundX2;
	private int bgDX;			// size of the background move (in pixels)


	public Background(JPanel panel, String imageFile, int bgDX) {

    		this.bgImage = loadImage(imageFile);
    		bgImageWidth = bgImage.getWidth(null);	// get width of the background

		System.out.println ("bgImageWidth = " + bgImageWidth);

		dimension = panel.getSize();

		if (bgImageWidth < dimension.width)
      			System.out.println("Background width < panel width");

    		this.bgDX = bgDX;

  	}


  	public void moveRight() {

		if (bgX == 0) {
			backgroundX = 0;
			backgroundX2 = bgImageWidth;			
		}

		bgX = bgX - bgDX;

		backgroundX = backgroundX - bgDX;
		backgroundX2 = backgroundX2 - bgDX;

		if ((bgX + bgImageWidth) % bgImageWidth == 0) {
			System.out.println ("Background change: bgX = " + bgX); 
			backgroundX = 0;
			backgroundX2 = bgImageWidth;
		}

  	}


  	public void moveLeft() {
	
		if (bgX == 0) {
			backgroundX = bgImageWidth * -1;
			backgroundX2 = 0;			
		}

		bgX = bgX + bgDX;
				
		backgroundX = backgroundX + bgDX;	
		backgroundX2 = backgroundX2 + bgDX;

		if ((bgX + bgImageWidth) % bgImageWidth == 0) {
			//System.out.println ("Background change: bgX = " + bgX); 
			backgroundX = bgImageWidth * -1;
			backgroundX2 = 0;
		}			
   	}
 

  	public void draw (Graphics2D g2) {
		g2.drawImage(bgImage, backgroundX, 0, null);
		g2.drawImage(bgImage, backgroundX2, 0, null);
  	}


  	public Image loadImage (String fileName) {
		return new ImageIcon(fileName).getImage();
  	}

}

/* BackgroundManager manages many backgrounds (wraparound images 
   used for the game's background). 

   Backgrounds 'further back' move slower than ones nearer the
   foreground of the game, creating a parallax distance effect.

   When a sprite is instructed to move left or right, the sprite
   doesn't actually move, instead the backgrounds move in the 
   opposite direction (right or left).

*/

import java.awt.Graphics2D;
import javax.swing.JPanel;


public class BackgroundManager {

	private String bgImages[] = {"images/layer_08.png",
			       	     "images/layer_07.png",
				     "images/layer_06.png",
				     "images/layer_05.png",
				     "images/layer_04.png",
				     "images/layer_03.png",
				     "images/layer_02.png",
			       	     "images/layer_01.png"};

  private int moveAmount[] = {2, 4, 6, 8, 8, 8, 10, 20};  
						// pixel amounts to move each background left or right
     						// a move amount of 0 makes a background stationary

  	private Background[] backgrounds;
  	private int numBackgrounds;

  	private JPanel panel;			// JPanel on which backgrounds are drawn

  	public BackgroundManager(JPanel panel, int moveSize) {
						// ignore moveSize
    		this.panel = panel;

    		numBackgrounds = bgImages.length;
    		backgrounds = new Background[numBackgrounds];

    		for (int i = 0; i < numBackgrounds; i++) {
       			backgrounds[i] = new Background(panel, bgImages[i], moveAmount[i]);
    		}
  	} 


  	public void moveRight() { 
		for (int i=0; i < numBackgrounds; i++)
      			backgrounds[i].moveRight();
  	}


  	public void moveLeft() {
		for (int i=0; i < numBackgrounds; i++)
      			backgrounds[i].moveLeft();
  	}


  	// The draw method draws the backgrounds on the screen. The
  	// backgrounds are drawn from the back to the front.

  	public void draw (Graphics2D g2) { 
		for (int i=0; i < numBackgrounds; i++)
      			backgrounds[i].draw(g2);
  	}

}

import java.awt.Image;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Graphics2D;


/**
    The BirdAnimation class creates an animation of a flying bird. 
*/
public class BirdAnimation {
	
	Animation animation;

	private int x;		// x position of animation
	private int y;		// y position of animation

	private int width;
	private int height;

	private int dx;		// increment to move along x-axis
	private int dy;		// increment to move along y-axis

    	private SoundManager soundManager;		// reference to SoundManager to play clip

	public BirdAnimation() {

        	dx = 10;	// increment to move along x-axis
        	dy = -3;	// increment to move along y-axis

		// load images for flying bird animation

		Image animImage1 = ImageManager.loadImage("images/bird1.png");
		Image animImage2 = ImageManager.loadImage("images/bird2.png");
		Image animImage3 = ImageManager.loadImage("images/bird3.png");
		Image animImage4 = ImageManager.loadImage("images/bird4.png");
		Image animImage5 = ImageManager.loadImage("images/bird5.png");
		Image animImage6 = ImageManager.loadImage("images/bird6.png");
		Image animImage7 = ImageManager.loadImage("images/bird7.png");
		Image animImage8 = ImageManager.loadImage("images/bird8.png");
		Image animImage9 = ImageManager.loadImage("images/bird9.png");
	
		// create animation object and insert frames

		animation = new Animation(false);	// play once only

		animation.addFrame(animImage1, 200);
		animation.addFrame(animImage2, 200);
		animation.addFrame(animImage3, 200);
		animation.addFrame(animImage4, 200);
		animation.addFrame(animImage5, 200);
		animation.addFrame(animImage6, 200);
		animation.addFrame(animImage7, 200);
		animation.addFrame(animImage8, 200);
		animation.addFrame(animImage9, 100);

		soundManager = SoundManager.getInstance();	
						// get reference to Singleton instance of SoundManager	}
	}


	public void start() {
		x = 100;
        	y = 300;
		animation.start();
		playSound();
	}

	
	public void update() {

		if (!animation.isStillActive()) {
			stopSound();
			return;
		}

		animation.update();

		x = x + dx;
		y = y + dy;

		if (x > 800)
			x = 100;
	}


	public void draw(Graphics2D g2) {

		if (!animation.isStillActive()) {
			return;
		}

		g2.drawImage(animation.getImage(), x, y, 150, 125, null);
	}


    	public void playSound() {
		soundManager.playSound("birdSound", true);
    	}


    	public void stopSound() {
		soundManager.stopSound("birdSound");
    	}

}


import javax.swing.*;

public class GameApplication
{
	public static void main (String[] args) {

      		JFrame frame = new GameWindow();
	}

}


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
   A component that displays all the game entities
*/

public class GamePanel extends JPanel
		       implements Runnable {

	private SoundManager soundManager;

	private boolean isRunning;
	private boolean isPaused;

	private Thread gameThread;

	private BufferedImage image;
 	private Image backgroundImage;

	private BirdAnimation animation;
	private volatile boolean isAnimShown;
	private volatile boolean isAnimPaused;

	private ImageEffect imageEffect;		// sprite demonstrating an image effect

	private TileMapManager tileManager;
	private TileMap	tileMap;

	private boolean levelChange;
	private int level;
	private boolean gameOver;

	public GamePanel () {

		isRunning = false;
		isPaused = false;
		isAnimShown = false;
		isAnimPaused = false;

		soundManager = SoundManager.getInstance();

		// Initialize buffer to match preferred size (1300x700)
		image = new BufferedImage (1300, 700, BufferedImage.TYPE_INT_RGB);

		level = 1;
		levelChange = false;
	}


	public void createGameEntities() {
		animation = new BirdAnimation();
		imageEffect = new ImageEffect (this);
	}


	public void run () {
		try {
			isRunning = true;
			while (isRunning) {
				if (!isPaused && !gameOver)
					gameUpdate();
				gameRender();
				Thread.sleep (50);	
			}
		}
		catch(InterruptedException e) {}
	}

	private void checkInput() {
		if (tileMap == null || gameOver) return;

		if (GameWindow.isKeyPressed(KeyEvent.VK_LEFT)) moveLeft();
		if (GameWindow.isKeyPressed(KeyEvent.VK_RIGHT)) moveRight();
		if (GameWindow.isKeyPressed(KeyEvent.VK_SPACE)) jump();
	}

	public void gameUpdate() {

		checkInput();
		tileMap.update();

		if (levelChange) {
			levelChange = false;
			tileManager = new TileMapManager (this);

			try {
				String filename = (level == 1) ? "maps/map_export.txt" : "maps/map" + level + ".txt";
				tileMap = tileManager.loadMap(filename) ;
				int w, h;
				w = tileMap.getWidth();
				h = tileMap.getHeight();
				System.out.println ("Changing level to Level " + level);
				System.out.println ("Width of tilemap " + w);
				System.out.println ("Height of tilemap " + h);
			}
			catch (Exception e) {		// no more maps: terminate game
				gameOver = true;
				System.out.println(e);
				System.out.println("Game Over"); 
				return;
/*
				System.exit(0);
*/
			}

			createGameEntities();
			return;
				
		}

		if (!isPaused && isAnimShown)
			animation.update();

		imageEffect.update();
	}


	public void gameRender() {
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image == null || tileMap == null) return;

		Graphics2D imageContext = image.createGraphics();
		tileMap.draw(imageContext);

		if (isAnimShown && animation != null) {
			animation.draw(imageContext);
		}

		if (gameOver) {
			imageContext.setColor(new Color(0, 0, 0, 125));
			imageContext.fill(new Rectangle2D.Double(0, 0, image.getWidth(), image.getHeight()));
		}

		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		imageContext.dispose();
	}

	public void startGame() {				// initialise and start the game thread 

		if (gameThread == null) {
			//soundManager.playSound ("background", true);

			gameOver = false;

			tileManager = new TileMapManager (this);

			try {
				tileMap = tileManager.loadMap("maps/map_export.txt");
				int w, h;
				w = tileMap.getWidth();
				h = tileMap.getHeight();
				System.out.println ("Width of tilemap " + w);
				System.out.println ("Height of tilemap " + h);
			}
			
			catch (Exception e) {
				System.out.println(e);
				System.exit(0);
			}
			image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

			createGameEntities();

			gameThread = new Thread(this);
			gameThread.start();			

		}
	}


	public void startNewGame() {				// initialise and start a new game thread 
		if (gameThread != null || !isRunning) {
			//soundManager.playSound ("background", true);

			endGame();

			gameOver = false;
			level = 1;

			tileManager = new TileMapManager (this);

			try {
				tileMap = tileManager.loadMap("maps/map_export.txt");
				int w, h;
				w = tileMap.getWidth();
				h = tileMap.getHeight();
				System.out.println ("Width of tilemap " + w);
				System.out.println ("Height of tilemap " + h);
			}
			catch (Exception e) {
				System.out.println(e);
				System.exit(0);
			}

			createGameEntities();

			gameThread = new Thread(this);
			gameThread.start();			

		}
	}


	public void pauseGame() {				// pause the game (don't update game entities)
		if (isRunning) {
			if (isPaused)
				isPaused = false;
			else
				isPaused = true;

			if (isAnimShown) {
				if (isPaused)
					animation.stopSound();
				else
					animation.playSound();
			}
		}
	}


	public void endGame() {					// end the game thread
		isRunning = false;
		//soundManager.stopClip ("background");
	}

	
	public void moveLeft() {
		if (!gameOver)
			tileMap.moveLeft();
	}


	public void moveRight() {
		if (!gameOver)
			tileMap.moveRight();
	}


	public void jump() {
		if (!gameOver)
			tileMap.jump();
	}

	
	public void showAnimation() {
		isAnimShown = true;
		animation.start();
		
	}


	public void endLevel() {
		level = level + 1;
		levelChange = true;
	}

}


import java.awt.*;			// need this for GUI objects
import java.awt.event.*;			// need this for Layout Managers
import javax.swing.*;		// need this to respond to GUI events
	
public class GameWindow extends JFrame 
				implements ActionListener,
					   KeyListener,
					   MouseListener
{
	// declare instance variables for user interface objects

	// declare labels 

	private JLabel statusBarL;
	private JLabel keyL;
	private JLabel mouseL;

	// declare text fields

	private JTextField statusBarTF;
	private JTextField keyTF;
	private JTextField mouseTF;

	// declare buttons

	private JButton startB;
	private JButton pauseB;
	private JButton endB;
	private JButton startNewB;
	private JButton focusB;
	private JButton exitB;

	private Container c;

	private JPanel mainPanel;
	private GamePanel gamePanel;

	private static final boolean[] keys = new boolean[1024];

	public static boolean isKeyPressed(int keyCode) {
		if (keyCode >= 0 && keyCode < keys.length) return keys[keyCode];
		return false;
	}

	@SuppressWarnings({"unchecked"})
	public GameWindow() {
 
		setTitle ("Tiled Bat and Ball Game: Ordinary Windowed Mode");
		setSize (1350, 900);

		// create user interface objects

		// create labels

		statusBarL = new JLabel ("Application Status: ");
		keyL = new JLabel("Key Pressed: ");
		mouseL = new JLabel("Location of Mouse Click: ");

		// create text fields and set their colour, etc.

		statusBarTF = new JTextField (25);
		keyTF = new JTextField (25);
		mouseTF = new JTextField (25);

		statusBarTF.setEditable(false);
		keyTF.setEditable(false);
		mouseTF.setEditable(false);

		statusBarTF.setBackground(Color.CYAN);
		keyTF.setBackground(Color.YELLOW);
		mouseTF.setBackground(Color.GREEN);

		// create buttons

	        startB = new JButton ("Start Game");
	        pauseB = new JButton ("Pause Game");
	        endB = new JButton ("End Game");
		startNewB = new JButton ("Start New Game");
	        focusB = new JButton ("Show Animation");
		exitB = new JButton ("Exit");


		// add listener to each button (same as the current object)

		startB.addActionListener(this);
		pauseB.addActionListener(this);
		endB.addActionListener(this);
		startNewB.addActionListener(this);
		focusB.addActionListener(this);
		exitB.addActionListener(this);
		
		// create mainPanel

		mainPanel = new JPanel();
		FlowLayout flowLayout = new FlowLayout();
		mainPanel.setLayout(flowLayout);

		GridLayout gridLayout;

		// create the gamePanel for game entities

		gamePanel = new GamePanel();
        	gamePanel.setPreferredSize(new Dimension(1300, 700));

		// create infoPanel

		JPanel infoPanel = new JPanel();
		gridLayout = new GridLayout(3, 2);
		infoPanel.setLayout(gridLayout);
		infoPanel.setBackground(Color.ORANGE);

		// add user interface objects to infoPanel
	
		infoPanel.add (statusBarL);
		infoPanel.add (statusBarTF);

		infoPanel.add (keyL);
		infoPanel.add (keyTF);		

		infoPanel.add (mouseL);
		infoPanel.add (mouseTF);

		
		// create buttonPanel

		JPanel buttonPanel = new JPanel();
		gridLayout = new GridLayout(2, 3);
		buttonPanel.setLayout(gridLayout);

		// add buttons to buttonPanel

		buttonPanel.add (startB);
		buttonPanel.add (pauseB);
		buttonPanel.add (endB);
		buttonPanel.add (startNewB);
		buttonPanel.add (focusB);
		buttonPanel.add (exitB);

		// add sub-panels with GUI objects to mainPanel and set its colour

		mainPanel.add(infoPanel);
		mainPanel.add(gamePanel);
		mainPanel.add(buttonPanel);
		mainPanel.setBackground(Color.PINK);

		// set up mainPanel to respond to keyboard and mouse

		gamePanel.addMouseListener(this);
		mainPanel.addKeyListener(this);

		// add mainPanel to window surface

		c = getContentPane();
		c.add(mainPanel);

		// set properties of window

		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setVisible(true);

		// set status bar message

		statusBarTF.setText("Application started.");
	}


	// implement single method in ActionListener interface

	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		
		statusBarTF.setText(command + " button clicked.");

		if (command.equals(startB.getText())) {
			gamePanel.startGame();
		}

		if (command.equals(pauseB.getText())) {
			gamePanel.pauseGame();
			if (command.equals("Pause Game"))
				pauseB.setText ("Resume");
			else
				pauseB.setText ("Pause Game");

		}
		
		if (command.equals(endB.getText())) {
			gamePanel.endGame();
		}

		if (command.equals(startNewB.getText()))
			gamePanel.startNewGame();

		if (command.equals(focusB.getText()))
			gamePanel.showAnimation();

		if (command.equals(exitB.getText()))
			System.exit(0);

		mainPanel.requestFocus();
	}


	// implement methods in KeyListener interface

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		String keyText = e.getKeyText(keyCode);
		keyTF.setText(keyText + " pressed.");
		
		if (keyCode >= 0 && keyCode < keys.length) {
			keys[keyCode] = true;
		}
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode >= 0 && keyCode < keys.length) {
			keys[keyCode] = false;
		}
	}

	public void keyTyped(KeyEvent e) {

	}


	// implement methods in MouseListener interface

	public void mouseClicked(MouseEvent e) {

		int x = e.getX();
		int y = e.getY();

		mouseTF.setText("(" + x +", " + y + ")");

	}


	public void mouseEntered(MouseEvent e) {
	
	}

	public void mouseExited(MouseEvent e) {
	
	}

	public void mousePressed(MouseEvent e) {
	
	}

	public void mouseReleased(MouseEvent e) {
	
	}

}

import java.util.Random;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;


public class Heart {

	private static final int XSIZE = 50;		// width of the image
	private static final int YSIZE = 50;		// height of the image
	//private static final int DX = 2;		// amount of pixels to move in one update
	private static final int YPOS = 150;		// vertical position of the image

	private JPanel panel;				// JPanel on which image will be drawn
	private Dimension dimension;
	private int x;
	private int y;
	private int dx;

	private Player player;

	private Image spriteImage;			// image for sprite

	//Graphics2D g2;

	int time, timeChange;				// to control when the image is grayed
	boolean originalImage, grayImage;


	public Heart (JPanel panel, Player player) {
		this.panel = panel;
		//Graphics g = window.getGraphics ();
		//g2 = (Graphics2D) g;

		dimension = panel.getSize();
		Random random = new Random();
		//x = 4174;	
		x = 4128;
		y = 270;
		dx = 2;

		this.player = player;

		time = 0;				// range is 0 to 10
		timeChange = 1;				// set to 1
		originalImage = true;
		grayImage = false;

		spriteImage = ImageManager.loadImage("images/Heart.png");

	}


	public void draw (Graphics2D g2) {

		g2.drawImage(spriteImage, x, y, XSIZE, YSIZE, null);

	}


	public boolean collidesWithPlayer () {
		Rectangle2D.Double myRect = getBoundingRectangle();
		Rectangle2D.Double playerRect = player.getBoundingRectangle();
		
		if (myRect.intersects(playerRect)) {
			System.out.println ("Collision with player!");
			return true;
		}
		else
			return false;
	}


	public Rectangle2D.Double getBoundingRectangle() {
		return new Rectangle2D.Double (x, y, XSIZE, YSIZE);
	}


	public void update() {				
		x = x + dx;

		if (x < 4064 || x > 4184)
			dx = dx * -1;

	}


   	public int getX() {
      		return x;
   	}


   	public void setX(int x) {
      		this.x = x;
   	}


   	public int getY() {
      		return y;
   	}


   	public void setY(int y) {
      		this.y = y;
   	}


   	public Image getImage() {
      		return spriteImage;
   	}

}


import java.util.Random;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;


public class ImageEffect {

	private static final int XSIZE = 100;		// width of the image
	private static final int YSIZE = 100;		// height of the image
	private static final int XSTEP = 7;		// amount of pixels to move in one keystroke
	private static final int YPOS = 150;		// vertical position of the image

	private JPanel panel;				// JPanel on which image will be drawn
	private Dimension dimension;
	private int x;
	private int y;

	private BufferedImage spriteImage;		// image for sprite effect
	private BufferedImage copy;			// copy of image

	//Graphics2D g2;

	int time, timeChange;				// to control when the image is grayed
	boolean originalImage, grayImage;


	public ImageEffect (JPanel panel) {
		this.panel = panel;
		//Graphics g = window.getGraphics ();
		//g2 = (Graphics2D) g;

		dimension = panel.getSize();
		Random random = new Random();
		x = random.nextInt (dimension.width - XSIZE);
		y = YPOS;

		time = 0;				// range is 0 to 10
		timeChange = 1;				// set to 1
		originalImage = true;
		grayImage = false;

		spriteImage = loadImage("images/Butterfly.png");
		copy = copyImage(spriteImage);		//  copy original image

	}


	public int toGray (int pixel) {

  		int alpha, red, green, blue, gray;
		int newPixel;

		alpha = (pixel >> 24) & 255;
		red = (pixel >> 16) & 255;
		green = (pixel >> 8) & 255;
		blue = pixel & 255;

		gray = (red + green + blue) / 3;	// Calculate the value for gray

		// Set red, green, and blue channels to gray

		red = green = blue = gray;

		newPixel = blue | (green << 8) | (red << 16) | (alpha << 24);
		return newPixel;
	}


	public void draw (Graphics2D g2) {

		copy = copyImage(spriteImage);		//  copy original image

		if (originalImage) {			// draw copy (already in colour) and return
			g2.drawImage(copy, x, y, XSIZE, YSIZE, null);
			return;
		}
							// change to gray and then draw
		int imWidth = copy.getWidth();
		int imHeight = copy.getHeight();

    		int [] pixels = new int[imWidth * imHeight];
    		copy.getRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);

    		int alpha, red, green, blue, gray;

		for (int i=0; i<pixels.length; i++) {
			if (grayImage)
				pixels[i] = toGray(pixels[i]);
		}
  
    		copy.setRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);	

		g2.drawImage(copy, x, y, XSIZE, YSIZE, null);

	}


	public Rectangle2D.Double getBoundingRectangle() {
		return new Rectangle2D.Double (x, y, XSIZE, YSIZE);
	}


	public void update() {				// modify time and change the effect if required
	
		time = time + timeChange;

		if (time < 20) {
			originalImage = true;
			grayImage = false;
		}
		else
		if (time < 40) {
			originalImage = false;
			grayImage = true;
		}
		else {		
			time = 0;
		}
	}


	public BufferedImage loadImage(String filename) {
		BufferedImage bi = null;

		File file = new File (filename);
		try {
			bi = ImageIO.read(file);
		}
		catch (IOException ioe) {
			System.out.println ("Error opening file " + filename + ": " + ioe);
		}
		return bi;
	}


  	// make a copy of the BufferedImage passed as a parameter

	public BufferedImage copyImage(BufferedImage src) {
		if (src == null)
			return null;

		BufferedImage copy = new BufferedImage (src.getWidth(), src.getHeight(),
							BufferedImage.TYPE_INT_ARGB);

    		Graphics2D g2d = copy.createGraphics();

    		g2d.drawImage(src, 0, 0, null);		// source image is drawn on copy
    		g2d.dispose();

    		return copy; 
	  }

}




import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
   The ImageManager class manages the loading and processing of images.
*/

public class ImageManager {
    private static final Map<String, Image> imageCache = new HashMap<>();
    private static final Map<String, BufferedImage> bufferedImageCache = new HashMap<>();

	public static Image loadImage (String fileName) {
		if (imageCache.containsKey(fileName)) {
			return imageCache.get(fileName);
		}
		Image img = new ImageIcon(fileName).getImage();
		imageCache.put(fileName, img);
		return img;
	}

	public static BufferedImage loadBufferedImage(String filename) {
		if (bufferedImageCache.containsKey(filename)) {
			return bufferedImageCache.get(filename);
		}
		BufferedImage bi = null;

		File file = new File (filename);
		try {
			bi = ImageIO.read(file);
		}
		catch (IOException ioe) {
			System.out.println ("Error opening file " + filename + ":" + ioe);
		}
		bufferedImageCache.put(filename, bi);
		return bi;
	}


  	// make a copy of the BufferedImage src

	public static BufferedImage copyImage(BufferedImage src) {
		if (src == null)
			return null;


		int imWidth = src.getWidth();
		int imHeight = src.getHeight();

		BufferedImage copy = new BufferedImage (imWidth, imHeight,
							BufferedImage.TYPE_INT_ARGB);

    		Graphics2D g2d = copy.createGraphics();

    		// copy image
    		g2d.drawImage(src, 0, 0, null);
    		g2d.dispose();

    		return copy; 
	  }

}


import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

public class Player {			

   private static final int DX = 36;	// amount of X pixels to move in one keystroke
   private static final int DY = 64;	// amount of Y pixels to move in one keystroke
   private static final int TILE_SIZE = 64;

   private JPanel panel;		// reference to the JFrame on which player is drawn
   private TileMap tileMap;
   private BackgroundManager bgManager;

   private int x;			// x-position of player's sprite
   private int y;			// y-position of player's sprite

   private int width;			// scaled width of player
   private int height;			// scaled height of player
   Graphics2D g2;
   private Dimension dimension;

   private Image playerImage, playerLeftImage, playerRightImage;

   private boolean jumping;
   private int timeElapsed;
   private int startY;

   private boolean goingUp;
   private boolean goingDown;

   private boolean inAir;
   private boolean wasOnGround;
   private int initialVelocity;
   
   private boolean isClimbing = false;
   private int climbingSide = 0; // -1 for Left, 1 for Right
   private int awayFrames = 0;
   private int startAir;

   public Player (JPanel panel, TileMap t, BackgroundManager b) {
      this.panel = panel;

      tileMap = t;			// tile map on which the player's sprite is displayed
      bgManager = b;			// instance of BackgroundManager

      goingUp = goingDown = false;
      inAir = false;
      wasOnGround = true;

      playerLeftImage = ImageManager.loadImage("images/playerLeft.gif");
      playerRightImage = ImageManager.loadImage("images/playerRight.gif");
      playerImage = playerRightImage;
      
      this.width = (int)(playerImage.getWidth(null) * 1.5);
      this.height = (int)(playerImage.getHeight(null) * 1.5);
   }

   public boolean isClimbing() { return isClimbing; }
   public int getClimbingSide() { return climbingSide; }

   /**
    * Sensor Box: Checks a 2-pixel wide strip just outside the player's bounds.
    */
   private boolean isTouchingWall(int side) {
      int sensorX = (side == -1) ? x - 5 : x + width + 3;
      // Check top, middle, and bottom of the player's side
      return tileMap.isPixelSolid(sensorX, y + 5) || 
             tileMap.isPixelSolid(sensorX, y + height / 2) || 
             tileMap.isPixelSolid(sensorX, y + height - 5);
   }

   /**
    * Ledge Sensor: Checks if the side sensor is clear at head-height but 
    * solid at feet-height (detecting a ledge).
    */
   private boolean isAtLedge(int side) {
      int sensorX = (side == -1) ? x - 2 : x + width;
      // Head is clear, but feet/middle are still against a wall
      boolean headClear = !tileMap.isPixelSolid(sensorX, y);
      boolean bodySolid = tileMap.isPixelSolid(sensorX, y + height / 2) || 
                          tileMap.isPixelSolid(sensorX, y + height - 10);
      return headClear && bodySolid;
   }


   public Point collidesWithTile(int newX, int newY) {

      // Check multiple points along the player's height, but skip the bottom 
      // section (feet) so the player can enter slope tiles horizontally.
      int checkHeightLimit = height - 32; 
      for (int i = 0; i < checkHeightLimit; i += 10) {
         if (tileMap.isPixelSolid(newX, newY + i)) {
            int tx = tileMap.pixelsToTiles(newX);
            int ty = tileMap.pixelsToTiles(newY + i);
            int type = tileMap.getTileType(tx, ty);
            // Bypass AABB: ignore horizontal collision for slope types
            if (type >= TileMap.TILE_RISING_LOW && type <= TileMap.TILE_FALLING_LOW) continue;
            return new Point(tx, ty);
         }
      }
      // Final check just below the "step" height
      if (tileMap.isPixelSolid(newX, newY + checkHeightLimit)) {
         return new Point(tileMap.pixelsToTiles(newX), tileMap.pixelsToTiles(newY + checkHeightLimit));
      }
      return null;
   }

   public float getSlopeY(int playerX, int tileX, int tileY, int tileType) {
      float localX = (float)playerX - (tileX * TILE_SIZE);
      float floorY = 0;
      if (tileType == TileMap.TILE_RISING_LOW) {
         floorY = 64 - (localX / 2.0f);
      } else if (tileType == TileMap.TILE_RISING_HIGH) {
         floorY = 32 - (localX / 2.0f);
      } else if (tileType == TileMap.TILE_FALLING_HIGH) {
         floorY = localX / 2.0f;
      } else if (tileType == TileMap.TILE_FALLING_LOW) {
         floorY = 32 + (localX / 2.0f);
      }
      return (float)(tileY * TILE_SIZE) + floorY;
   }


   public Point collidesWithTileDown (int newX, int newY) {

	  int playerWidth = width;
      int playerHeight = height;
      
      // Check three points at the bottom: left corner, center, and right corner
      if (tileMap.isPixelSolid(newX, newY + playerHeight)) {
          return new Point(tileMap.pixelsToTiles(newX), tileMap.pixelsToTiles(newY + playerHeight));
      }
      if (tileMap.isPixelSolid(newX + playerWidth / 2, newY + playerHeight)) {
          return new Point(tileMap.pixelsToTiles(newX + playerWidth / 2), tileMap.pixelsToTiles(newY + playerHeight));
      }
      if (tileMap.isPixelSolid(newX + playerWidth - 1, newY + playerHeight)) {
          return new Point(tileMap.pixelsToTiles(newX + playerWidth - 1), tileMap.pixelsToTiles(newY + playerHeight));
      }
      return null;
   }


   public Point collidesWithTileUp (int newX, int newY) {

	  int playerWidth = width;

	  int xTile = tileMap.pixelsToTiles(newX);

	  int yTileFrom = tileMap.pixelsToTiles(y);
	  int yTileTo = tileMap.pixelsToTiles(newY);
	 
	  for (int yTile=yTileFrom; yTile>=yTileTo; yTile--) {
		if (tileMap.getTile(xTile, yTile) != null) {
	        	Point tilePos = new Point (xTile, yTile);
	  		return tilePos;
		}
		else {
			if (tileMap.getTile(xTile+1, yTile) != null) {
				int leftSide = (xTile + 1) * TILE_SIZE;
				if (newX + playerWidth > leftSide) {
				    Point tilePos = new Point (xTile+1, yTile);
				    return tilePos;
			        }
			}
		}
				    
	  }

	  return null;
   }
 
/*

   public Point collidesWithTile(int newX, int newY) {

	 int playerWidth = playerImage.getWidth(null);
	 int playerHeight = playerImage.getHeight(null);

      	 int fromX = Math.min (x, newX);
	 int fromY = Math.min (y, newY);
	 int toX = Math.max (x, newX);
	 int toY = Math.max (y, newY);

	 int fromTileX = tileMap.pixelsToTiles (fromX);
	 int fromTileY = tileMap.pixelsToTiles (fromY);
	 int toTileX = tileMap.pixelsToTiles (toX + playerWidth - 1);
	 int toTileY = tileMap.pixelsToTiles (toY + playerHeight - 1);

	 for (int x=fromTileX; x<=toTileX; x++) {
		for (int y=fromTileY; y<=toTileY; y++) {
			if (tileMap.getTile(x, y) != null) {
				Point tilePos = new Point (x, y);
				return tilePos;
			}
		}
	 }
	
	 return null;
   }
*/


   public synchronized void move (int direction) {

      if (!panel.isVisible ()) return;

      // Release wall if moving away
      if (isClimbing && direction != 3) {
         if ((climbingSide == -1 && direction == 2) || (climbingSide == 1 && direction == 1)) {
            awayFrames++;
            if (awayFrames > 5) {
               isClimbing = false;
               awayFrames = 0;
            }
         } else {
            awayFrames = 0;
         }
         return; // Disable normal horizontal movement while climbing
      }
      
      if (direction == 1) {		// move left
          playerImage = playerLeftImage;
          int newX = x - DX;
	  if (newX < 0) {
		x = 0;
		return;
	  }

      Point tilePos = collidesWithTile(newX, y);
      int tileType = (tilePos != null) ? tileMap.getTileType((int)tilePos.getX(), (int)tilePos.getY()) : TileMap.TILE_EMPTY;
      if (tilePos != null && tileType == TileMap.TILE_SOLID) {
         x = ((int) tilePos.getX() + 1) * TILE_SIZE;
      } else {
         x = newX;
         bgManager.moveLeft();
      }
      }
      else if (direction == 2) {		// move right
          playerImage = playerRightImage;
      	  int playerWidth = width;
          int newX = x + DX;
         int tileMapWidth = tileMap.getWidthPixels();

         if (newX + width >= tileMapWidth) {
            x = tileMapWidth - width;
            return;
         }

         Point tilePos = collidesWithTile(newX + playerWidth, y);
         int tileType = (tilePos != null) ? tileMap.getTileType((int)tilePos.getX(), (int)tilePos.getY()) : TileMap.TILE_EMPTY;
         if (tilePos != null && tileType == TileMap.TILE_SOLID) {
            x = ((int) tilePos.getX()) * TILE_SIZE - playerWidth;
         } else {
            x = newX;
            bgManager.moveRight();
         }
      }
      else if (direction == 3 && !jumping) {	
          jump();
          return;
      }
   }


   public boolean isInAir() {
      if (!jumping && !inAir && !isClimbing) {   
          // Player is NOT in air if any point under their feet (left, center, right) is solid
          boolean onGround = tileMap.isPixelSolid(x, y + height + 1) || 
                            tileMap.isPixelSolid(x + width / 2, y + height + 1) || 
                            tileMap.isPixelSolid(x + width - 1, y + height + 1);
          
          return !onGround;
      }
      return false;
   }


   private void fall() {

      jumping = false;
      inAir = true;
      timeElapsed = 0;

      goingUp = false;
      goingDown = true;

      startY = y;
      initialVelocity = 0;
   }


   public void jump () {  

      if (!panel.isVisible ()) return;

      jumping = true;
      timeElapsed = 0;
      startY = y;

      if (isClimbing) {
         // Jump off wall (apply kickback)
         isClimbing = false;
         x += (climbingSide == -1) ? 20 : -20; 
         initialVelocity = 180;
      } else {
         initialVelocity = 120;
      }

      goingUp = true;
      goingDown = false;
   }


   public void update () {

      // Entrance check: Trigger if holding Up while against a wall (checked every frame)
      if (!isClimbing && GameWindow.isKeyPressed(KeyEvent.VK_UP)) {
         if (isTouchingWall(-1)) {
            isClimbing = true;
            climbingSide = -1;
            playerImage = playerLeftImage;
            jumping = inAir = goingUp = goingDown = false;
         } else if (isTouchingWall(1)) {
            isClimbing = true;
            climbingSide = 1;
            playerImage = playerRightImage;
            jumping = inAir = goingUp = goingDown = false;
         }
      }

      if (isClimbing) {
         int climbSpeed = 8;
         if (GameWindow.isKeyPressed(KeyEvent.VK_UP)) {
            y -= climbSpeed;
            
            // Mantle / Ledge Move
            if (isAtLedge(climbingSide)) {
               y -= 32; // Lift up
               x += (climbingSide == -1) ? -20 : 20; // Push onto surface
               isClimbing = false;
            }
         }
         if (GameWindow.isKeyPressed(KeyEvent.VK_DOWN)) {
            y += climbSpeed;
            // Fall off if we hit the floor
            if (tileMap.isPixelSolid(x + width/2, y + height + 1)) {
               isClimbing = false;
            }
         }

         // Safety check: if wall disappears, stop climbing
         if (!isTouchingWall(climbingSide)) {
            isClimbing = false;
         }
         return; // Bypass normal physics
      }

      int distance = 0;
      int newY = 0;

      // SLOPE HANDLING & SNAPPING (Moved from move() to update() to ensure constant physics)
      int centerX = x + width / 2;
      int footY = y + height;
      int searchRange = (wasOnGround && !jumping) ? 16 : 0;
      boolean onSlope = false;

      for (int i = 0; i <= searchRange; i++) {
          int tx = tileMap.pixelsToTiles(centerX);
          int ty = tileMap.pixelsToTiles(footY + i - 1);
          int type = tileMap.getTileType(tx, ty);

          if (type >= TileMap.TILE_RISING_LOW && type <= TileMap.TILE_FALLING_LOW) {
              float surfaceY = getSlopeY(centerX, tx, ty, type);
              if (footY + i >= surfaceY) {
                  y = (int) (surfaceY - height);
                  jumping = false;
                  inAir = false;
                  goingUp = goingDown = false;
                  timeElapsed = 0; 
                  onSlope = true;
                  break;
              }
          }
      }

      if (!onSlope && isInAir()) {
          fall();
      }
      wasOnGround = !isInAir() || onSlope;

      timeElapsed++;

      if (jumping || inAir) {
	   distance = (int) (initialVelocity * timeElapsed - 
                             9.8 * timeElapsed * timeElapsed);
	   newY = startY - distance;

	   if (newY > y && goingUp) {
		goingUp = false;
 	  	goingDown = true;
	   }

	   if (goingUp) {
		Point tilePos = collidesWithTileUp (x, newY);	
	   	if (tilePos != null) {				// hits a tile going up
		   	System.out.println ("Jumping: Collision Going Up!");

			int topTileY = ((int) tilePos.getY()) * TILE_SIZE;
			int bottomTileY = topTileY + TILE_SIZE;

		   	y = bottomTileY;
		   	fall();
		}
	   	else {
			y = newY;
			System.out.println ("Jumping: No collision.");

			// the following if-statement is to pause the jump to capture the screen

/*
			if (x > 1608 && y < 300) {
				try {
					Thread.sleep (1000);
				}
				catch (Exception e) {
					System.out.println ("ERROR! " + e);
				}
			}
*/
	   	}
            }
	    else
	    if (goingDown) {			
            boolean hitGround = false;
            // Sweep collision check: iterate from current y to newY to catch intermediate platforms
            for (int tempY = y; tempY <= newY; tempY++) {
                Point tilePos = collidesWithTileDown(x, tempY);
                if (tilePos != null) {
                    int playerHeight = height;
                    
                    // Fine-tune Y position: step up pixel-by-pixel until not colliding
                    int adjustedY = tempY;
                    while (tileMap.isPixelSolid(x, adjustedY + playerHeight) || 
                           tileMap.isPixelSolid(x + width / 2, adjustedY + playerHeight) || 
                           tileMap.isPixelSolid(x + width - 1, adjustedY + playerHeight)) {
                        adjustedY--;
                    }

                    y = adjustedY;
                    goingDown = false;
                    jumping = false;
                    inAir = false;
                    hitGround = true;
                    break;
                }
            }
            if (!hitGround) {
                y = newY;
            }
        }
      }
   }


   public void moveUp () {

      if (!panel.isVisible ()) return;

      y = y - DY;
   }


   public int getX() {
      return x;
   }


   public void setX(int x) {
      this.x = x;
   }


   public int getY() {
      return y;
   }


   public void setY(int y) {
      this.y = y;
   }


   public int getWidth() {
      return width;
   }

   public int getHeight() {
      return height;
   }

   public Image getImage() {
      return playerImage;
   }


   public Rectangle2D.Double getBoundingRectangle() {
      int playerWidth = width;
      int playerHeight = height;

      return new Rectangle2D.Double (x, y, playerWidth, playerHeight);
   }

}


import javax.sound.sampled.AudioInputStream;		// for playing sound clips
import javax.sound.sampled.*;
import java.io.*;

import java.util.HashMap;				// for storing sound clips


public class SoundManager {				// a Singleton class
	HashMap<String, Clip> clips;

   	Clip hitClip = null;				// played when bat hits ball
   	Clip appearClip = null;				// played when ball is re-generated
   	Clip backgroundClip = null;			// played continuously after ball is created

	private static SoundManager instance = null;	// keeps track of Singleton instance

	private SoundManager () {
		Clip clip;
		clips = new HashMap<String, Clip>();
		
		//Clip clip = loadClip("sounds/background.wav");
		//clips.put("background", clip);		// background theme sound

		clip = loadClip("sounds/hitSound.wav");
		clips.put("hit", clip);			// played when player's sprite collides 
							//   with another sprice

		clip = loadClip("sounds/appearSound.wav");
		clips.put("appear", clip);		// played when a special sprite 
							//   makes an appearance

		clip = loadClip("sounds/BirdSound.wav");
		clips.put("birdSound", clip);		// played for bird-flying animation
	}


	public static SoundManager getInstance() {	// class method to get Singleton instance
		if (instance == null)
			instance = new SoundManager();
		
		return instance;
	}		


	public Clip getClip (String title) {

		return clips.get(title);		// gets a sound by supplying key
	}


    	public Clip loadClip (String fileName) {	// gets clip from the specified file
 		AudioInputStream audioIn;
		Clip clip = null;

		try {
    			File file = new File(fileName);
    			audioIn = AudioSystem.getAudioInputStream(file.toURI().toURL()); 
    			clip = AudioSystem.getClip();
    			clip.open(audioIn);
		}
		catch (Exception e) {
 			System.out.println ("Error opening sound files: " + e);
		}
    		return clip;
    	}


    	public void playSound(String title, Boolean looping) {
		Clip clip = getClip(title);
		if (clip != null) {
			clip.setFramePosition(0);
			if (looping)
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			else
				clip.start();
		}
    	}


    	public void stopSound(String title) {
		Clip clip = getClip(title);
		if (clip != null) {
			clip.stop();
		}
    	}

}


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;

/**
    The TileMap class contains the data for a tile-based
    map, including Sprites. Each tile is a reference to an
    Image. Images are used multiple times in the tile map.
    map.
*/

public class TileMap {

    public static final int TILE_SIZE = 64;
    private static final int TILE_SIZE_BITS = 6;

    // Tile Type Constants
    public static final int TILE_EMPTY = 0;
    public static final int TILE_SOLID = 1;
    public static final int TILE_SLOPE_UP = 2;
    public static final int TILE_SLOPE_DOWN = 3;
    public static final int TILE_RISING_LOW = 4;
    public static final int TILE_RISING_HIGH = 5;
    public static final int TILE_FALLING_HIGH = 6;
    public static final int TILE_FALLING_LOW = 7;

    private Image[][] tiles;
    private int[][] tileTypes;
    private int screenWidth, screenHeight;
    private int mapWidth, mapHeight;
    private int offsetY;

    private LinkedList sprites;
    private Image skyBackground;
    private Image cityBackground;
    private Player player;
    private Heart heart;

    BackgroundManager bgManager;

    private GamePanel panel;
    private Dimension dimension;

    /**
        Creates a new TileMap with the specified width and
        height (in number of tiles) of the map.
    */
    public TileMap(GamePanel panel, int width, int height) {

	this.panel = panel;
	dimension = panel.getSize();

	screenWidth = dimension.width;
	screenHeight = dimension.height;

	System.out.println ("Width: " + screenWidth);
	System.out.println ("Height: " + screenHeight);

	mapWidth = width;
	mapHeight = height;

        // get the y offset to draw all sprites and tiles
        tileTypes = new int[mapWidth][mapHeight];

       	offsetY = 0;
	System.out.println("offsetY: " + offsetY);

	bgManager = new BackgroundManager (panel, 12);

        skyBackground = ImageManager.loadImage("images/sky11d.png");
        cityBackground = ImageManager.loadImage("images/city_background_clean.png");

        tiles = new Image[mapWidth][mapHeight];
	player = new Player (panel, this, bgManager);
	heart = new Heart (panel, player);
		
        sprites = new LinkedList();

	int x, y;
        // Adjusted starting coordinates to suit the 32x32 grid
	x = 22368;
	y = 165;

	//x = 1000;					// position player in 'random' location

        player.setX(x);
        player.setY(y);

	System.out.println("Player coordinates: " + x + "," + y);

    }


    /**
        Gets the width of this TileMap (number of pixels across).
    */
    public int getWidthPixels() {
	return tilesToPixels(mapWidth);
    }

    /**
        Gets the height of this TileMap (number of pixels down).
    */
    public int getHeightPixels() {
        return tilesToPixels(mapHeight);
    }


    /**
        Gets the width of this TileMap (number of tiles across).
    */
    public int getWidth() {
        return mapWidth;
    }


    /**
        Gets the height of this TileMap (number of tiles down).
    */
    public int getHeight() {
        return mapHeight;
    }


    public int getOffsetY() {
	return offsetY;
    }

    /**
        Gets the tile at the specified location. Returns null if
        no tile is at the location or if the location is out of
        bounds.
    */
    public Image getTile(int x, int y) {
        if (x < 0 || x >= mapWidth ||
            y < 0 || y >= mapHeight)
        {
            return null;
        }
        else {
            return tiles[x][y];
        }
    }


    /**
        Sets the tile at the specified location.
    */
    public void setTile(int x, int y, Image tile) {
        tiles[x][y] = tile;
    }

    /**
        Gets the type of the tile at the specified location.
    */
    public int getTileType(int x, int y) {
        if (x < 0 || x >= mapWidth || y < 0 || y >= mapHeight) return TILE_EMPTY;
        return tileTypes[x][y];
    }

    /**
        Sets the type of the tile at the specified location.
    */
    public void setTileType(int x, int y, int type) {
        tileTypes[x][y] = type;
    }

    /**
        Checks if a specific pixel coordinate in the map is solid.
    */
    public boolean isPixelSolid(int pixelX, int pixelY) {
        int tileX = pixelsToTiles(pixelX);
        int tileY = pixelsToTiles(pixelY);
        
        Image tile = getTile(tileX, tileY);
        if (tile instanceof BufferedImage) {
            BufferedImage bi = (BufferedImage) tile;
            int localX = pixelX % TILE_SIZE;
            int localY = pixelY % TILE_SIZE;
            
            if (localX >= 0 && localX < bi.getWidth() && localY >= 0 && localY < bi.getHeight()) {
                int color = bi.getRGB(localX, localY);
                // Check alpha (transparency). 0 is fully transparent.
                return ((color >> 24) & 0xff) > 0;
            }
        }
        return tile != null; // Fallback if image isn't a BufferedImage
    }

    /**
        Gets an Iterator of all the Sprites in this map,
        excluding the player Sprite.
    */

    public Iterator getSprites() {
        return sprites.iterator();
    }

    /**
        Class method to convert a pixel position to a tile position.
    */

    public static int pixelsToTiles(float pixels) {
        return pixelsToTiles(Math.round(pixels));
    }


    /**
        Class method to convert a pixel position to a tile position.
    */

    public static int pixelsToTiles(int pixels) {
        return (int)Math.floor((float)pixels / TILE_SIZE);
    }


    /**
        Class method to convert a tile position to a pixel position.
    */

    public static int tilesToPixels(int numTiles) {
        return numTiles * TILE_SIZE;
    }

    /**
        Draws the specified TileMap.
    */
    public void draw(Graphics2D g2)
    {
        double zoom = 0.8; // Zoom out factor (0.8 = 80% scale, or 20% zoom out)
        AffineTransform oldTransform = g2.getTransform();

        // Determine virtual screen size based on zoom level for centering and culling
        Rectangle bounds = g2.getClipBounds();
        int virtualWidth = (bounds != null) ? (int)(bounds.width / zoom) : screenWidth;
        int virtualHeight = (bounds != null) ? (int)(bounds.height / zoom) : screenHeight;

        int mapWidthPixels = tilesToPixels(mapWidth);
        int mapHeightPixels = tilesToPixels(mapHeight);

        // get the scrolling position of the map
        // based on player's position

        int offsetX = virtualWidth / 2 -
            (int)Math.round((double)player.getX()) - (TILE_SIZE / 2);
        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, virtualWidth - mapWidthPixels);

        // Calculate dynamic offsetY to keep player centered vertically
        int offsetY = (int)(virtualHeight * 0.75) - (int)Math.round((double)player.getY()) - (TILE_SIZE / 2);
        // Vertical clamping removed to allow the camera to follow the player at unlimited height and depth.
        this.offsetY = offsetY; // Update the field for collision logic

        // Apply the zoom scale to the graphics context
        g2.scale(zoom, zoom);

        // Clear the visible area to prevent "ghosting" or duplicated pixels 
        // when the camera moves outside the map boundaries.
        g2.setColor(Color.BLACK); 
        g2.fillRect(0, 0, virtualWidth, virtualHeight);

	// draw the background first

        if (skyBackground != null || cityBackground != null) {
            if (skyBackground != null) {
                int skyOffsetX = (int) (offsetX * 0.2); 
                g2.drawImage(skyBackground, skyOffsetX, offsetY, getWidthPixels(), getHeightPixels(), null);
            }
            if (cityBackground != null) {
                int cityOffsetX = (int) (offsetX * 0.5); 
                g2.drawImage(cityBackground, cityOffsetX, offsetY, getWidthPixels(), getHeightPixels(), null);
            }
        } else {
            bgManager.draw (g2);
        }


	//Draw white background (for screen capture)
/*
	g2.setColor (Color.WHITE);
	g2.fill (new Rectangle2D.Double (0, 0, 600, 500));
*/
        // draw the visible tiles

        int firstTileX = Math.max(0, pixelsToTiles(-offsetX) - 1);
        int lastTileX = Math.min(mapWidth - 1, pixelsToTiles(-offsetX + virtualWidth) + 6);

        int firstTileY = Math.max(0, pixelsToTiles(-offsetY) - 3);
        int lastTileY = Math.min(mapHeight - 1, pixelsToTiles(-offsetY + virtualHeight) + 6);

        for (int y=firstTileY; y <= lastTileY; y++) {
            for (int x=firstTileX; x <= lastTileX; x++) {
                Image image = getTile(x, y);
                if (image != null) {
                    g2.drawImage(image,
                        tilesToPixels(x) + offsetX,
                        tilesToPixels(y) + offsetY,
                        TILE_SIZE,
                        TILE_SIZE,
                        null);
                }
            }
        }


        // draw player
        
        int pX = (int)Math.round((double)player.getX()) + offsetX;
        int pY = (int)Math.round((double)player.getY()) + offsetY;
        int pW = player.getWidth();
        int pH = player.getHeight();

        if (player.isClimbing()) {
            // Save current transform
            AffineTransform old = g2.getTransform();
            
            // Rotate 90 degrees toward the wall around the player's center
            double rotation = (player.getClimbingSide() == -1) ? Math.toRadians(90) : Math.toRadians(-90);
            g2.rotate(rotation, pX + pW / 2, pY + pH / 2);
            
            g2.drawImage(player.getImage(), pX, pY, pW, pH, null);
            
            // Restore transform
            g2.setTransform(old);
        } else {
            g2.drawImage(player.getImage(), pX, pY, pW, pH, null);
        }

	// draw Heart sprite

        g2.drawImage(heart.getImage(),
            (int)Math.round((double)heart.getX()) + offsetX,
            (int)Math.round((double)heart.getY()) + offsetY, 40, 40,
            null);

        // Restore original transform to avoid affecting UI or subsequent draw calls
        g2.setTransform(oldTransform);

/*
        // draw sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite)i.next();
            int x = Math.round(sprite.getX()) + offsetX;
            int y = Math.round(sprite.getY()) + offsetY;
            g.drawImage(sprite.getImage(), x, y, null);

            // wake up the creature when it's on screen
            if (sprite instanceof Creature &&
                x >= 0 && x < screenWidth)
            {
                ((Creature)sprite).wakeUp();
            }
        }
*/

    }


    public void moveLeft() {
	int x, y;
	x = player.getX();
	y = player.getY();

	String mess = "Going left. x = " + x + " y = " + y;
	System.out.println(mess);

	player.move(1);

    }


    public void moveRight() {
	int x, y;
	x = player.getX();
	y = player.getY();

	String mess = "Going right. x = " + x + " y = " + y;
	System.out.println(mess);

	player.move(2);

    }


    public void jump() {
	int x, y;
	x = player.getX();
	y = player.getY();

	String mess = "Jumping. x = " + x + " y = " + y;
	System.out.println(mess);

	player.move(3);

    }


    public void update() {
	player.update();

	if (heart.collidesWithPlayer()) {
		panel.endLevel();
		return;
	}

	heart.update();

	if (heart.collidesWithPlayer()) {
		panel.endLevel();
	}

    }

}


import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;


/**
    The TileMapeManager class loads and manages tile Images and
    "host" Sprites used in the game. Game Sprites are cloned from
    "host" Sprites.
*/
public class TileMapManager {

    private ArrayList<BufferedImage> tiles;
    private int currentMap = 0;

    private GamePanel panel;

/*

    // host sprites used for cloning
    private Sprite playerSprite;
    private Sprite musicSprite;
    private Sprite coinSprite;
    private Sprite goalSprite;
    private Sprite grubSprite;
    private Sprite flySprite;
*/

    public TileMapManager(GamePanel panel) {
	this.panel = panel;

        loadTileImages();

        //loadCreatureSprites();
        //loadPowerUpSprites();
    }


     public TileMap loadMap(String filename)
        throws IOException
    {
        ArrayList<String[]> tileData = new ArrayList<>();
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("TILE")) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    tileData.add(parts);
                    int tx = Integer.parseInt(parts[1]);
                    int ty = Integer.parseInt(parts[2]);
                    
                    minX = Math.min(minX, tx);
                    maxX = Math.max(maxX, tx);
                    minY = Math.min(minY, ty);
                    maxY = Math.max(maxY, ty);
                }
            } else if (line.startsWith("BACKGROUND")) {
                // Background info from Godot can be logged or used for initialization
                System.out.println("Map Background Info: " + line);
            }
        }
        reader.close();

        if (tileData.isEmpty()) {
            System.out.println("No tile data found in " + filename);
            return new TileMap(panel, 1, 1);
        }

        int mapWidth = maxX - minX + 1;
        int mapHeight = maxY - minY + 1;

        TileMap newMap = new TileMap(panel, mapWidth, mapHeight);

        // Constant to map Godot Atlas (x,y) coordinates to a linear index for tiles A, B, C...
        // If your tiles look scrambled, change this number to match the 
        // number of COLUMNS in your Godot Tileset Atlas setup.
        // For example, if your atlas is a 10x6 grid, change this to 10.
        // If all your tiles are in one long row, change this to 64.
        final int ATLAS_WIDTH = 8; 

        for (String[] parts : tileData) {
            // Normalize coordinates to be 0-indexed for the Java TileMap array
            int x = Integer.parseInt(parts[1]) - minX;
            int y = Integer.parseInt(parts[2]) - minY;
            
            int atlasX = Integer.parseInt(parts[3]);
            int atlasY = Integer.parseInt(parts[4]);

            // Calculate tile index (0='A', 1='B', etc.) based on atlas grid
            int tileIndex = (atlasY * ATLAS_WIDTH) + atlasX;

            if (tileIndex >= 0 && tileIndex < tiles.size()) {
                newMap.setTile(x, y, tiles.get(tileIndex));
                
                // ASSIGN TILE TYPES BASED ON IMAGE INDEX
                // Adjust these numbers based on your actual Tile IDs (0-indexed)
                if (tileIndex == 43) { 
                    newMap.setTileType(x, y, TileMap.TILE_RISING_LOW);
                } else if (tileIndex == 33) { 
                    newMap.setTileType(x, y, TileMap.TILE_RISING_HIGH);
                } else if (tileIndex == 34) { 
                    newMap.setTileType(x, y, TileMap.TILE_FALLING_HIGH);
                } else if (tileIndex == 44) { 
                    newMap.setTileType(x, y, TileMap.TILE_FALLING_LOW);
                } else {
                    newMap.setTileType(x, y, TileMap.TILE_SOLID);
                }
            }
        }

        return newMap;
    }


/*
    private void addSprite(TileMap map,
        Sprite hostSprite, int tileX, int tileY)
    {
        if (hostSprite != null) {
            // clone the sprite from the "host"
            Sprite sprite = (Sprite)hostSprite.clone();

            // center the sprite
            sprite.setX(
                TileMapRenderer.tilesToPixels(tileX) +
                (TileMapRenderer.tilesToPixels(1) -
                sprite.getWidth()) / 2);

            // bottom-justify the sprite
            sprite.setY(
                TileMapRenderer.tilesToPixels(tileY + 1) -
                sprite.getHeight());

            // add it to the map
            map.addSprite(sprite);
        }
    }

*/

    // -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------


    public void loadTileImages() {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ folder

	File file;

	System.out.println("loadTileImages called.");

        tiles = new ArrayList<BufferedImage>();
        int i = 1;
        while (true) {
            String filename = String.format("images/1 Tiles/Tile_%02d.png", i);
	    file = new File(filename);
            if (!file.exists()) {
                break;
            }
	    else
		System.out.println("Image file opened: " + filename);
		BufferedImage tileImage = ImageManager.loadBufferedImage(filename);
		tiles.add(tileImage);
            i++;
        }
    }

/*
    public void loadCreatureSprites() {

        Image[][] images = new Image[4][];

        // load left-facing images
        images[0] = new Image[] {
            loadImage("player1.png"),
            loadImage("player2.png"),
            loadImage("player3.png"),
            loadImage("fly1.png"),
            loadImage("fly2.png"),
            loadImage("fly3.png"),
            loadImage("grub1.png"),
            loadImage("grub2.png"),
        };

        images[1] = new Image[images[0].length];
        images[2] = new Image[images[0].length];
        images[3] = new Image[images[0].length];
        for (int i=0; i<images[0].length; i++) {
            // right-facing images
            images[1][i] = getMirrorImage(images[0][i]);
            // left-facing "dead" images
            images[2][i] = getFlippedImage(images[0][i]);
            // right-facing "dead" images
            images[3][i] = getFlippedImage(images[1][i]);
        }

        // create creature animations
        Animation[] playerAnim = new Animation[4];
        Animation[] flyAnim = new Animation[4];
        Animation[] grubAnim = new Animation[4];
        for (int i=0; i<4; i++) {
            playerAnim[i] = createPlayerAnim(
                images[i][0], images[i][1], images[i][2]);
            flyAnim[i] = createFlyAnim(
                images[i][3], images[i][4], images[i][5]);
            grubAnim[i] = createGrubAnim(
                images[i][6], images[i][7]);
        }

        // create creature sprites
        playerSprite = new Player(playerAnim[0], playerAnim[1],
            playerAnim[2], playerAnim[3]);
        flySprite = new Fly(flyAnim[0], flyAnim[1],
            flyAnim[2], flyAnim[3]);
        grubSprite = new Grub(grubAnim[0], grubAnim[1],
            grubAnim[2], grubAnim[3]);
System.out.println("loadCreatureSprites successfully executed.");

    }
*/

}