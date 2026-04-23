import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

public class Player {			

   private static final int DX = 18;	// amount of X pixels to move in one keystroke
   private static final int DY = 32;	// amount of Y pixels to move in one keystroke

   private static final int TILE_SIZE = 64;

   private JPanel panel;		// reference to the JFrame on which player is drawn
   private TileMap tileMap;
   private BackgroundManager bgManager;

   private int x;			// x-position of player's sprite
   private int y;			// y-position of player's sprite

   private int width;			// scaled width of player
   private int height;			// scaled height of player
   Graphics2D g2;
   private Dimension dimension;

   private Image playerImage, playerLeftImage, playerRightImage;

   private boolean jumping;
   private int timeElapsed;
   private int startY;

   private boolean goingUp;
   private boolean goingDown;

   private boolean inAir;
   private int initialVelocity;
   private int startAir;

   public Player (JPanel panel, TileMap t, BackgroundManager b) {
      this.panel = panel;

      tileMap = t;			// tile map on which the player's sprite is displayed
      bgManager = b;			// instance of BackgroundManager

      goingUp = goingDown = false;
      inAir = false;

      playerLeftImage = ImageManager.loadImage("images/playerLeft.gif");
      playerRightImage = ImageManager.loadImage("images/playerRight.gif");
      playerImage = playerRightImage;
      
      this.width = (int)(playerImage.getWidth(null) * 1.5);
      this.height = (int)(playerImage.getHeight(null) * 1.5);
   }


   public Point collidesWithTile(int newX, int newY) {

      // Check multiple points along the player's height, but skip the bottom 
      // section (feet) so the player can enter slope tiles horizontally.
      int checkHeightLimit = height - 16; 
      for (int i = 0; i < checkHeightLimit; i += 10) {
         if (tileMap.isPixelSolid(newX, newY + i)) {
            return new Point(tileMap.pixelsToTiles(newX), tileMap.pixelsToTiles(newY + i));
         }
      }
      // Final check just below the "step" height
      if (tileMap.isPixelSolid(newX, newY + checkHeightLimit)) {
         return new Point(tileMap.pixelsToTiles(newX), tileMap.pixelsToTiles(newY + checkHeightLimit));
      }
      return null;
   }

   public float getSlopeY(int playerX, int tileX, int tileY, int tileType) {
      float xLocal = (float)playerX - (tileX * TILE_SIZE);
      float yOffset = 0;
      if (tileType == TileMap.TILE_SLOPE_UP) {
         yOffset = (float)TILE_SIZE - xLocal; 
      } else if (tileType == TileMap.TILE_SLOPE_DOWN) {
         yOffset = xLocal;
      }
      return (float)(tileY * TILE_SIZE) + yOffset;
   }


   public Point collidesWithTileDown (int newX, int newY) {

	  int playerWidth = width;
      int playerHeight = height;
      
      // Check three points at the bottom: left corner, center, and right corner
      if (tileMap.isPixelSolid(newX, newY + playerHeight)) {
          return new Point(tileMap.pixelsToTiles(newX), tileMap.pixelsToTiles(newY + playerHeight));
      }
      if (tileMap.isPixelSolid(newX + playerWidth / 2, newY + playerHeight)) {
          return new Point(tileMap.pixelsToTiles(newX + playerWidth / 2), tileMap.pixelsToTiles(newY + playerHeight));
      }
      if (tileMap.isPixelSolid(newX + playerWidth - 1, newY + playerHeight)) {
          return new Point(tileMap.pixelsToTiles(newX + playerWidth - 1), tileMap.pixelsToTiles(newY + playerHeight));
      }
      return null;
   }


   public Point collidesWithTileUp (int newX, int newY) {

	  int playerWidth = width;

	  int xTile = tileMap.pixelsToTiles(newX);

	  int yTileFrom = tileMap.pixelsToTiles(y);
	  int yTileTo = tileMap.pixelsToTiles(newY);
	 
	  for (int yTile=yTileFrom; yTile>=yTileTo; yTile--) {
		if (tileMap.getTile(xTile, yTile) != null) {
	        	Point tilePos = new Point (xTile, yTile);
	  		return tilePos;
		}
		else {
			if (tileMap.getTile(xTile+1, yTile) != null) {
				int leftSide = (xTile + 1) * TILE_SIZE;
				if (newX + playerWidth > leftSide) {
				    Point tilePos = new Point (xTile+1, yTile);
				    return tilePos;
			        }
			}
		}
				    
	  }

	  return null;
   }
 
/*

   public Point collidesWithTile(int newX, int newY) {

	 int playerWidth = playerImage.getWidth(null);
	 int playerHeight = playerImage.getHeight(null);

      	 int fromX = Math.min (x, newX);
	 int fromY = Math.min (y, newY);
	 int toX = Math.max (x, newX);
	 int toY = Math.max (y, newY);

	 int fromTileX = tileMap.pixelsToTiles (fromX);
	 int fromTileY = tileMap.pixelsToTiles (fromY);
	 int toTileX = tileMap.pixelsToTiles (toX + playerWidth - 1);
	 int toTileY = tileMap.pixelsToTiles (toY + playerHeight - 1);

	 for (int x=fromTileX; x<=toTileX; x++) {
		for (int y=fromTileY; y<=toTileY; y++) {
			if (tileMap.getTile(x, y) != null) {
				Point tilePos = new Point (x, y);
				return tilePos;
			}
		}
	 }
	
	 return null;
   }
*/


