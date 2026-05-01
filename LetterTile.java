import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class LetterTile{
    private BufferedImage tile;
    private BufferedImage tileCopy, tileTintCopy;
    private char letter; 
    private Player player;

    private int x, y, width, height, alpha, tint;

    private boolean captured, disappeared, tinted;
    private boolean correct;

    private SoundManager sm;

    public LetterTile(BufferedImage tile, char letter, Player player){
        this.tile = tile;
        this.letter = letter;
        this.player = player;

        width = 92;
        height = 92;

        tint = 150;

        captured = false;
        disappeared = false;
        tinted = false;

        //width = tile.getWidth();
        //height = tile.getHeight();

        sm = SoundManager.getInstance();

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

    public boolean isInAnswer(char[] ans){
        for(int i = 0; i < ans.length; i++){
            if(letter == ans[i]){
                correct = true;
                return true;}
                
        }
        return false;
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
            if(tinted){
                g2.drawImage(tileTintCopy, x+offsetX, y+offsetY, width, height, null);
            }else{
                g2.drawImage(tile, x+offsetX, y+offsetY, width, height, null);

            }
        }
        
    }

    private int truncate (int colourValue) {	
		if (colourValue > 255)
			return 255;

		if (colourValue < 0)
			return 0;

		return colourValue;
	}

    private int applyTint (int pixel, String colour) {

    	int alpha, red, green, blue, tintcol;
		int newPixel;

		alpha = (pixel >> 24) & 255;
		red = (pixel >> 16) & 255;
		green = (pixel >> 8) & 255;
		blue = pixel & 255;

		if(colour.equals("red")){
            red = red + tint;
		    red = truncate (red);
        }
        else {
            if(colour.equals("green")){
                green = green + tint;
                green = truncate (green);
            }
        }
		
		newPixel = blue | (green << 8) | (red << 16) | (alpha << 24);

		return newPixel;
	}

    public void tintedCopy(String colour){
        tileTintCopy = ImageManager.copyImage(tile);
        int imW = tileTintCopy.getWidth();
        int imH = tileTintCopy.getHeight();

        int[] pix = new int[imW*imH];
        tileTintCopy.getRGB(0, 0,imW, imH, pix, 0, imW);
        int alpha, red, green, blue;

        for(int i = 0; i < pix.length; i++){
            pix[i]=applyTint(pix[i], colour);
        }

        tileTintCopy.setRGB(0, 0,imW, imH, pix, 0, imW);
        tinted = true;
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