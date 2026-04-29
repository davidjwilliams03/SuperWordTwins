import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Harley{
    Animation idle, fall, attack, currAnim;
    private BufferedImage idleSheet, attackSheet, frame;

    private int x, y;
    private int dx, dy;

    public Harley(){
        x = 100;
        y = 200;
        dx = 10;
        dy = 10;
        idle = new Animation(true);
        fall = new Animation(false);
        attack = new Animation(false);

        loadAnimations();
        this.currAnim = idle;
    }

    public void loadAnimations(){
        idleSheet = ImageManager.loadBufferedImage("images/characters/hqIdle.png");
        for(int i = 0; i < 4; i++){
            frame = idleSheet.getSubimage(i*96, 0, 96, 97);
            idle.addFrame(frame, 150);
        }
    }

    public void start(){
        currAnim.start();
    }

    public void update(){
        if(!currAnim.isStillActive()){
            return;
        }
        currAnim.update();
    
    }

    public void draw(Graphics2D g2) {

		//if (!currAnim.isStillActive()) {
		//	return;
		//}

		g2.drawImage(currAnim.getImage(), x, y, null);
	}
}