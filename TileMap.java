import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * The TileMap class contains the data for a tile-based
 * map, including Sprites. Each tile is a reference to an
 * Image. Images are used multiple times in the tile map.
 * map.
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
    private char[] letters = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

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

    private SoundManager sm;

    /**
     * Creates a new TileMap with the specified width and
     * height (in number of tiles) of the map.
     */
    public TileMap(GamePanel panel, int width, int height) {

        this.panel = panel;
        dimension = panel.getSize();

        screenWidth = dimension.width;
        screenHeight = dimension.height;

        System.out.println("Width: " + screenWidth);
        System.out.println("Height: " + screenHeight);

        mapWidth = width;
        mapHeight = height;

        // get the y offset to draw all sprites and tiles
        tileTypes = new int[mapWidth][mapHeight];

        offsetY = 0;
        System.out.println("offsetY: " + offsetY);

        bgManager = new BackgroundManager(panel, 12);

        skyBackground = ImageManager.loadImage("images/sky11d.png");
        cityBackground = ImageManager.loadImage("images/city_background_clean.png");

        tiles = new Image[mapWidth][mapHeight];
        player = new Player(panel, this, bgManager);

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

        blades.add(new SpinningBlade(17642, 4994, ImageManager.loadImage("images/spinning_blade.png")));
        blades.add(new SpinningBlade(17642, 5519, ImageManager.loadImage("images/spinning_blade.png")));
        blades.add(new SpinningBlade(11716, 7500, ImageManager.loadImage("images/spinning_blade.png")));
        blades.add(new SpinningBlade(11716, 6800, ImageManager.loadImage("images/spinning_blade.png")));

        spikes.add(new GroundSpike(14720, 4680, ImageManager.loadImage("images/groundspikes.png")));
        spikes.add(new GroundSpike(14785, 4680, ImageManager.loadImage("images/groundspikes.png")));
        spikes.add(new GroundSpike(14850, 4680, ImageManager.loadImage("images/groundspikes.png")));

        spikes.add(new GroundSpike(17664, 6425, ImageManager.loadImage("images/groundspikes.png")));
        spikes.add(new GroundSpike(6600, 7690, ImageManager.loadImage("images/groundspikes.png")));
        spikes.add(new GroundSpike(6700, 7690, ImageManager.loadImage("images/groundspikes.png")));

        // Add a few enemies to the map near the starting area
        // enemies.add(new Goon(17200, 7141, ImageManager.loadImage("images/goon.png"),
        // this));
        // enemies.add(new Goon(17500, 7141, ImageManager.loadImage("images/goon.png"),
        // this));

        mboxes.add(new MysteryBox(14720, 4300, player));
        mboxes.add(new MysteryBox(6500, 6333, player));
        mboxes.add(new MysteryBox(11768, 5629, player));
        // mboxes.add(new MysteryBox(14900, 4300, player));

        // code for coin placement
        // level 1
        int startx = 14500;
        int starty = 4300;
        for (int i = 0; i < 1; i++) {
            coins.add(new Coin(startx + (i * 10), starty, player));
        }
        coinsCollected = 0;

        for (int i = 0; i < 4; i++) {
            coins.add(new Coin(1216 + (i * 60), 6245, player));
        }

        for (int i = 0; i < 8; i++) {
            coins.add(new Coin(1788 + (i * 60), 6053, player));
        }

        for (int i = 0; i < 7; i++) {
            coins.add(new Coin(4744 + (i * 60), 5769, player));
        }

        // Set 1: x=4132, y=7230, num=1
        for (int i = 0; i < 1; i++) {
            coins.add(new Coin(4132 + (i * 60), 7230, player));
        }

        // Set 2: x=5812, y=7205, num=6
        for (int i = 0; i < 6; i++) {
            coins.add(new Coin(5812 + (i * 60), 7205, player));
        }

        // Set 3: x=7248, y=7269, num=3
        for (int i = 0; i < 3; i++) {
            coins.add(new Coin(7248 + (i * 60), 7269, player));
        }

        // Set 4: x=7576, y=8549, num=3
        for (int i = 0; i < 3; i++) {
            coins.add(new Coin(7576 + (i * 60), 8549, player));
        }

        // Set 5: x=7576, y=8128, num=30
        for (int i = 0; i < 30; i++) {
            coins.add(new Coin(7576 + (i * 60), 8128, player));
        }

        // Set 6: x=8080, y=8293, num=3
        for (int i = 0; i < 3; i++) {
            coins.add(new Coin(8080 + (i * 60), 8293, player));
        }

        // Set 7: x=8060, y=8485, num=3
        for (int i = 0; i < 3; i++) {
            coins.add(new Coin(8060 + (i * 60), 8485, player));
        }

        // Set 8: x=8996, y=8357, num=3
        for (int i = 0; i < 3; i++) {
            coins.add(new Coin(8996 + (i * 60), 8357, player));
        }

        // Set 9: x=5144, y=6501, num=6
        for (int i = 0; i < 6; i++) {
            coins.add(new Coin(5144 + (i * 60), 6501, player));
        }

        // Set 10: x=10304, y=8549, num=4
        for (int i = 0; i < 4; i++) {
            coins.add(new Coin(10304 + (i * 60), 8549, player));
        }

        // Set 11: x=10704, y=8421, num=1
        for (int i = 0; i < 1; i++) {
            coins.add(new Coin(10704 + (i * 60), 8421, player));
        }

        // Set 12: x=10020, y=7589, num=10
        for (int i = 0; i < 10; i++) {
            coins.add(new Coin(10020, 7589 + (i * 60), player));
        }

        // Set 13: x=6044, y=6285, num=4
        for (int i = 0; i < 4; i++) {
            coins.add(new Coin(6044 + (i * 60), 6285, player));
        }

        // Set 14: x=7140, y=6245, num=3
        for (int i = 0; i < 3; i++) {
            coins.add(new Coin(7140 + (i * 60), 6245, player));
        }

        // Set 15: x=7392, y=6053, num=3
        for (int i = 0; i < 3; i++) {
            coins.add(new Coin(7392 + (i * 60), 6053, player));
        }

        // Set 16: x=9344, y=6181, num=2
        for (int i = 0; i < 2; i++) {
            coins.add(new Coin(9344 + (i * 60), 6181, player));
        }

        // Set 17: x=9960, y=5925, num=7
        for (int i = 0; i < 7; i++) {
            coins.add(new Coin(9960 + (i * 60), 5925, player));
        }

        // Set 18: x=10448, y=6437, num=2
        for (int i = 0; i < 2; i++) {
            coins.add(new Coin(10448 + (i * 60), 6437, player));
        }

        // Set 19: x=10776, y=7525, num=2
        for (int i = 0; i < 2; i++) {
            coins.add(new Coin(10776 + (i * 60), 7525, player));
        }

        // Set 20: x=11068, y=6373, num=2
        for (int i = 0; i < 2; i++) {
            coins.add(new Coin(11068 + (i * 60), 6373, player));
        }

        // Set 21: x=12696, y=5221, num=8
        for (int i = 0; i < 8; i++) {
            coins.add(new Coin(12696 + (i * 60), 5221, player));
        }

        // Set 22: x=15924, y=5925, num=8
        for (int i = 0; i < 8; i++) {
            coins.add(new Coin(15924 + (i * 60), 5925, player));
        }

        // Set 23: x=14988, y=6317, num=25
        for (int i = 0; i < 25; i++) {
            coins.add(new Coin(14988 + (i * 60), 6317, player));
        }

        // Set 24: x=15272, y=6912, num=8
        for (int i = 0; i < 8; i++) {
            coins.add(new Coin(15272 + (i * 60), 6912, player));
        }

        // Set 25: x=16416, y=1741, num=10
        for (int i = 0; i < 10; i++) {
            coins.add(new Coin(16416 + (i * 60), 1741, player));
        }

        // Set 26: x=18256, y=4653, num=10
        for (int i = 0; i < 10; i++) {
            coins.add(new Coin(18256, 4653 + (i * 60), player));
        }

        // Set 27: x=17496, y=4147, num=4
        for (int i = 0; i < 4; i++) {
            coins.add(new Coin(17496 + (i * 60), 4147, player));
        }

        // Set 28: x=18256, y=2200, num=10
        for (int i = 0; i < 10; i++) {
            coins.add(new Coin(18256, 2200 + (i * 60), player));
        }

        // Set 29: x=19352, y=2213, num=2
        for (int i = 0; i < 2; i++) {
            coins.add(new Coin(19352 + (i * 60), 2213, player));
        }

        // Set 30: x=21524, y=997, num=3
        for (int i = 0; i < 3; i++) {
            coins.add(new Coin(21524 + (i * 60), 997, player));
        }

        // end of l1 coins

        // load in tile images
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
        // create letter tiles using corresponding indexes.

        answer = panel.getAnswer();
        answerLetters = answer.toUpperCase().toCharArray();

        // Define the 13 locations
        int[] locX = { 568, 3284, 5540, 4012, 3968, 10316, 10072, 9872, 15256, 14876, 17152, 18148, 20504 };
        int[] locY = { 6559, 6309, 6540, 7269, 7104, 8448, 6885, 6117, 5669, 6629, 6053, 4005, 1701 };

        // Prepare a pool of letters for these 13 slots
        java.util.ArrayList<Character> letterPool = new java.util.ArrayList<>();

        // 1. Add all unique alphabetic letters from the answer
        java.util.Set<Character> uniqueAnswerLetters = new java.util.HashSet<>();
        for (char c : answerLetters) {
            if (Character.isLetter(c)) {
                uniqueAnswerLetters.add(c);
            }
        }

        for (char c : uniqueAnswerLetters) {
            letterPool.add(c);
        }

        // 2. Fill the rest with random wrong letters
        java.util.Random rand = new java.util.Random();
        while (letterPool.size() < locX.length) {
            char randomChar = (char) ('A' + rand.nextInt(26));
            if (!uniqueAnswerLetters.contains(randomChar)) {
                letterPool.add(randomChar);
            }
        }

        // 3. Shuffle the pool to distribute correct letters randomly
        java.util.Collections.shuffle(letterPool);

        // 4. Create and place the tiles
        for (int i = 0; i < locX.length; i++) {
            char c = letterPool.get(i);
            int letterIndex = c - 'A';
            if (letterIndex >= 0 && letterIndex < 26) {
                BufferedImage img = tileimgs.get(letterIndex);
                if (img == null) {
                    System.out.println("Image " + c + " failed to load");
                    img = tileimgs.get(0);
                }
                LetterTile lt = new LetterTile(img, c, player);
                lt.setx(locX[i]);
                lt.sety(locY[i]);
                lts.add(lt);
            }
        }

        sprites = new LinkedList();
        sm = SoundManager.getInstance();
        setupLevel(1);
    }

    public void setupLevel(int level) {
        if (level == 1) {
            player.setX(100);
            player.setY(100);
            player.setStartPosition(100, 100);
            player.setBounds(0, 23092);
            heart = new Heart(panel, player, 22408, 165);
            System.out.println("Level 1 initialized");
        } else if (level == 2) {
            player.setX(23056);
            player.setY(165);
            player.setStartPosition(23056, 165);
            player.setBounds(22924, getWidthPixels());
            heart = new Heart(panel, player, 43800, 4517);
            System.out.println("Level 2 initialized");
        }
    }

    /**
     * Gets the width of this TileMap (number of pixels across).
     */
    public int getWidthPixels() {
        return tilesToPixels(mapWidth);
    }

    /**
     * Gets the height of this TileMap (number of pixels down).
     */
    public int getHeightPixels() {
        return tilesToPixels(mapHeight);
    }

    /**
     * Gets the width of this TileMap (number of tiles across).
     */
    public int getWidth() {
        return mapWidth;
    }

    /**
     * Gets the height of this TileMap (number of tiles down).
     */
    public int getHeight() {
        return mapHeight;
    }

    public int getOffsetY() {
        return offsetY;
    }

    /**
     * Gets the tile at the specified location. Returns null if
     * no tile is at the location or if the location is out of
     * bounds.
     */
    public Image getTile(int x, int y) {
        if (x < 0 || x >= mapWidth ||
                y < 0 || y >= mapHeight) {
            return null;
        } else {
            return tiles[x][y];
        }
    }

    /**
     * Sets the tile at the specified location.
     */
    public void setTile(int x, int y, Image tile) {
        tiles[x][y] = tile;
    }

    /**
     * Gets the type of the tile at the specified location.
     */
    public int getTileType(int x, int y) {
        if (x < 0 || x >= mapWidth || y < 0 || y >= mapHeight)
            return TILE_EMPTY;
        return tileTypes[x][y];
    }

    /**
     * Sets the type of the tile at the specified location.
     */
    public void setTileType(int x, int y, int type) {
        tileTypes[x][y] = type;
    }

    /**
     * Checks if a specific pixel coordinate in the map is solid.
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

            if (type == TILE_RISING_LOW)
                floorY = 64 - (localX / 2.0f);
            else if (type == TILE_RISING_HIGH)
                floorY = 32 - (localX / 2.0f);
            else if (type == TILE_FALLING_HIGH)
                floorY = localX / 2.0f;
            else if (type == TILE_FALLING_LOW)
                floorY = 32 + (localX / 2.0f);

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
     * Gets an Iterator of all the Sprites in this map,
     * excluding the player Sprite.
     */

    public Iterator getSprites() {
        return sprites.iterator();
    }

    /**
     * Class method to convert a pixel position to a tile position.
     */

    public static int pixelsToTiles(float pixels) {
        return pixelsToTiles(Math.round(pixels));
    }

    /**
     * Class method to convert a pixel position to a tile position.
     */

    public static int pixelsToTiles(int pixels) {
        return (int) Math.floor((float) pixels / TILE_SIZE);
    }

    /**
     * Class method to convert a tile position to a pixel position.
     */

    public static int tilesToPixels(int numTiles) {
        return numTiles * TILE_SIZE;
    }

    /**
     * Draws the specified TileMap.
     */
    public void draw(Graphics2D g2) {
        double zoom = 0.8; // Zoom out factor (0.8 = 80% scale, or 20% zoom out)
        AffineTransform oldTransform = g2.getTransform();

        // Determine virtual screen size based on zoom level for centering and culling
        Rectangle bounds = g2.getClipBounds();
        int virtualWidth = (bounds != null) ? (int) (bounds.width / zoom) : screenWidth;
        int virtualHeight = (bounds != null) ? (int) (bounds.height / zoom) : screenHeight;

        int mapWidthPixels = tilesToPixels(mapWidth);
        int mapHeightPixels = tilesToPixels(mapHeight);

        // get the scrolling position of the map
        // based on player's position

        int offsetX = virtualWidth / 2 -
                (int) Math.round((double) player.getX()) - (TILE_SIZE / 2);
        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, virtualWidth - mapWidthPixels);

        // Calculate dynamic offsetY to keep player centered vertically
        int offsetY = (int) (virtualHeight * 0.75) - (int) Math.round((double) player.getY()) - (TILE_SIZE / 2);
        // Vertical clamping removed to allow the camera to follow the player at
        // unlimited height and depth.
        this.offsetY = offsetY; // Update the field for collision logic

        // Apply the zoom scale to the graphics context
        g2.scale(zoom, zoom);

        // Clear the visible area to prevent "ghosting" or duplicated pixels
        // when the camera moves outside the map boundaries.
        // Game Programming Trick: Change the clear color to exactly match the top pixel
        // of your sky image so it flawlessly blends infinitely upwards.
        g2.setColor(new Color(0, 10, 27));
        g2.fillRect(0, 0, virtualWidth, virtualHeight);

        // draw the background first

        if (skyBackground != null || cityBackground != null) {
            if (skyBackground != null) {
                int skyOffsetX = (int) (offsetX * 0.2);
                // Game Programming Trick: Anchor the sky to the top of the screen
                // if the camera looks above the map (offsetY > 0)
                int skyOffsetY = Math.min(offsetY, 0);
                g2.drawImage(skyBackground, skyOffsetX, skyOffsetY, getWidthPixels(), getHeightPixels(), null);
            }
            if (cityBackground != null) {
                int cityOffsetX = (int) (offsetX * 0.5);
                g2.drawImage(cityBackground, cityOffsetX, offsetY, getWidthPixels(), getHeightPixels(), null);
            }
        } else {
            bgManager.draw(g2);
        }

        // Draw white background (for screen capture)
        /*
         * g2.setColor (Color.WHITE);
         * g2.fill (new Rectangle2D.Double (0, 0, 600, 500));
         */
        // draw the visible tiles

        int firstTileX = Math.max(0, pixelsToTiles(-offsetX) - 1);
        int lastTileX = Math.min(mapWidth - 1, pixelsToTiles(-offsetX + virtualWidth) + 6);

        int firstTileY = Math.max(0, pixelsToTiles(-offsetY) - 3);
        int lastTileY = Math.min(mapHeight - 1, pixelsToTiles(-offsetY + virtualHeight) + 6);

        for (int y = firstTileY; y <= lastTileY; y++) {
            for (int x = firstTileX; x <= lastTileX; x++) {
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

        int pX = (int) Math.round((double) player.getX()) + offsetX;
        int pY = (int) Math.round((double) player.getY()) + offsetY;
        int pW = player.getWidth();
        int pH = player.getHeight();

        Image pImg = player.getImage();
        if (pImg != null) {
            double scale = 0.675; // Scale down the massive new animation frames
            int imgW = pImg.getWidth(null);
            int imgH = pImg.getHeight(null);
            int drawW = (int) (imgW * scale);
            int drawH = (int) (imgH * scale);

            // Anchor to the bottom center of the physics hitbox
            // Nudge it slightly down by adding + 5 to the drawY to make sure feet touch the
            // ground
            int drawX = pX + (pW / 2) - (drawW / 2);
            int drawY = pY + pH - drawH + 5;

            if (player.isCeilingWalking()) {
                // Invert player 180 degrees for ceiling walking
                AffineTransform old = g2.getTransform();

                g2.rotate(Math.toRadians(180), pX + pW / 2, pY + pH / 2);
                g2.drawImage(pImg, drawX, drawY, drawW, drawH, null);

                // Restore transform
                g2.setTransform(old);
            } else {
                g2.drawImage(pImg, drawX, drawY, drawW, drawH, null);
            }
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
        for (MysteryBox box : mboxes) {
            box.draw(g2, offsetX, offsetY);
        }
        for (Coin coin : coins) {
            if (!coin.isDisappeared())
                coin.draw(g2, offsetX, offsetY);
        }
        for (LetterTile lt : lts) {
            if (!lt.isDisappeared())
                lt.draw(g2, offsetX, offsetY);
        }

        // draw Heart sprite if riddle is solved
        if (panel.isWordComplete()) {
            g2.drawImage(heart.getImage(),
                    (int) Math.round((double) heart.getX()) + offsetX,
                    (int) Math.round((double) heart.getY()) + offsetY, 40, 40,
                    null);
        }

        // Restore original transform to avoid affecting UI or subsequent draw calls
        g2.setTransform(oldTransform);

        /*
         * // draw sprites
         * Iterator i = map.getSprites();
         * while (i.hasNext()) {
         * Sprite sprite = (Sprite)i.next();
         * int x = Math.round(sprite.getX()) + offsetX;
         * int y = Math.round(sprite.getY()) + offsetY;
         * g.drawImage(sprite.getImage(), x, y, null);
         * 
         * // wake up the creature when it's on screen
         * if (sprite instanceof Creature &&
         * x >= 0 && x < screenWidth)
         * {
         * ((Creature)sprite).wakeUp();
         * }
         * }
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
                player.takeDamage(50); // Traps deal 50 damage
            }
        }
        for (SpinningBlade blade : blades) {
            blade.update();
            if (blade.collidesWith(player)) {
                player.takeDamage(50); // Traps deal 50 damage
            }
        }
        for (GroundSpike spike : spikes) {
            if (spike.collidesWith(player)) {
                player.takeDamage(50); // Traps deal 50 damage
            }
        }

        for (Enemy enemy : enemies) {
            enemy.update();
            if (enemy.collidesWith(player)) {
                player.takeDamage(10); // Chip damage from enemies
            }
        }
        for (MysteryBox box : mboxes) {
            box.update();
            if (box.collidesWithPlayer() && !box.isDisappeared()) {
                box.disappearBox(box);
                player.heal(10);

                Random random = new Random();
                int n = 5;
                // random.nextInt(5);
                if (n == 0)
                    box.slowPlayer(10000);
                else {
                    if (n == 1) {
                        box.speedPlayer(10000);
                        ;
                    } else {
                        if (n == 2) {
                            for (LetterTile lt : lts) {
                                if (lt.isInAnswer(panel.getAnswer().toCharArray()))
                                    box.tintTiles("green", lt, 20000);
                                else
                                    box.tintTiles("red", lt, 20000);
                            }
                        } else {
                            if (n == 3) {
                                player.heal(100);
                            } else {
                                if (n == 4) {// FIXXX
                                    player.setWidth(player.getWidth() * 10);
                                    player.setHeight(player.getHeight() * 10);
                                    box.enlargePlayer(10000);
                                }
                            }
                        }
                    }

                }

            }

            if (box.isBoosted()) {
                int timer = box.getTimer();
                int duration = box.getDuration();
                timer = timer + 50;

                if (timer >= duration) {
                    player.setdx(player.getdx() - 25);
                    box.setBoosted(false);
                }
            }

            if (box.isSlowed()) {
                int timer = box.getTimer();
                int duration = box.getDuration();
                timer = timer + 50;

                if (timer >= duration) {
                    player.setdx(player.getdx() + 25);
                    box.setSlowed(false);
                }
            }

            if (box.isEnlarged()) {
                int timer = box.getTimer();
                int duration = box.getDuration();
                timer = timer + 50;

                if (timer >= duration) {
                    player.setHeight(player.getHeight() / 10);
                    player.setWidth(player.getWidth() / 10);
                    box.setEnlarged(false);
                }
            }

        }
        /*
         * for(LetterTile lt : lts){
         * lt.update();
         * if(lt.isDisappeared()){
         * char c = lt.getLetter();
         * for (int i = 0; i < answerLetters.length; i++){
         * if(c == answerLetters[i]){
         * lt.setCaptured(true);
         * break;
         * }
         * }
         * if(!lt.isCaptured()){
         * player.takeDamage(10);
         * }
         * lts.remove(lt);
         * }
         * 
         * 
         * }
         */
        Iterator<LetterTile> lit = lts.iterator();
        while (lit.hasNext()) {
            LetterTile lt = lit.next();
            lt.update();

            if (lt.isDisappeared()) {
                char c = lt.getLetter();
                lt.setCaptured(false);
                for (char l : answerLetters) {
                    if (c == l) {
                        lt.setCaptured(true);
                        panel.correctGuess(c);
                        sm.playSound("correct", false);
                        break;
                    }
                }
                if (!lt.isCaptured()) {
                    player.takeDamage(10);
                    sm.playSound("wrong", false);
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
                player.heal(1); // Each coin heals 1
            }
        }

        if (player.getHealth() <= 0) {
            if (player.isDeathFinished()) {
                panel.setGameOver();
            }
            return;
        }

        if (panel.isWordComplete()) {
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

    public int getCoinsCollected() {
        return coinsCollected;
    }

    public Player getPlayer() {
        return player;
    }

}
