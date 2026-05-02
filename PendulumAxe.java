import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

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
        // The axe rotates around the pivotX, pivotY point
        // Save transform
        AffineTransform old = g2.getTransform();
        
        // Move to pivot and rotate
        g2.translate(pivotX + offsetX, pivotY + offsetY);
        g2.rotate(-angle);
        
        // Draw image offset by 'length' to simulate a chain/handle
        // The handle starts at 'length' and the blade is at 'length + axeHeight'
        g2.drawImage(axeImage, -axeWidth/2, length, axeWidth, axeHeight, null);
        
        // Restore transform
        g2.setTransform(old);
    }

    public boolean collidesWith(Player player) {
        // Calculate the actual world position of the blade
        // The blade center is roughly in the middle of the axe sprite (axeHeight/2)
        // plus the length of the pendulum
        double bladeDist = length + (axeHeight / 2) + 150; // Adjusted for this specific sprite
        
        int bX = pivotX + (int)(bladeDist * Math.sin(angle));
        int bY = pivotY + (int)(bladeDist * Math.cos(angle));
        
        // Create a collision box around the calculated blade center
        // Larger area (168x132)
        Rectangle2D.Double bladeBox = new Rectangle2D.Double(bX - 84, bY - 66, 168, 132);
        
        return player.getBoundingRectangle().intersects(bladeBox);
    }
}