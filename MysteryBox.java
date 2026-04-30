import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class MysteryBox{
    private BufferedImage sheet;
    private BufferedImage frame;
    private BufferedImage box;
    Animation animation;

    private int spriteW, spriteH, width, height;
    private int x, y;

    private boolean disappeared, boosted, slowed, tinted;
    private int alpha, duration, timer;

    private Player player;
    private Random random;

    public MysteryBox(int x, int y, Player player){
        this.x = x;
        this.y = y;
        this.player = player;

        spriteW = 352;
        spriteH = 256;
        width = spriteW/2;
        height = spriteH/2;

        animation = new Animation(true);

        sheet = ImageManager.loadBufferedImage("images/mysteryBoxSheet.png");
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 4; j++){
                frame = sheet.getSubimage(j*spriteW, i*spriteH, spriteW, spriteH);
                animation.addFrame(frame, 100);
            }
        }

        animation.start();
        disappeared = false;
        boosted = false;
        slowed = false;
    }

    public void update(){
        if(!disappeared){
            animation.update();
        }
    }

    public void draw(Graphics2D g2, int offsetX, int offsetY) {

		if (isDisappeared()) {
            g2.drawImage(box, x+offsetX, y+offsetY, width, height, null);
		}
        else
		    g2.drawImage(animation.getImage(), x+offsetX, y+offsetY, width, height, null);
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

    public void disappearBox(MysteryBox m){
        box = ImageManager.loadBufferedImage("images/box.png");
        int imw = box.getWidth();
        int imh = box.getHeight();

        int[] pix = new int[imw*imh];
        box.getRGB(0, 0,imw, imh, pix, 0, imw);
        int alpha, red, green, blue;

        for(int i = 0; i < pix.length; i++){
            pix[i]=disappear(pix[i]);
        }

        box.setRGB(0, 0,imw, imh, pix, 0, imw);
        m.disappeared = true;
        //m.animation.stop();
    }

    public boolean isDisappeared(){
        return disappeared;
    }
    public boolean isBoosted(){
        return boosted;
    }
    public void setBoosted(boolean boosted){
        this.boosted = boosted;
    }
    public void setSlowed(boolean slowed){
        this.slowed = slowed;
    }
    public void setTinted(boolean tinted){
        this.tinted = tinted;
    }

    
    public boolean isSlowed(){
        return slowed;
    }
    public boolean isTinted(){
        return tinted;
    }
    public int getTimer(){
        return timer;
    }
    public int getDuration(){
        return duration;
    }

    public void speedPlayer(int time){
        player.setdx(player.getdx()+25);
        duration = time;
        timer = 0;
        boosted = true;
        slowed = false;
        tinted = false;    
    }

    public void slowPlayer(int time){
        player.setdx(player.getdx()-25);
        duration = time;
        timer = 0;
        slowed = true;
        boosted = false;
        tinted = false;
    }

    public void tintTiles(String colour, LetterTile lt, int time){
        duration = time;
        timer = 0;
        lt.tintedCopy(colour);
        tinted = true;
        boosted = false; 
        slowed = false;
    }

    public boolean collidesWithPlayer() {
        return player.getBoundingRectangle().intersects(
            x , y , width, height
        );
    }
}