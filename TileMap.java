import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
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

    private Image[][] tiles;
    private int[][] tileTypes;
    private int screenWidth, screenHeight;
    private int mapWidth, mapHeight;
    private int offsetY;

    private LinkedList sprites;
    private Image mapBackground;
    private Player player;
    private Heart heart;

    BackgroundManager bgManager;

    private GamePanel panel;
    private Dimension dimension;

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

        mapBackground = ImageManager.loadImage("images/city_background_sunset.png");

        tiles = new Image[mapWidth][mapHeight];
	player = new Player (panel, this, bgManager);
	heart = new Heart (panel, player);
		
        sprites = new LinkedList();

	int x, y;
        // Set starting coordinates as requested
	x = 380;
	y = 6590;

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
        
        Image tile = getTile(tileX, tileY);
        if (tile instanceof BufferedImage) {
            BufferedImage bi = (BufferedImage) tile;
            int localX = pixelX % TILE_SIZE;
            int localY = pixelY % TILE_SIZE;
            
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
        // Update dimensions based on the current graphics clip to ensure 
        // the culling logic matches the actual drawing surface (the 1200x1200px buffer).
        Rectangle bounds = g2.getClipBounds();
        if (bounds != null) {
            screenWidth = bounds.width;
            screenHeight = bounds.height;
        }

        int mapWidthPixels = tilesToPixels(mapWidth);
        int mapHeightPixels = tilesToPixels(mapHeight);

        // get the scrolling position of the map
        // based on player's position

        int offsetX = screenWidth / 2 -
            (int)Math.round((double)player.getX()) - (TILE_SIZE / 2);
        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, screenWidth - mapWidthPixels);

        // Calculate dynamic offsetY to keep player centered vertically
        int offsetY = screenHeight / 2 - (int)Math.round((double)player.getY()) - (TILE_SIZE / 2);
        offsetY = Math.min(offsetY, 0);
        offsetY = Math.max(offsetY, screenHeight - mapHeightPixels);
        this.offsetY = offsetY; // Update the field for collision logic

/*
        // draw black background, if needed
        if (background == null ||
            screenHeight > background.getHeight(null))
        {
            g.setColor(Color.black);
            g.fillRect(0, 0, screenWidth, screenHeight);
        }
*/
	// draw the background first

        if (mapBackground != null) {
            // Parallax effect: The background scrolls at 50% speed
            int bgOffsetX = (int) (offsetX * 0.5);
            g2.drawImage(mapBackground, bgOffsetX, offsetY, getWidthPixels(), getHeightPixels(), null);
        } else {
            bgManager.draw (g2);
        }


	//Draw white background (for screen capture)
/*
	g2.setColor (Color.WHITE);
	g2.fill (new Rectangle2D.Double (0, 0, 600, 500));
*/
        // draw the visible tiles

        int firstTileX = pixelsToTiles(-offsetX);
        int lastTileX = pixelsToTiles(-offsetX + screenWidth) + 1;

        int firstTileY = pixelsToTiles(-offsetY);
        int lastTileY = pixelsToTiles(-offsetY + screenHeight) + 1;

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

        g2.drawImage(player.getImage(),
            (int)Math.round((double)player.getX()) + offsetX,
            (int)Math.round((double)player.getY()) + offsetY,
            player.getWidth(),
            player.getHeight(),
            null);

	// draw Heart sprite

        g2.drawImage(heart.getImage(),
            (int)Math.round((double)heart.getX()) + offsetX,
            (int)Math.round((double)heart.getY()) + offsetY, 40, 40,
            null);

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

	if (heart.collidesWithPlayer()) {
		panel.endLevel();
		return;
	}

	heart.update();

	if (heart.collidesWithPlayer()) {
		panel.endLevel();
	}

    }

}
