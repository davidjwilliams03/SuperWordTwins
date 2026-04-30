import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JPanel;

/**
   A component that displays all the game entities
*/

public class GamePanel extends JPanel
		       implements Runnable {

	private SoundManager soundManager;

	private volatile boolean isRunning;
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
	private boolean gameOverPending;
	private Image endImage;

	private ArrayList<String> riddle;
	private ArrayList<String> ans;

	private int clueIndex;
	private String clue = "";
	private char guessedChar;
	private boolean wordComplete;
	private int numGuessesCorrect;

	private GameWindow window;

	private Random random;


	public GamePanel () {

		isRunning = false;
		isPaused = false;
		isAnimShown = false;
		isAnimPaused = false;
		random = new Random();

		riddle = new ArrayList<>();
        ans = new ArrayList<>();
		gameOverPending = false;

		soundManager = SoundManager.getInstance();

		// Initialize buffer to match preferred size (1300x700)
		image = new BufferedImage (1300, 700, BufferedImage.TYPE_INT_RGB);

		level = 1;
		levelChange = false;

		numGuessesCorrect = 0;
		wordComplete = false;

		loadRiddles();
	}

	public void setWindow(GameWindow window) {
		this.window = window;
	}

	public void loadRiddles() {
		BufferedReader br = null;
    	String line = "";

	    try {
    	    br = new BufferedReader(new FileReader("riddles.csv"));
    	    while ((line = br.readLine()) != null) {
    	        String[] row = line.split(",");
    	        riddle.add(row[0].trim().toUpperCase());
    	        ans.add(row[1].trim().toUpperCase());
    	    }
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
	}

	public String chooseClue() {
		if (riddle != null && !riddle.isEmpty()) {
			clueIndex = random.nextInt(riddle.size());
			clue = riddle.get(clueIndex);
		}
		return clue;
	}

	public String getAnswer() {
		return ans.get(clueIndex);
	}

	public void correctGuess(char c){
		guessedChar = c;
		numGuessesCorrect += window.updateAns(c);
		if(numGuessesCorrect == getAnswer().length()){
			wordComplete = true;
		}
	}

	public char getCorrectGuessChar(){
		return guessedChar;
	}

	public int numCharAns(){
		return getAnswer().length();
	}

	public ArrayList<String> getRiddles(){
		return riddle;
	}
	public ArrayList<String> getAnswers(){
		return ans;
	}

	public int getCoinsCollected() {
    	if (tileMap == null) return 0;
    	return tileMap.getCoinsCollected();
}

	public void createGameEntities() {
		animation = new BirdAnimation();
		imageEffect = new ImageEffect (this);
	}


	public void run () {
		try {
			isRunning = true;
			while (isRunning) {
				if (!isPaused && !gameOver && !gameOverPending)
					gameUpdate();
				gameRender();
				Thread.sleep (50);	
			}
		}
		catch(InterruptedException e) {}
	}

	private void checkInput() {
		if (tileMap == null || gameOver || gameOverPending) return;

		if (GameWindow.isKeyPressed(KeyEvent.VK_LEFT)) moveLeft();
		if (GameWindow.isKeyPressed(KeyEvent.VK_RIGHT)) moveRight();
		if (GameWindow.isKeyPressed(KeyEvent.VK_SPACE)) jump();
	}

	public void gameUpdate() {

		checkInput();
		tileMap.update();

		// Update UI bars in GameWindow
		if (window != null && tileMap != null) {
			Player p = tileMap.getPlayer();
			window.updateLevel(level);
			window.updateHealth(p.getHealth());
			window.updateCoins(tileMap.getCoinsCollected());
			
			int mapWidth = tileMap.getWidthPixels();
			// Calculate percentage progress based on player X position
			int progress = (mapWidth > 0) ? (int)((p.getX() * 100.0) / mapWidth) : 0;
			window.updateProgress(Math.min(100, Math.max(0, progress)));
		}

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

		drawHUD(imageContext);

		if (gameOver) {
			// Apply grayscale effect to the frozen frame
			int w = image.getWidth();
			int h = image.getHeight();
			int[] pixels = new int[w * h];
			image.getRGB(0, 0, w, h, pixels, 0, w);

			for (int i = 0; i < pixels.length; i++) {
				int p = pixels[i];
				int a = (p >> 24) & 0xff;
				int r = (p >> 16) & 0xff;
				int g_comp = (p >> 8) & 0xff;
				int b = p & 0xff;
				int avg = (r + g_comp + b) / 3;
				pixels[i] = (a << 24) | (avg << 16) | (avg << 8) | avg;
			}
			image.setRGB(0, 0, w, h, pixels, 0, w);

			// Darken the screen slightly for contrast
			imageContext.setColor(new Color(0, 0, 0, 100));
			imageContext.fillRect(0, 0, w, h);

			// Draw Game Over Message
			imageContext.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			imageContext.setFont(new Font("Arial", Font.BOLD, 72));
			imageContext.setColor(Color.WHITE);
			String msg = "GAME OVER";
			imageContext.drawString(msg, (w - imageContext.getFontMetrics().stringWidth(msg)) / 2, h / 2);

			imageContext.setFont(new Font("Arial", Font.PLAIN, 24));
			String restartMsg = "Press 'Start New Game' to retry";
			imageContext.drawString(restartMsg, (w - imageContext.getFontMetrics().stringWidth(restartMsg)) / 2, h / 2 + 60);
		}

		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		imageContext.dispose();
	}

	private void drawHUD(Graphics2D g2) {
		if (tileMap == null) return;
		
		int health = tileMap.getPlayer().getHealth();
		int barWidth = 200;
		int barHeight = 20;
		int x = 20;
		int y = 20;

		// Background (Red)
		g2.setColor(Color.RED);
		g2.fillRect(x, y, barWidth, barHeight);

		// Current Health (Green)
		g2.setColor(Color.GREEN);
		int currentBarWidth = (int)((health / 100.0) * barWidth);
		g2.fillRect(x, y, currentBarWidth, barHeight);

		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 14));
		g2.drawString("HEALTH", x, y - 5);
	}

	/**
	 * Triggers the game over sequence with a slight delay so the 
	 * player can see the collision before the screen freezes.
	 */
	public void setGameOver() {
		if (!gameOver && !gameOverPending) {
			gameOverPending = true;
			
			// Start a timer to trigger the actual grey-out after 500ms
			new javax.swing.Timer(500, e -> {
				this.gameOver = true;
				this.gameOverPending = false;
				((javax.swing.Timer)e.getSource()).stop();
			}).start();
		}
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
		// Stop any existing game thread safely before starting a new one
		if (gameThread != null) {
			isRunning = false;
			try {
				gameThread.join(200); // Wait for the old thread to terminate
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			gameThread = null;
		}

		gameOver = false;
		level = 1;
		startGame(); // Use existing startGame logic to avoid duplication
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