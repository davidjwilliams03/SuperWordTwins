import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

public class Player {			

   private int DX = 36;	// amount of X pixels to move in one keystroke
   private int DY = 64;	// amount of Y pixels to move in one keystroke
   private static final int TILE_SIZE = 64;

   private JPanel panel;		// reference to the JFrame on which player is drawn
   private TileMap tileMap;
   private BackgroundManager bgManager;

   private int x;			// x-position of player's sprite
   private int y;			// y-position of player's sprite

   private int width;			// scaled width of player
   private int height;			// scaled height of player
   private Dimension dimension;

   private int minX = 0;
   private int maxX = Integer.MAX_VALUE;
   private int startPosX = 100;
   private int startPosY = 100;


   private Animation stanceRightAnim, stanceLeftAnim;
   private Animation runRightAnim, runLeftAnim;
   private Animation jumpRightAnim, jumpLeftAnim;
   private Animation climbRightAnim, climbLeftAnim;
   private Animation crouchRightAnim, crouchLeftAnim;
   private Animation introAnim, deadAnim;
   private Animation punchRightAnim, punchLeftAnim;
   private Animation kickRightAnim, kickLeftAnim;
   
   private Animation currAnim;
   
   private boolean facingRight = true;
   private boolean isIntro = true;
   private boolean isDead = false;
   private boolean isCrouching = false;
   private boolean isAttacking = false;


   private boolean jumping;
   private int timeElapsed;
   private int startY;

   private boolean goingUp;
   private boolean goingDown;

   private boolean inAir;
   private boolean wasOnGround;
   private int initialVelocity;
   private boolean isCeilingWalking = false;
   
   private int health = 100;
   private final int MAX_HEALTH = 100;
   private long lastDamageTime = 0;

   private boolean isClimbing = false;
   private int climbingSide = 0; // -1 for Left, 1 for Right
   private int awayFrames = 0;
   private int startAir;

   public Player (JPanel panel, TileMap t, BackgroundManager b) {
      this.panel = panel;

      tileMap = t;			// tile map on which the player's sprite is displayed
      bgManager = b;			// instance of BackgroundManager

      goingUp = goingDown = false;
      inAir = false;
      wasOnGround = true;


      isIntro = true;
      facingRight = true;
      isDead = false;
      isAttacking = false;
      isCrouching = false;

      // Load Animations
      stanceRightAnim = loadAnim("images/batmantwin/stance/s", 1, 7, true, 100, false);
      stanceLeftAnim = loadAnim("images/batmantwin/stance/s", 1, 7, true, 100, true);
      
      runRightAnim = loadAnim("images/batmantwin/run/r", 1, 8, true, 50, false);
      runLeftAnim = loadAnim("images/batmantwin/run/l", 1, 8, true, 50, false);
      
      jumpRightAnim = loadAnim("images/batmantwin/jump/sr", 1, 10, false, 80, false);
      jumpLeftAnim = loadAnim("images/batmantwin/jump/sl", 1, 10, false, 80, false);
      
      climbRightAnim = loadAnim("images/batmantwin/climb/c", 1, 6, false, 150, false);
      climbLeftAnim = loadAnim("images/batmantwin/climb/c", 1, 6, false, 150, true);
      
      crouchRightAnim = loadAnim("images/batmantwin/crouch/c", 1, 6, false, 80, false);
      crouchLeftAnim = loadAnim("images/batmantwin/crouch/c", 1, 6, false, 80, true);
      
      introAnim = loadAnim("images/batmantwin/intro/i", 1, 12, false, 150, false);
      deadAnim = loadAnim("images/batmantwin/dead/d", 1, 15, false, 150, false);
      
      punchRightAnim = loadAnim("images/batmantwin/punch/p", 1, 5, false, 80, false);
      punchLeftAnim = loadAnim("images/batmantwin/punch/p", 1, 5, false, 80, true);
      
      kickRightAnim = loadAnim("images/batmantwin/kick/k", 1, 4, false, 100, false);
      kickLeftAnim = loadAnim("images/batmantwin/kick/k", 1, 4, false, 100, true);

      currAnim = introAnim;
      currAnim.start();

      this.width = 54;
      this.height = 96;

   }


   public boolean isDead() { return isDead; }
   public boolean isDeathFinished() { return isDead && !deadAnim.isStillActive(); }

