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

    private LinkedList<PendulumAxe> axes;
    private LinkedList<SpinningBlade> blades;
    private LinkedList<GroundSpike> spikes;
    private LinkedList<MysteryBox> mboxes;
    private LinkedList<Enemy> enemies;

    private LinkedList<Coin> coins;
    private int coinsCollected;

    private LinkedList<LetterTile> lts;
    private LinkedList<BufferedImage> tileimgs;
    private char[] letters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private LinkedList sprites;
    private Image skyBackground;
    private Image cityBackground;
    private Player player;
    private Heart heart;

    BackgroundManager bgManager;

    private GamePanel panel;
    private Dimension dimension;

    private String answer;
    private char[] answerLetters;

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

        // Initialize and populate traps
        axes = new LinkedList<>();
        blades = new LinkedList<>();
        spikes = new LinkedList<>();
        mboxes = new LinkedList<>();
        enemies = new LinkedList<>();
        coins = new LinkedList<>();
        lts = new LinkedList<>();
        tileimgs = new LinkedList<>();

        axes.add(new PendulumAxe(11560, 5015, 200, ImageManager.loadImage("images/battle_axe.png")));
        axes.add(new PendulumAxe(6444, 5675, 200, ImageManager.loadImage("images/battle_axe.png")));
        
        blades.add(new SpinningBlade(18200, 6400, ImageManager.loadImage("images/spinning_blade.png")));
        blades.add(new SpinningBlade(18400, 6300, ImageManager.loadImage("images/spinning_blade.png")));
        blades.add(new SpinningBlade(11716, 7500, ImageManager.loadImage("images/spinning_blade.png")));
        blades.add(new SpinningBlade(11716, 6800, ImageManager.loadImage("images/spinning_blade.png")));


        spikes.add(new GroundSpike(14720, 4680, ImageManager.loadImage("images/groundspikes.png")));
        spikes.add(new GroundSpike(14785, 4680, ImageManager.loadImage("images/groundspikes.png")));
        spikes.add(new GroundSpike(14850, 4680, ImageManager.loadImage("images/groundspikes.png")));

        spikes.add(new GroundSpike(17664, 6425, ImageManager.loadImage("images/groundspikes.png")));
        spikes.add(new GroundSpike(6600, 7690, ImageManager.loadImage("images/groundspikes.png")));
        spikes.add(new GroundSpike(6700, 7690, ImageManager.loadImage("images/groundspikes.png")));

        // Add a few enemies to the map near the starting area
        enemies.add(new Goon(17200, 7141, ImageManager.loadImage("images/goon.png"), this));
        enemies.add(new Goon(17500, 7141, ImageManager.loadImage("images/goon.png"), this));

        mboxes.add(new MysteryBox(14720, 4300, player));
        //mboxes.add(new MysteryBox(14900, 4300, player));

        int startx = 14500;
        int starty = 4300;
        for(int i = 0; i < 1; i++){
            coins.add(new Coin(startx + (i*10), starty, player));
        }
        coinsCollected = 0;

        //load in tile images
        tileimgs.add(ImageManager.loadBufferedImage("images/A.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/B.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/C.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/D.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/E.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/F.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/G.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/H.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/I.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/J.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/K.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/L.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/M.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/N.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/O.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/P.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/Q.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/R.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/S.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/T.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/U.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/V.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/W.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/X.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/Y.jpg"));
        tileimgs.add(ImageManager.loadBufferedImage("images/Z.png"));
        //create letter tiles using corresponding indexes. 

        for(int i = 0; i < 26; i++){
            if (tileimgs.get(i) == null) System.out.println("Image " + letters[i] + " failed to load");
            lts.add(new LetterTile(tileimgs.get(i), letters[i], player));
        }
        //TO FIX: NOT ALL TILES ARE SAME SIZE...
        lts.get(25).setx(14200);
        lts.get(25). sety(4300);

        answer = panel.getAnswer();
        //System.out.println("Ans: " + answer);
        answerLetters = answer.toCharArray();
        //System.out.println("Answer Letters: " + answerLetters.length);

	    heart = new Heart (panel, player);
		
        sprites = new LinkedList();

	    int x, y;
        // Adjusted starting coordinates to suit the 32x32 grid
	    x = 19368;
	    y = 2213;

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
        int type = getTileType(tileX, tileY);

        // Mathematical Slope Collision: prevents jitter and "ghosting" by using 
        // the same floor formulas used for snapping.
        if (type >= TILE_RISING_LOW && type <= TILE_FALLING_LOW) {
            int localX = pixelX - tilesToPixels(tileX);
            int localY = pixelY - tilesToPixels(tileY);
            float floorY = 0;
            
            if (type == TILE_RISING_LOW) floorY = 64 - (localX / 2.0f);
            else if (type == TILE_RISING_HIGH) floorY = 32 - (localX / 2.0f);
            else if (type == TILE_FALLING_HIGH) floorY = localX / 2.0f;
            else if (type == TILE_FALLING_LOW) floorY = 32 + (localX / 2.0f);
            
            return localY >= floorY;
        }
        
        Image tile = getTile(tileX, tileY);
        if (tile instanceof BufferedImage) {
            BufferedImage bi = (BufferedImage) tile;
            int localX = pixelX - tilesToPixels(tileX);
            int localY = pixelY - tilesToPixels(tileY);
            
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
        } else if (player.isCeilingWalking()) {
            // Invert player 180 degrees for ceiling walking
            AffineTransform old = g2.getTransform();
            
            g2.rotate(Math.toRadians(180), pX + pW / 2, pY + pH / 2);
            g2.drawImage(player.getImage(), pX, pY, pW, pH, null);
            
            // Restore transform
            g2.setTransform(old);
        } else {
            g2.drawImage(player.getImage(), pX, pY, pW, pH, null);
        }

        for (Enemy enemy : enemies) {
            enemy.draw(g2, offsetX, offsetY);
        }

        // Draw Traps
        for (PendulumAxe axe : axes) {
            axe.draw(g2, offsetX, offsetY);
        }
        for (SpinningBlade blade : blades) {
            blade.draw(g2, offsetX, offsetY);
        }
        for (GroundSpike spike : spikes) {
            spike.draw(g2, offsetX, offsetY);
        }
        for(MysteryBox box : mboxes){
            box.draw(g2, offsetX, offsetY);
        }
        for(Coin coin : coins){
            if(!coin.isDisappeared())
                coin.draw(g2, offsetX, offsetY);
        }
        for(LetterTile lt : lts){
            if(!lt.isDisappeared())
                lt.draw(g2, offsetX, offsetY);
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

        // Update hazards and check collision
        for (PendulumAxe axe : axes) {
            axe.update();
            if (axe.collidesWith(player)) {
                player.setHealth(0); // Instant death from traps
            }
        }
        for (SpinningBlade blade : blades) {
            blade.update();
            if (blade.collidesWith(player)) {
                player.setHealth(0); // Instant death from traps
            }
        }
        for (GroundSpike spike : spikes) {
            if (spike.collidesWith(player)) {
                player.setHealth(0); // Instant death from traps
            }
        }

        for (Enemy enemy : enemies) {
            enemy.update();
            if (enemy.collidesWith(player)) {
                player.takeDamage(10); // Chip damage from enemies
            }
        }
        for(MysteryBox box : mboxes){
            box.update();
        }
        /*for(LetterTile lt : lts){
            lt.update();
            if(lt.isDisappeared()){
                char c = lt.getLetter();
                for (int i = 0; i < answerLetters.length; i++){
                    if(c == answerLetters[i]){
                        lt.setCaptured(true);
                        break;
                    }
                }
                if(!lt.isCaptured()){
                    player.takeDamage(10);
                }
                lts.remove(lt);
            }
            

        }*/
       Iterator<LetterTile> lit = lts.iterator();
       while(lit.hasNext()){
        LetterTile lt = lit.next();
        lt.update();

        if(lt.isDisappeared()){
            char c = lt.getLetter();
            lt.setCaptured(false);
            for(char l: answerLetters){
                if(c == l){
                    lt.setCaptured(true);
                    break;
                }
            }
            if(!lt.isCaptured()){
                player.takeDamage(10);
            }
            lit.remove();
        }
       }


        Iterator<Coin> coinIt = coins.iterator();
        while (coinIt.hasNext()) {
            Coin coin = coinIt.next();
            coin.update();
            if (coin.isDisappeared()) {
                coinIt.remove(); // Safely remove collected coin
                coinsCollected++;
            }
        }

        if (player.getHealth() <= 0) {
            panel.setGameOver();
            return;
        }

	if (heart.collidesWithPlayer()) {
		panel.endLevel();
		return;
	}

	heart.update();

	if (heart.collidesWithPlayer()) {
		panel.endLevel();
	}

    }

    public int getCoinsCollected(){
        return coinsCollected;
    }

    public Player getPlayer() {
        return player;
    }

}
