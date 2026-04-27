import java.awt.Graphics2D;
import java.awt.Image;

public class SpinningBlade {

    private int x, y;
    private double angle = 0;
    private double speed = 0.4;

    private Image bladeImage;

    private int width = 256;
    private int height = 256;

    public SpinningBlade(int x, int y, Image img) {
        this.x = x;
        this.y = y;
        this.bladeImage = img;
    }

    public void update() {
        angle += speed;
    }

    public void draw(Graphics2D g2, int offsetX, int offsetY) {
        int drawX = x + offsetX;
        int drawY = y + offsetY;

        g2.rotate(angle, drawX + width/2, drawY + height/2);
        g2.drawImage(bladeImage, drawX, drawY, width, height, null);
        g2.rotate(-angle, drawX + width/2, drawY + height/2);
    }

    public boolean collidesWith(Player player) {
        // Use a slightly smaller collision box for blades to feel fairer to the player
        return player.getBoundingRectangle().intersects(
            x + 10, y + 10, width - 20, height - 20
        );
    }
}