   private BufferedImage flipImage(BufferedImage image) {
      BufferedImage flipped = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = flipped.createGraphics();
      g.drawImage(image, image.getWidth(), 0, -image.getWidth(), image.getHeight(), null);
      g.dispose();
      return flipped;
   }

   private Animation loadAnim(String prefix, int start, int end, boolean loop, int duration, boolean flip) {
      Animation anim = new Animation(loop);
      for (int i = start; i <= end; i++) {
         BufferedImage img = ImageManager.loadBufferedImage(prefix + i + ".png");
         if (img != null) {
            if (flip) img = flipImage(img);
            anim.addFrame(img, duration);
         }
      }
      return anim;
   }

   public boolean isClimbing() { return isClimbing; }
   public int getClimbingSide() { return climbingSide; }
   public boolean isCeilingWalking() { return isCeilingWalking; }

   public void setBounds(int minX, int maxX) {
      this.minX = minX;
      this.maxX = maxX;
   }

   public void setStartPosition(int x, int y) {
      this.startPosX = x;
      this.startPosY = y;
   }

   public int getHealth() { return health; }
   public void setHealth(int health) { 
      this.health = Math.min(MAX_HEALTH, Math.max(0, health)); 
   }

   public void takeDamage(int amount) {
      // 500ms invincibility after taking damage from enemies
      if (System.currentTimeMillis() - lastDamageTime > 500) {
         this.health = Math.max(0, this.health - amount);
         lastDamageTime = System.currentTimeMillis();
      }
   }

   public void heal(int amount) {
      health += amount;
      if (health > MAX_HEALTH) health = MAX_HEALTH;
   }

   /**
    * Sensor Box: Checks a 2-pixel wide strip just outside the player's bounds.
    */
   private boolean isTouchingWall(int side) {
      int sensorX = (side == -1) ? x - 5 : x + width + 3;
      // Check top, middle, and bottom of the player's side
      return tileMap.isPixelSolid(sensorX, y + 5) || 
             tileMap.isPixelSolid(sensorX, y + height / 2) || 
             tileMap.isPixelSolid(sensorX, y + height - 5);
   }

   /**
    * Ledge Sensor: Checks if the side sensor is clear at head-height but 
    * solid at feet-height (detecting a ledge).
    */
   private boolean isAtLedge(int side) {
      int sensorX = (side == -1) ? x - 2 : x + width;
      // Head is clear, but feet/middle are still against a wall
      boolean headClear = !tileMap.isPixelSolid(sensorX, y);
      boolean bodySolid = tileMap.isPixelSolid(sensorX, y + height / 2) || 
                          tileMap.isPixelSolid(sensorX, y + height - 10);
      return headClear && bodySolid;
   }


   public Point collidesWithTile(int newX, int newY) {

      // Check points from head to feet.
      int checkHeightLimit = height; 
      for (int i = 0; i < checkHeightLimit; i += 10) {
         if (tileMap.isPixelSolid(newX, newY + i)) {
            int tx = tileMap.pixelsToTiles(newX);
            int ty = tileMap.pixelsToTiles(newY + i);
            int type = tileMap.getTileType(tx, ty);

            // JUNCTION FIX: Allow a small step-up (15px) for solid tiles to prevent sticking 
            // where slopes meet flat ground.
            if (type == TileMap.TILE_SOLID && i > height - 15) continue;
            
            // Bypass horizontal collision for slopes near the feet (bottom 40px)
            if (type >= TileMap.TILE_RISING_LOW && type <= TileMap.TILE_FALLING_LOW && i > height - 40) continue;
            
            return new Point(tx, ty);
         }
      }
      // Final check just below the "step" height
      if (tileMap.isPixelSolid(newX, newY + checkHeightLimit)) {
         return new Point(tileMap.pixelsToTiles(newX), tileMap.pixelsToTiles(newY + checkHeightLimit));
      }
      return null;
   }

