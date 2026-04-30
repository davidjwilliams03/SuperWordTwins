import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
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
	private Image endImage;

	private ArrayList<String> riddle;
    private ArrayList<String> ans;

	private int clueIndex;
	private String clue = "";

	private Random random;

	public GamePanel () {

		isRunning = false;
		isPaused = false;
		isAnimShown = false;
		isAnimPaused = false;
		random = new Random();

		riddle = new ArrayList<>();
        ans = new ArrayList<>();

		soundManager = SoundManager.getInstance();

		// Initialize buffer to match preferred size (1300x700)
		image = new BufferedImage (1300, 700, BufferedImage.TYPE_INT_RGB);

		level = 1;
		levelChange = false;

	}

    public void loadRiddles() {
        BufferedReader br = null;
        String line = "";

        try {
            br = new BufferedReader(new FileReader("riddles.csv"));
            while((line = br.readLine()) != null) {
                String[] row = line.split(",");
                riddle.add(row[0].trim().toUpperCase());
                ans.add(row[1].trim().toUpperCase());
                
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
         /*for(int i = 0; i < riddle.size(); i++){
                System.out.println("Riddle: " + riddle.get(i));
                System.out.println("Answer: " + ans.get(i));

            }*/
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

	public String chooseClue(){
		if(riddle != null){
			clueIndex = random.nextInt(riddle.size());
			clue = riddle.get(clueIndex);
		}

		System.out.println("Clue:" + clue);

		return clue;
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

	public int getCoinsCollected(){
		if (tileMap == null) {
        	return 0; 
    	}
		return tileMap.getCoinsCollected();
	}


	public void endLevel() {
		level = level + 1;
		levelChange = true;
	}

}