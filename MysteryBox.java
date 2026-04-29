import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class MysteryBox{
    private BufferedImage sheet;
    private BufferedImage frame;
    private BufferedImage box;
    Animation animation;

    private int spriteW, spriteH, width, height;
    private int x, y;

    private boolean disappeared;
    private int alpha;

    private Player player;

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
    }

    public void update(){
        if(!disappeared){
            animation.update();
        }

        boolean collision = collidesWithPlayer();
        if(collision&& !disappeared){
            disappearBox(this);
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

    public boolean collidesWithPlayer() {
        return player.getBoundingRectangle().intersects(
            x , y , width, height
        );
    }
}