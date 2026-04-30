import java.awt.Graphics2D;
import java.awt.Image;

public abstract class Enemy {
    protected int x, y, width, height;
    protected Image image;
    protected TileMap tileMap;

    public Enemy(int x, int y, int width, int height, Image image, TileMap tileMap) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        this.tileMap = tileMap;
    }

    public abstract void update();

    public void draw(Graphics2D g2, int offsetX, int offsetY) {
        g2.drawImage(image, x + offsetX, y + offsetY, width, height, null);
    }

    public boolean collidesWith(Player player) {
        return player.getBoundingRectangle().intersects(x, y, width, height);
    }
}