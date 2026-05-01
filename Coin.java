import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Coin {
    Animation animation;
    private BufferedImage sheet;
    private BufferedImage frame;
    private BufferedImage coin;
    private Player player;

    private int x;
    private int y;

    private int spriteW;
    private int spriteH;
    private int width, reducedW;
    private int height, reducedH;

    private int dx, dy;

    private Random random;

    private int alpha;
    private boolean disappeared;

    private SoundManager sm;

    public Coin(int x, int y, Player player){
        this.x = x;
        this.y = y;
        this.player = player;

        //spriteW = 85;
        //spriteH = 91;
        width = 50;
        height = 55;

        random = new Random();
        animation = new Animation(true);

        frame = ImageManager.loadBufferedImage("images/coin1.png");
        animation.addFrame(frame, 300);
        frame = ImageManager.loadBufferedImage("images/coin2.png");
        animation.addFrame(frame, 300);
        frame = ImageManager.loadBufferedImage("images/coin3.png");
        animation.addFrame(frame, 300);
        frame = ImageManager.loadBufferedImage("images/coin4.png");
        animation.addFrame(frame, 300);
        frame = ImageManager.loadBufferedImage("images/coin5.png");
        animation.addFrame(frame, 300);
        frame = ImageManager.loadBufferedImage("images/coin6.png");
        animation.addFrame(frame, 300);

        animation.start();
        disappeared = false;
        //sm = new SoundManager();
        //effect = "";
        sm = SoundManager.getInstance();
    }



    private int disappear(int pixel){
        int red, green, blue, newPixel;

        alpha = (pixel >> 24) & 255;
		red = (pixel >> 16) & 255;
		green = (pixel >> 8) & 255;
		blue = pixel & 255;

		alpha = 1;
		
		newPixel = blue | (green << 8) | (red << 16) | (alpha << 24);

		return newPixel;
    }

    private void disappearCoin(Coin c){
        coin = ImageManager.loadBufferedImage("images/coin.png");
        int imW = coin.getWidth();
        int imH = coin.getHeight();

        int[] pix = new int[imW*imH];
        coin.getRGB(0, 0,imW, imH, pix, 0, imW);

        for(int i = 0; i < pix.length; i++){
            pix[i]=disappear(pix[i]);
        }

        coin.setRGB(0, 0,imW, imH, pix, 0, imW);
        c.disappeared = true;
    }

    public boolean isDisappeared(){
        return disappeared;
    }

    public void draw(Graphics2D g2, int offsetX, int offsetY) {
		if (isDisappeared()) {
            g2.drawImage(coin, x+offsetX, y+offsetY, width, height, null);
		}
        else{
            BufferedImage currentFrame = (BufferedImage) animation.getImage();
        
            if (currentFrame != null) {
                int reducedW = currentFrame.getWidth() / 2;
                int reducedH = currentFrame.getHeight() / 2;
            
                this.width = reducedW;
                this.height = reducedH;

                g2.drawImage(currentFrame, x + offsetX, y + offsetY, reducedW, reducedH, null);
            }
        }
	}

    public boolean collidesWithPlayer() {
        return player.getBoundingRectangle().intersects(
            x , y , width, height
        );
    }

    public void update(){
        if(!disappeared){
            animation.update();
        }

        boolean collision = collidesWithPlayer();
        if(collision&& !disappeared){
            sm.playSound("coin", false);
            disappearCoin(this);
        }
        
    }    
    
}