   public synchronized void move (int direction) {

      if (!panel.isVisible ()) return;
      
      if (direction == 1) {		// move left
          playerImage = playerLeftImage;
          int newX = x - DX;
	  if (newX < 0) {
		x = 0;
		return;
	  }

      Point tilePos = collidesWithTile(newX, y);
      // Only block horizontal movement if we hit a TILE_SOLID (not a slope)
      if (tilePos != null && tileMap.getTileType((int)tilePos.getX(), (int)tilePos.getY()) == TileMap.TILE_SOLID) {
         x = ((int) tilePos.getX() + 1) * TILE_SIZE;
      } else {
         x = newX;
         bgManager.moveLeft();
      }
      }
      else if (direction == 2) {		// move right
          playerImage = playerRightImage;
      	  int playerWidth = width;
          int newX = x + DX;
         int tileMapWidth = tileMap.getWidthPixels();

         if (newX + width >= tileMapWidth) {
            x = tileMapWidth - width;
            return;
         }

         Point tilePos = collidesWithTile(newX + playerWidth, y);
         // Only block horizontal movement if we hit a TILE_SOLID (not a slope)
         if (tilePos != null && tileMap.getTileType((int)tilePos.getX(), (int)tilePos.getY()) == TileMap.TILE_SOLID) {
            x = ((int) tilePos.getX()) * TILE_SIZE - playerWidth;
         } else {
            x = newX;
            bgManager.moveRight();
         }
      }
      else if (direction == 3 && !jumping) {	
          jump();
          return;
      }

      // SLOPE HANDLING: Height Map Resolution
      if (!jumping && !inAir) {
          int centerX = x + width / 2;
          int footY = y + height;
          int tileX = tileMap.pixelsToTiles(centerX);
          int tileY = tileMap.pixelsToTiles(footY - 1);
          int type = tileMap.getTileType(tileX, tileY);

          if (type == TileMap.TILE_SLOPE_UP || type == TileMap.TILE_SLOPE_DOWN) {
              float surfaceY = getSlopeY(centerX, tileX, tileY, type);
              y = (int) (surfaceY - height);
          }
      }

      if (isInAir()) {
          fall();
      }
   }


   public boolean isInAir() {
      if (!jumping && !inAir) {   
          // Player is NOT in air if any point under their feet (left, center, right) is solid
          boolean onGround = tileMap.isPixelSolid(x, y + height + 1) || 
                            tileMap.isPixelSolid(x + width / 2, y + height + 1) || 
                            tileMap.isPixelSolid(x + width - 1, y + height + 1);
          
          return !onGround;
      }
      return false;
   }


   private void fall() {

      jumping = false;
      inAir = true;
      timeElapsed = 0;

      goingUp = false;
      goingDown = true;

      startY = y;
      initialVelocity = 0;
   }


   public void jump () {  

      if (!panel.isVisible ()) return;

      jumping = true;
      timeElapsed = 0;

      goingUp = true;
      goingDown = false;

      startY = y;
      initialVelocity = 100;
   }


   public void update () {
      int distance = 0;
      int newY = 0;

      timeElapsed++;

      if (jumping || inAir) {
	   distance = (int) (initialVelocity * timeElapsed - 
                             4.9 * timeElapsed * timeElapsed);
	   newY = startY - distance;

	   if (newY > y && goingUp) {
		goingUp = false;
 	  	goingDown = true;
	   }

	   if (goingUp) {
		Point tilePos = collidesWithTileUp (x, newY);	
	   	if (tilePos != null) {				// hits a tile going up
		   	System.out.println ("Jumping: Collision Going Up!");

			int topTileY = ((int) tilePos.getY()) * TILE_SIZE;
			int bottomTileY = topTileY + TILE_SIZE;

		   	y = bottomTileY;
		   	fall();
		}
	   	else {
			y = newY;
			System.out.println ("Jumping: No collision.");

			// the following if-statement is to pause the jump to capture the screen

/*
			if (x > 1608 && y < 300) {
				try {
					Thread.sleep (1000);
				}
				catch (Exception e) {
					System.out.println ("ERROR! " + e);
				}
			}
*/
	   	}
            }
	    else
	    if (goingDown) {			
            boolean hitGround = false;
            // Sweep collision check: iterate from current y to newY to catch intermediate platforms
            for (int tempY = y; tempY <= newY; tempY++) {
                Point tilePos = collidesWithTileDown(x, tempY);
                if (tilePos != null) {
                    int playerHeight = height;
                    
                    // Fine-tune Y position: step up pixel-by-pixel until not colliding
                    int adjustedY = tempY;
                    while (tileMap.isPixelSolid(x, adjustedY + playerHeight) || 
                           tileMap.isPixelSolid(x + width / 2, adjustedY + playerHeight) || 
                           tileMap.isPixelSolid(x + width - 1, adjustedY + playerHeight)) {
                        adjustedY--;
                    }

                    y = adjustedY;
                    goingDown = false;
                    jumping = false;
                    inAir = false;
                    hitGround = true;
                    break;
                }
            }
            if (!hitGround) {
                y = newY;
            }
        }
      }
   }


   public void moveUp () {

      if (!panel.isVisible ()) return;

      y = y - DY;
   }


   public int getX() {
      return x;
   }


   public void setX(int x) {
      this.x = x;
   }


   public int getY() {
      return y;
   }


   public void setY(int y) {
      this.y = y;
   }


   public int getWidth() {
      return width;
   }

   public int getHeight() {
      return height;
   }

   public Image getImage() {
      return playerImage;
   }


   public Rectangle2D.Double getBoundingRectangle() {
      int playerWidth = width;
      int playerHeight = height;

      return new Rectangle2D.Double (x, y, playerWidth, playerHeight);
   }

}