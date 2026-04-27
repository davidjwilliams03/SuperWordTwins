import java.awt.Graphics2D;
import java.awt.Image;

public class GroundSpike {

    private int x, y;
    private Image spikeImage;
    private int width = 64;
    private int height = 64;

    public GroundSpike(int x, int y, Image img) {
        this.x = x;
        this.y = y;
        this.spikeImage = img;
    }

    public void draw(Graphics2D g2, int offsetX, int offsetY) {
        g2.drawImage(spikeImage, x + offsetX, y + offsetY, width, height, null);
    }

    public boolean collidesWith(Player player) {
        // Spikes usually only hurt if the player's feet touch the bottom half
        return player.getBoundingRectangle().intersects(
            x + 5, y + 32, width - 10, 32
        );
    }
}