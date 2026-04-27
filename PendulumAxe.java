import java.awt.Graphics2D;
import java.awt.Image;

public class PendulumAxe {

    private int pivotX, pivotY;
    private int length;

    private double angle = 0;
    private double time = 0;

    private double amplitude = Math.toRadians(120); // swing range
    private double speed = 0.05;

    private Image axeImage;

    private int axeWidth = 512;
    private int axeHeight = 512;

    public PendulumAxe(int pivotX, int pivotY, int length, Image img) {
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.length = length;
        this.axeImage = img;
    }

    public void update() {
        time += 1;
        angle = Math.sin(time * speed) * amplitude;
    }

    public void draw(Graphics2D g2, int offsetX, int offsetY) {

        int axeX = pivotX + (int)(length * Math.sin(angle));
        int axeY = pivotY + (int)(length * Math.cos(angle));

        // Draw chain (line)
//        g2.drawLine(
//           pivotX + offsetX, pivotY + offsetY,
//            axeX + offsetX, axeY + offsetY
//        );

        // Rotate axe around its center
        g2.rotate(-angle, axeX + offsetX + axeWidth/2, axeY + offsetY + axeHeight/2);

        g2.drawImage(axeImage,
            axeX + offsetX,
            axeY + offsetY,
            axeWidth,
            axeHeight,
            null
        );

        // Reset rotation
        g2.rotate(angle, axeX + offsetX + axeWidth/2, axeY + offsetY + axeHeight/2);
    }

    public boolean collidesWith(Player player) {
        int axeX = pivotX + (int)(length * Math.sin(angle));
        int axeY = pivotY + (int)(length * Math.cos(angle));

        return player.getBoundingRectangle().intersects(axeX, axeY, (axeWidth - 100), (axeHeight - 50));
    }
}