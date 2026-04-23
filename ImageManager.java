import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
   The ImageManager class manages the loading and processing of images.
*/

public class ImageManager {
    private static final Map<String, Image> imageCache = new HashMap<>();
    private static final Map<String, BufferedImage> bufferedImageCache = new HashMap<>();

	public static Image loadImage (String fileName) {
		if (imageCache.containsKey(fileName)) {
			return imageCache.get(fileName);
		}
		Image img = new ImageIcon(fileName).getImage();
		imageCache.put(fileName, img);
		return img;
	}

	public static BufferedImage loadBufferedImage(String filename) {
		if (bufferedImageCache.containsKey(filename)) {
			return bufferedImageCache.get(filename);
		}
		BufferedImage bi = null;

		File file = new File (filename);
		try {
			bi = ImageIO.read(file);
		}
		catch (IOException ioe) {
			System.out.println ("Error opening file " + filename + ":" + ioe);
		}
		bufferedImageCache.put(filename, bi);
		return bi;
	}


  	// make a copy of the BufferedImage src

	public static BufferedImage copyImage(BufferedImage src) {
		if (src == null)
			return null;


		int imWidth = src.getWidth();
		int imHeight = src.getHeight();

		BufferedImage copy = new BufferedImage (imWidth, imHeight,
							BufferedImage.TYPE_INT_ARGB);

    		Graphics2D g2d = copy.createGraphics();

    		// copy image
    		g2d.drawImage(src, 0, 0, null);
    		g2d.dispose();

    		return copy; 
	  }

}
