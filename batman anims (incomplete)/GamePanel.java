import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
   A component that displays all the game entities
*/

public class GamePanel extends JPanel implements Runnable {
    private boolean isRunning;
	private boolean isPaused;

	private Thread gameThread;

	private BufferedImage image;
 	private Image backgroundImage;

    private boolean gameOver;

    private Batman batman;
	private boolean isMoving, left, right, jump, attack;

	//private Harley hq; 

    public GamePanel () {

		isRunning = false;
		isPaused = false;

        batman = null;
		//hq = null;

		image = new BufferedImage (1200, 1200, BufferedImage.TYPE_INT_RGB);
		backgroundImage = ImageManager.loadImage("images/bg.jpg");

    }

	public void setLeft(boolean bool){
		left = bool;
	}

	public void setRight(boolean bool){
		right = bool;
	}

	public void setJump(boolean bool){
		jump = bool;
	}

	public void setAttack(boolean bool){
		attack = bool;
	}

    public void createGameEntities() {
		batman = new Batman(this);
		//hq = new Harley();
	}


	public void run () {
		try {
			isRunning = true;
			while (isRunning) {
				if (!isPaused && !gameOver)
					batmanUpdate();
					gameUpdate();
				gameRender();
				Thread.sleep (50);	
			}
		}
		catch(InterruptedException e) {}
	}


	public void gameUpdate() {
		//hq.update();

	}


	public void batmanUpdate(){
		if(batman == null)
			return;		

		isMoving = false;
		boolean playing;
		if(batman.currAnim == batman.jump || batman.currAnim == batman.attack1 || batman.currAnim == batman.attack2)
			playing = true;
		else
			playing = false;


		if(playing && !batman.currAnim.isStillActive()){
			jump = false;
			attack = false;
		}
		
			if(left){
				batman.move(1);
				isMoving = true;
			}else {
				if(right){
				batman.move(2);
				isMoving = true;
				}
			}
		
			if(jump){
				batman.move(3);
				isMoving = true;
			}

			if(attack){
				batman.move(4);
				isMoving = true;
			}

			if(!isMoving){
				batman.setIdle();
			}
		//}
		//else {
		//	if(!batman.currAnim.isStillActive()){
		//		jump = false;
		//		attack = false;
		//		batman.setIdle();
			//}
		///}
		
		batman.update();
	}

	public void gameRender() {

		// draw the game objects on the image

		Graphics2D imageContext = (Graphics2D) image.getGraphics();

		imageContext.drawImage(backgroundImage, 0, 0, 1200, 1200, null);
        batman.draw(imageContext);
		//hq.draw(imageContext);

		if (gameOver) {
			Color darken = new Color (0, 0, 0, 125);
			imageContext.setColor (darken);
			imageContext.fill (new Rectangle2D.Double (0, 0, 1200, 1200));
		}

		Graphics2D g2 = (Graphics2D) getGraphics();	// get the graphics context for the panel
		g2.drawImage(image, 0, 0, 1200, 1200, null);	// draw the image on the graphics context

		imageContext.dispose();
	}


	public void startGame() {				// initialise and start the game thread 

		if (gameThread == null) {
			//soundManager.playSound ("background", true);

			gameOver = false;

			createGameEntities();

			gameThread = new Thread(this);
			gameThread.start();			

		}
	}


	public void startNewGame() {				// initialise and start a new game thread 
		if (gameThread != null || !isRunning) {
			//soundManager.playSound ("background", true);

			endGame();

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
		}
	}


	public void endGame() {					// end the game thread
		isRunning = false;
		//soundManager.stopClip ("background");
	}

}

