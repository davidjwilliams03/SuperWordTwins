import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;


/**
    The TileMapeManager class loads and manages tile Images and
    "host" Sprites used in the game. Game Sprites are cloned from
    "host" Sprites.
*/
public class TileMapManager {

    private ArrayList<BufferedImage> tiles;
    private int currentMap = 0;

    private GamePanel panel;

/*

    // host sprites used for cloning
    private Sprite playerSprite;
    private Sprite musicSprite;
    private Sprite coinSprite;
    private Sprite goalSprite;
    private Sprite grubSprite;
    private Sprite flySprite;
*/

    public TileMapManager(GamePanel panel) {
	this.panel = panel;

        loadTileImages();

        //loadCreatureSprites();
        //loadPowerUpSprites();
    }


     public TileMap loadMap(String filename)
        throws IOException
    {
        ArrayList<String[]> tileData = new ArrayList<>();
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("TILE")) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    tileData.add(parts);
                    int tx = Integer.parseInt(parts[1]);
                    int ty = Integer.parseInt(parts[2]);
                    
                    minX = Math.min(minX, tx);
                    maxX = Math.max(maxX, tx);
                    minY = Math.min(minY, ty);
                    maxY = Math.max(maxY, ty);
                }
            } else if (line.startsWith("BACKGROUND")) {
                // Background info from Godot can be logged or used for initialization
                System.out.println("Map Background Info: " + line);
            }
        }
        reader.close();

        if (tileData.isEmpty()) {
            System.out.println("No tile data found in " + filename);
            return new TileMap(panel, 1, 1);
        }

        int mapWidth = maxX - minX + 1;
        int mapHeight = maxY - minY + 1;

        TileMap newMap = new TileMap(panel, mapWidth, mapHeight);

        // Constant to map Godot Atlas (x,y) coordinates to a linear index for tiles A, B, C...
        // If your tiles look scrambled, change this number to match the 
        // number of COLUMNS in your Godot Tileset Atlas setup.
        // For example, if your atlas is a 10x6 grid, change this to 10.
        // If all your tiles are in one long row, change this to 64.
        final int ATLAS_WIDTH = 8; 

        for (String[] parts : tileData) {
            // Normalize coordinates to be 0-indexed for the Java TileMap array
            int x = Integer.parseInt(parts[1]) - minX;
            int y = Integer.parseInt(parts[2]) - minY;
            
            int atlasX = Integer.parseInt(parts[3]);
            int atlasY = Integer.parseInt(parts[4]);

            // Calculate tile index (0='A', 1='B', etc.) based on atlas grid
            int tileIndex = (atlasY * ATLAS_WIDTH) + atlasX;

            if (tileIndex >= 0 && tileIndex < tiles.size()) {
                newMap.setTile(x, y, tiles.get(tileIndex));
                
                // ASSIGN TILE TYPES BASED ON IMAGE INDEX
                // Shifted to 0-indexed to match loaded Tile_01 (index 0) to Tile_64 (index 63)
                if (tileIndex == 42) { // Tile_43
                    newMap.setTileType(x, y, TileMap.TILE_RISING_LOW);
                } else if (tileIndex == 32) { // Tile_33
                    newMap.setTileType(x, y, TileMap.TILE_RISING_HIGH);
                } else if (tileIndex == 33) { // Tile_34
                    newMap.setTileType(x, y, TileMap.TILE_FALLING_HIGH);
                } else if (tileIndex == 43) { // Tile_44
                    newMap.setTileType(x, y, TileMap.TILE_FALLING_LOW);
                } else {
                    newMap.setTileType(x, y, TileMap.TILE_SOLID);
                }
            }
        }

        return newMap;
    }


/*
    private void addSprite(TileMap map,
        Sprite hostSprite, int tileX, int tileY)
    {
        if (hostSprite != null) {
            // clone the sprite from the "host"
            Sprite sprite = (Sprite)hostSprite.clone();

            // center the sprite
            sprite.setX(
                TileMapRenderer.tilesToPixels(tileX) +
                (TileMapRenderer.tilesToPixels(1) -
                sprite.getWidth()) / 2);

            // bottom-justify the sprite
            sprite.setY(
                TileMapRenderer.tilesToPixels(tileY + 1) -
                sprite.getHeight());

            // add it to the map
            map.addSprite(sprite);
        }
    }

*/

    // -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------


    public void loadTileImages() {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ folder

	File file;

	System.out.println("loadTileImages called.");

        tiles = new ArrayList<BufferedImage>();
        int i = 1;
        while (true) {
            String filename = String.format("images/1 Tiles/Tile_%02d.png", i);
	    file = new File(filename);
            if (!file.exists()) {
                break;
            }
	    else
		System.out.println("Image file opened: " + filename);
		BufferedImage tileImage = ImageManager.loadBufferedImage(filename);
		tiles.add(tileImage);
            i++;
        }
    }

/*
    public void loadCreatureSprites() {

        Image[][] images = new Image[4][];

        // load left-facing images
        images[0] = new Image[] {
            loadImage("player1.png"),
            loadImage("player2.png"),
            loadImage("player3.png"),
            loadImage("fly1.png"),
            loadImage("fly2.png"),
            loadImage("fly3.png"),
            loadImage("grub1.png"),
            loadImage("grub2.png"),
        };

        images[1] = new Image[images[0].length];
        images[2] = new Image[images[0].length];
        images[3] = new Image[images[0].length];
        for (int i=0; i<images[0].length; i++) {
            // right-facing images
            images[1][i] = getMirrorImage(images[0][i]);
            // left-facing "dead" images
            images[2][i] = getFlippedImage(images[0][i]);
            // right-facing "dead" images
            images[3][i] = getFlippedImage(images[1][i]);
        }

        // create creature animations
        Animation[] playerAnim = new Animation[4];
        Animation[] flyAnim = new Animation[4];
        Animation[] grubAnim = new Animation[4];
        for (int i=0; i<4; i++) {
            playerAnim[i] = createPlayerAnim(
                images[i][0], images[i][1], images[i][2]);
            flyAnim[i] = createFlyAnim(
                images[i][3], images[i][4], images[i][5]);
            grubAnim[i] = createGrubAnim(
                images[i][6], images[i][7]);
        }

        // create creature sprites
        playerSprite = new Player(playerAnim[0], playerAnim[1],
            playerAnim[2], playerAnim[3]);
        flySprite = new Fly(flyAnim[0], flyAnim[1],
            flyAnim[2], flyAnim[3]);
        grubSprite = new Grub(grubAnim[0], grubAnim[1],
            grubAnim[2], grubAnim[3]);
System.out.println("loadCreatureSprites successfully executed.");

    }
*/

}
