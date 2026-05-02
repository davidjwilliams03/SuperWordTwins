import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Enemy {
    public enum Type { PENGUIN, JOKER, HARLEY, RIDDLER, REDHOOD, KILLERCROC }

    private int territoryMinX = -1, territoryMaxX = -1, territoryMinY = -1, territoryMaxY = -1;
    private Type type;
    private int x, y, width, height;
    private int startX, startY;
    private int health = 5;
    private boolean isDead = false;
    private boolean isVisible = true;
    private boolean isFacingRight = true;

    private Animation walkAnim;
    private Animation deathAnim;
    private Animation currAnim;

    private TileMap tileMap;
    private Player player;
    private double speed;

    // Movement variables
    private double time = 0;
    private double bezierT = 1.0; // 1.0 means reached destination
    private int p0x, p0y, p1x, p1y, p2x, p2y; // Bezier points
    
    // Physics for Red Hood
    private float dy = 0;
    private boolean inAir = true;
    private long lastDamageTime = 0;

    public Enemy(Type type, int x, int y, TileMap tileMap, Player player) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.startX = x;
        this.startY = y;
        this.tileMap = tileMap;
        this.player = player;
        
        // Killer Croc, Harley, and Riddler are 1.5x larger
        this.width = (type == Type.KILLERCROC || type == Type.HARLEY || type == Type.RIDDLER) ? 144 : 96;
        this.height = (type == Type.KILLERCROC || type == Type.HARLEY || type == Type.RIDDLER) ? 144 : 96;

        this.speed = (type == Type.REDHOOD) ? 36 : 12; // Adjusted speed for better control
        
        loadAnimations();
        currAnim = walkAnim;
        currAnim.start();
    }

    public void setTerritory(int minX, int maxX, int minY, int maxY) {
        this.territoryMinX = minX;
        this.territoryMaxX = maxX;
        this.territoryMinY = minY;
        this.territoryMaxY = maxY;
    }

    private void loadAnimations() {
        String path = "";
        int frames = 0;
        char prefix = ' ';
        boolean flip = false;

        switch (type) {
            case PENGUIN: path = "images/penguin/p"; frames = 6; prefix = 'p'; break;
            case JOKER: path = "images/joker/j"; frames = 6; prefix = 'j'; break;
            case HARLEY: path = "images/harley/h"; frames = 8; prefix = 'h'; break;
            case RIDDLER: path = "images/riddler/r"; frames = 9; prefix = 'r'; break;
            case REDHOOD: path = "images/red hood/r"; frames = 10; prefix = 'r'; break;
            case KILLERCROC: path = "images/killer croc/k"; frames = 8; prefix = 'k'; break;
        }

        walkAnim = loadAnim(path, 1, frames, true, 100, false);
        deathAnim = loadAnim("images/enemydeath/d", 1, 15, false, 80, false);
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

    private BufferedImage flipImage(BufferedImage image) {
        BufferedImage flipped = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = flipped.createGraphics();
        g.drawImage(image, image.getWidth(), 0, -image.getWidth(), image.getHeight(), null);
        g.dispose();
        return flipped;
    }

    public void update() {
        if (!isVisible) return;

        if (isDead) {
            currAnim.update();
            if (!currAnim.isStillActive()) isVisible = false;
            return;
        }

        currAnim.update();
        move();
        checkCollision();
    }

    private void move() {
        int playerX = player.getX();
        int playerY = player.getY();
        int nextX = x;
        int nextY = y;

        switch (type) {
            case PENGUIN:
                // Sine wave movement (Horizontal chase with vertical oscillation)
                if (Math.abs(playerX - x) > 10) {
                    nextX += (playerX > x) ? speed : -speed;
                }
                time += 0.05;
                if (territoryMinY != -1 && territoryMaxY != -1) {
                    int midY = (territoryMinY + territoryMaxY - height) / 2;
                    int amp = (territoryMaxY - territoryMinY - height) / 2;
                    nextY = midY + (int)(Math.sin(time) * amp);
                } else {
                    nextY = 4111 + (int)(Math.sin(time) * 140);
                }
                break;

            case JOKER:
            case HARLEY:
            case RIDDLER:
                // Simple X-axis chase
                if (Math.abs(playerX - x) > 10) {
                    nextX += (playerX > x) ? speed : -speed;
                }
                break;

            case REDHOOD:
                // Follow player everywhere, obey tile limits
                updateRedHood();
                return; // RedHood handles its own movement logic

            case KILLERCROC:
                // Bezier movement
                updateBezierMovement();
                return; // Bezier handles its own movement logic
        }
        
        applyMovement(nextX, nextY);
        isFacingRight = (playerX > x);
    }

    private void applyMovement(int nextX, int nextY) {
        // Enforce territory boundaries (except for Red Hood)
        if (type != Type.REDHOOD) {
            if (territoryMinX != -1 && nextX < territoryMinX) nextX = territoryMinX;
            if (territoryMaxX != -1 && nextX > territoryMaxX - width) nextX = territoryMaxX - width;
            if (territoryMinY != -1 && nextY < territoryMinY) nextY = territoryMinY;
            if (territoryMaxY != -1 && nextY > territoryMaxY - height) nextY = territoryMaxY - height;
        }

        // TileMap Collision check
        if (!tileMap.isPixelSolid(nextX, nextY) && 
            !tileMap.isPixelSolid(nextX + width, nextY) &&
            !tileMap.isPixelSolid(nextX, nextY + height - 1) && 
            !tileMap.isPixelSolid(nextX + width, nextY + height - 1)) {
            x = nextX;
            y = nextY;
        } else {
            // If horizontal movement blocked, try vertical only
            if (!tileMap.isPixelSolid(x, nextY) && !tileMap.isPixelSolid(x + width, nextY)) {
                y = nextY;
            }
            // If vertical blocked, try horizontal only
            else if (!tileMap.isPixelSolid(nextX, y) && !tileMap.isPixelSolid(nextX + width, y)) {
                x = nextX;
            }
        }
    }

    private void updateRedHood() {
        int targetX = player.getX();
        int targetY = player.getY();
        
        int moveX = 0;
        if (Math.abs(targetX - x) > 5) {
            moveX = (targetX > x) ? (int)speed : (int)-speed;
        }

        // Horizontal collision
        if (!tileMap.isPixelSolid(x + moveX, y) && !tileMap.isPixelSolid(x + moveX + width, y) &&
            !tileMap.isPixelSolid(x + moveX, y + height - 1) && !tileMap.isPixelSolid(x + moveX + width, y + height - 1)) {
            x += moveX;
        }

        // Gravity/Vertical
        dy += 2.0f; // gravity
        int nextY = y + (int)dy;
        
        if (dy > 0) { // falling
            if (tileMap.isPixelSolid(x + width/2, nextY + height)) {
                y = (nextY / 64) * 64 + (64 - height);
                dy = 0;
                inAir = false;
                // Jump if player is significantly higher
                if (targetY < y - 100) {
                    dy = -35;
                    inAir = true;
                }
            } else {
                y = nextY;
                inAir = true;
            }
        } else { // jumping
            if (tileMap.isPixelSolid(x + width/2, nextY)) {
                dy = 0;
            } else {
                y = nextY;
            }
        }
        isFacingRight = (targetX > x);
    }

    private void updateBezierMovement() {
        if (bezierT >= 1.0) {
            p0x = x; p0y = y;
            p2x = player.getX(); p2y = player.getY();
            p1x = (p0x + p2x) / 2;
            p1y = Math.min(p0y, p2y) - 200;
            bezierT = 0;
        }

        bezierT += 0.02;
        double t = bezierT;
        int nextX = (int)((1-t)*(1-t)*p0x + 2*(1-t)*t*p1x + t*t*p2x);
        int nextY = (int)((1-t)*(1-t)*p0y + 2*(1-t)*t*p1y + t*t*p2y);
        
        applyMovement(nextX, nextY);
        isFacingRight = (player.getX() > x);
    }

    private void checkCollision() {
        if (player.getBoundingRectangle().intersects(x + 10, y + 10, width - 20, height - 20)) {
            if (player.isAttacking()) {
                takeDamage(1);
                x += (player.getX() > x) ? -50 : 50;
            } else {
                player.takeDamage(10);
                x += (player.getX() > x) ? -50 : 50;
            }
        }
    }

    public void takeDamage(int amount) {
        if (System.currentTimeMillis() - lastDamageTime < 500) return;
        health -= amount;
        lastDamageTime = System.currentTimeMillis();
        if (health <= 0) {
            isDead = true;
            currAnim = deathAnim;
            currAnim.start();
        }
    }

    public void draw(Graphics2D g2, int offsetX, int offsetY) {
        if (!isVisible) return;
        Image img = currAnim.getImage();
        if (img != null) {
            // Harley and Riddler point the wrong way originally, so we flip the facing logic for them
            boolean shouldFlip = !isFacingRight;
            if (type == Type.HARLEY || type == Type.RIDDLER) {
                shouldFlip = isFacingRight;
            }

            if (shouldFlip) {
                g2.drawImage(img, x + offsetX + width, y + offsetY, -width, height, null);
            } else {
                g2.drawImage(img, x + offsetX, y + offsetY, width, height, null);
            }
        }
    }

    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x, y, width, height);
    }
}