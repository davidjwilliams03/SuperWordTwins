import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class LetterTile{
    private BufferedImage tile;
    private BufferedImage tileCopy;
    private char letter; 
    private Player player;

    private int x, y, width, height, alpha;

    private boolean captured, disappeared;

    public LetterTile(BufferedImage tile, char letter, Player player){
        this.tile = tile;
        this.letter = letter;
        this.player = player;

        width = tile.getWidth()/10;
        height = tile.getHeight()/10;

        captured = false;
        disappeared = false;

        //width = tile.getWidth();
        //height = tile.getHeight();

    }

    public void setx(int x){
        this.x = x;
    }
    public void sety(int y){
        this.y = y;
    }
    public int getx(){
        return this.x;
    }
    public int gety(){
        return this.y;
    }
    public char getLetter(){
        return letter;
    }

    public void setCaptured(boolean captured){
        this.captured = captured;
    }

    public boolean isCaptured(){
        return captured;
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

    private void disappearTile(){
        tileCopy = ImageManager.copyImage(tile);
        int[] pix = new int[width*height];
        tileCopy.getRGB(0, 0,width, height, pix, 0, width);

        for(int i = 0; i < pix.length; i++){
            pix[i]=disappear(pix[i]);
        }

        tileCopy.setRGB(0, 0,width, height, pix, 0, width);
        this.disappeared = true;
    }

    public boolean isDisappeared(){
        return disappeared;
    }

    public void draw(Graphics2D g2, int offsetX, int offsetY) {
        if(disappeared){
            g2.drawImage(tileCopy, x+offsetX, y+offsetY, width, height, null);
        }
        else {
            g2.drawImage(tile, x+offsetX, y+offsetY, width, height, null);
        }
        
    }
        
	

    public boolean collidesWithPlayer() {
        return player.getBoundingRectangle().intersects(
            x , y , width, height
        );
    }

    public void update(){
        boolean collision = collidesWithPlayer();
        if(collision&& !disappeared){
            disappearTile();
        }
        
    }    

    
}