   public float getSlopeY(int playerX, int tileX, int tileY, int tileType) {
      float localX = (float)playerX - (tileX * TILE_SIZE);
      localX = Math.max(0, Math.min(localX, (float)TILE_SIZE));
      float floorY = 0;
      if (tileType == TileMap.TILE_RISING_LOW) {
         floorY = 64 - (localX / 2.0f);
      } else if (tileType == TileMap.TILE_RISING_HIGH) {
         floorY = 32 - (localX / 2.0f);
      } else if (tileType == TileMap.TILE_FALLING_HIGH) {
         floorY = localX / 2.0f;
      } else if (tileType == TileMap.TILE_FALLING_LOW) {
         floorY = 32 + (localX / 2.0f);
      }
      return (float)(tileY * TILE_SIZE) + floorY;
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


   public Point collidesWithTileUp(int newX, int newY) {
      int startYTile = tileMap.pixelsToTiles(y);
      int endYTile = tileMap.pixelsToTiles(newY);
      // Head sensor points: inset side points by 10px to avoid wall-snagging
      int[] checkX = { newX + 10, newX + width / 2, newX + width - 10 };

      // Sweep check: check every tile row the head passes through during the jump
      for (int ty = startYTile; ty >= endYTile; ty--) {
         // Safety: Only detect tiles whose bottom edge is at or above the current head position.
         // This prevents downward snapping into the floor or the player's own body.
         if (tileMap.tilesToPixels(ty) + TILE_SIZE > y + 2) {
            continue;
         }

         for (int px : checkX) {
            int tx = tileMap.pixelsToTiles(px);
            if (tileMap.getTile(tx, ty) != null) {
               return new Point(tx, ty);
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

      // Release wall if moving away
      if ((isClimbing || isCeilingWalking) && direction != 3) {
         if ((climbingSide == -1 && direction == 2) || (climbingSide == 1 && direction == 1)) {
            awayFrames++;
            if (awayFrames > 5) {
               isClimbing = false;
               awayFrames = 0;
            }
         } else {
            awayFrames = 0;
         }
         // Horizontal movement is still allowed for Ceiling Walking below
      }
      
      if (direction == 1) {		// move left
          facingRight = false;
          int newX = x - DX;
	  if (newX < minX) {
		x = minX;
		return;
	  }

      Point tilePos = collidesWithTile(newX, y);
      int tileType = (tilePos != null) ? tileMap.getTileType((int)tilePos.getX(), (int)tilePos.getY()) : TileMap.TILE_EMPTY;
      if (tilePos != null && tileType == TileMap.TILE_SOLID) {
         x = ((int) tilePos.getX() + 1) * TILE_SIZE;
      } else {
         // While ceiling walking, ensure we still have a ceiling above us
         if (isCeilingWalking && collidesWithTileUp(newX, y - 2) == null) {
            return; // Don't move past the end of the ceiling
         }
         x = newX;
         bgManager.moveLeft();
      }
      }
      else if (direction == 2) {		// move right
          facingRight = true;
      	  int playerWidth = width;
          int newX = x + DX;
         int tileMapWidth = tileMap.getWidthPixels();
         int limit = Math.min(tileMapWidth, maxX);

         if (newX + width >= limit) {
            x = limit - width;
            return;
         }

         Point tilePos = collidesWithTile(newX + playerWidth, y);
         int tileType = (tilePos != null) ? tileMap.getTileType((int)tilePos.getX(), (int)tilePos.getY()) : TileMap.TILE_EMPTY;
         if (tilePos != null && tileType == TileMap.TILE_SOLID) {
            x = ((int) tilePos.getX()) * TILE_SIZE - playerWidth;
         } else {
            // While ceiling walking, ensure we still have a ceiling above us
            if (isCeilingWalking && collidesWithTileUp(newX, y - 2) == null) {
               return; // Don't move past the end of the ceiling
            }
            x = newX;
            bgManager.moveRight();
         }
      }
      else if (direction == 3) { 
          if (!jumping && !inAir) {
              jump();
          }
          return;
      }
   }


   public boolean isInAir() {
      if (!jumping && !inAir && !isClimbing && !isCeilingWalking) {   
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
      startY = y;

      if (isClimbing) {
         // Jump off wall (apply kickback)
         isClimbing = false;
         x += (climbingSide == -1) ? 20 : -20; 
         initialVelocity = 180;
      } else {
         initialVelocity = 130; // Adjusted for 64x64 tiles at 20fps physics
      }

      goingUp = true;
      goingDown = false;
   }


   public void update () {


      if (isIntro) {
         currAnim.update();
         if (!currAnim.isStillActive()) {
            isIntro = false;
            currAnim = facingRight ? stanceRightAnim : stanceLeftAnim;
            currAnim.start();
         }
         return; // lock movement
      }

      if (health <= 0) {
         if (!isDead) {
            isDead = true;
            currAnim = deadAnim;
            currAnim.start();
         }
         currAnim.update();
         return;
      }

      // Attack Logic
      if (!isAttacking && !isClimbing && !isCrouching && !jumping && !inAir) {
         if (GameWindow.isKeyPressed(KeyEvent.VK_Z)) {
            isAttacking = true;
            currAnim = facingRight ? punchRightAnim : punchLeftAnim;
            currAnim.start();
         } else if (GameWindow.isKeyPressed(KeyEvent.VK_X)) {
            isAttacking = true;
            currAnim = facingRight ? kickRightAnim : kickLeftAnim;
            currAnim.start();
         }
      }

      if (isAttacking) {
         currAnim.update();
         if (!currAnim.isStillActive()) {
            isAttacking = false;
            currAnim = facingRight ? stanceRightAnim : stanceLeftAnim;
            currAnim.start();
         }
         // Do not return here if we want physics to apply while attacking, but returning locks horizontal movement which is usually desired for ground attacks.
         return;
      }

      // Crouch Logic
      boolean downPressed = GameWindow.isKeyPressed(KeyEvent.VK_DOWN);
      if (!isClimbing && !jumping && !inAir && downPressed) {
         if (!isCrouching) {
            isCrouching = true;
            y += 48; // shrink from the top down
            height = 48;
            currAnim = facingRight ? crouchRightAnim : crouchLeftAnim;
            currAnim.start();
         }
      } else if (isCrouching) {
         // Attempt to stand up
         boolean canStand = true;
         // Check if standing up would hit the ceiling
         Point ceiling = collidesWithTileUp(x, y - 48);
         if (ceiling != null) {
            canStand = false;
         }
         
         if (canStand) {
            isCrouching = false;
            y -= 48;
            height = 96;
         } else {
            // Keep crouching
            isCrouching = true;
         }
      }
      
      // Update Animation States for Movement
      if (isClimbing) {
         Animation cAnim = (climbingSide == 1) ? climbRightAnim : climbLeftAnim;
         if (currAnim != cAnim) { currAnim = cAnim; currAnim.start(); }
      } else if (jumping || inAir) {
         Animation jAnim = facingRight ? jumpRightAnim : jumpLeftAnim;
         if (currAnim != jAnim) { currAnim = jAnim; currAnim.start(); }
      } else if (isCrouching) {
         Animation cAnim = facingRight ? crouchRightAnim : crouchLeftAnim;
         if (currAnim != cAnim) { currAnim = cAnim; currAnim.start(); }
      } else if (GameWindow.isKeyPressed(KeyEvent.VK_LEFT)) {
         if (currAnim != runLeftAnim) { currAnim = runLeftAnim; currAnim.start(); }
      } else if (GameWindow.isKeyPressed(KeyEvent.VK_RIGHT)) {
         if (currAnim != runRightAnim) { currAnim = runRightAnim; currAnim.start(); }
      } else {
         Animation sAnim = facingRight ? stanceRightAnim : stanceLeftAnim;
         if (currAnim != sAnim) { currAnim = sAnim; currAnim.start(); }
      }

      currAnim.update();

      // Entrance check: Trigger if holding Up while against a wall (checked every frame)
      if (!isClimbing && GameWindow.isKeyPressed(KeyEvent.VK_UP)) {
         if (isTouchingWall(-1)) {
            isClimbing = true;
            climbingSide = -1;
            facingRight = false;
            jumping = inAir = goingUp = goingDown = false;
         } else if (isTouchingWall(1)) {
            isClimbing = true;
            climbingSide = 1;
            facingRight = true;
            jumping = inAir = goingUp = goingDown = false;
         }
      }

      if (isClimbing) {
         int climbSpeed = 8;
         if (GameWindow.isKeyPressed(KeyEvent.VK_UP)) {
            int nextY = y - climbSpeed;
            // Check if head hits a solid part of a tile (including slopes)
            if (!tileMap.isPixelSolid(x + width / 2, nextY)) {
               y = nextY;
            }
            
            // Mantle / Ledge Move
            if (isAtLedge(climbingSide)) {
               y -= 32; // Lift up
               x += (climbingSide == -1) ? -20 : 20; // Push onto surface
               isClimbing = false;
            }
         }
         if (GameWindow.isKeyPressed(KeyEvent.VK_DOWN)) {
            int nextY = y + climbSpeed;
            // Check if feet hit a solid part of a tile
            if (!tileMap.isPixelSolid(x + width / 2, nextY + height)) {
               y = nextY;
            }
            // Fall off if we hit the floor
            if (tileMap.isPixelSolid(x + width/2, y + height + 1)) {
               isClimbing = false;
            }
         }

         // Safety check: if wall disappears, stop climbing
         if (!isTouchingWall(climbingSide)) {
            isClimbing = false;
         }
         return; // Bypass normal physics
      }

      // RESET LOGIC (Death Plane)
      if (y > tileMap.getHeightPixels() + 200) {
         x = startPosX; y = startPosY; // Reset to level start
         fall();
      }

      // CEILING STICKING LOGIC (Only trigger if jumping or in air)
      if (GameWindow.isKeyPressed(KeyEvent.VK_SPACE) && (jumping || inAir)) {
         Point headTile = collidesWithTileUp(x, y - 2);
         if (headTile != null) {
            isCeilingWalking = true;
            jumping = inAir = goingUp = goingDown = false;
            y = (int)(headTile.getY() + 1) * TILE_SIZE; // Snap to ceiling
         }
      } else {
         isCeilingWalking = false;
      }

      if (isCeilingWalking) {
         if (collidesWithTileUp(x, y - 2) == null) isCeilingWalking = false;
         else return; // Stay on ceiling
      }

      int distance = 0;
      int newY = 0;

      // SLOPE HANDLING & SNAPPING (Moved from move() to update() to ensure constant physics)
      int leftX = x + 4;
      int centerX = x + width / 2;
      int rightX = x + width - 4;
      int footY = y + height;

      // Increased searchRange to 24 to catch steep drop-offs when DX=36
      // Increased upwardSearch to -32 to catch deep penetrations when moving uphill at DX=36
      int searchRange = (wasOnGround && !jumping) ? 24 : 0; 
      int upwardSearch = (goingUp || jumping) ? 0 : -32; 

      boolean onSlope = false;
      int[] samplePoints = { leftX, centerX, rightX };

      // Prevent snapping to the floor if the player is actively jumping up
      if (!goingUp && !jumping) {
          for (int px : samplePoints) {
              for (int i = upwardSearch; i <= searchRange; i++) {
                  int tx = tileMap.pixelsToTiles(px);
                  int ty = tileMap.pixelsToTiles(footY + i - 1);
                  int type = tileMap.getTileType(tx, ty);

                  if (type >= TileMap.TILE_RISING_LOW && type <= TileMap.TILE_FALLING_LOW) {
                      float surfaceY = getSlopeY(px, tx, ty, type);
                      // Removed the Math.abs < 10 check because DX=36 causes penetrations
                      // deeper than 10 pixels, which was preventing the snap from triggering uphill.
                      if (footY + i >= surfaceY) {
                          y = (int) (surfaceY - height);
                          jumping = false;
                          inAir = false;
                          goingUp = goingDown = false;
                          timeElapsed = 0; 
                          onSlope = true;
                          break;
                      }
                  }
              }
              if (onSlope) break;
          }
      }

      boolean isOnGround = tileMap.isPixelSolid(x, y + height + 1) || 
                           tileMap.isPixelSolid(x + width / 2, y + height + 1) || 
                           tileMap.isPixelSolid(x + width - 1, y + height + 1);

      if (!onSlope && !isOnGround && !jumping && !inAir && !isClimbing) {
          fall();
      }
      wasOnGround = isOnGround || onSlope;

      timeElapsed++;

      if (jumping || inAir) {
	   distance = (int) (initialVelocity * timeElapsed - 
                             9.8 * timeElapsed * timeElapsed);
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

   public int getdx() {
      return DX;
   }

   public int getdy() {
      return DY;
   }

   public void setdx(int dx){
      this.DX = dx;
   }

   public void setdy(int dy){
      this.DY = dy;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public int getWidth() {
      return width;
   }

   public int getHeight() {
      return height;
   }


   public Image getImage() {
      if (currAnim != null && currAnim.getImage() != null) {
         return currAnim.getImage();
      }
      return null;
   }



   public Rectangle2D.Double getBoundingRectangle() {
      int playerWidth = width;
      int playerHeight = height;

      return new Rectangle2D.Double (x, y, playerWidth, playerHeight);
   }

}