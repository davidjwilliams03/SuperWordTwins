import java.awt.Image;

public class Goon extends Enemy {
    private int dx = 4;
    private int startX;
    private int walkRange = 256;

    public Goon(int x, int y, Image image, TileMap tileMap) {
        super(x, y, 64, 64, image, tileMap);
        this.startX = x;
    }

    @Override
    public void update() {
        x += dx;
        if (Math.abs(x - startX) > walkRange) {
            dx = -dx;
        }
    }
}