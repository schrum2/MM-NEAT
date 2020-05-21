package icecreamyou.LodeRunner;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Provides methods to facilitate drawing images.
 */
public class Picture {
	
	public static final String FILE_PATH = "src/main/java/icecreamyou/LodeRunner/";
	
	/**
	 * Keep track of pictures that have already been drawn so that we don't have to load them every time.
	 */
	private static Map<String, BufferedImage> cache = new HashMap<String, BufferedImage>();

	/**
	 * Draw an image.
	 *
	 * @param g The graphics context in which to draw the image.
	 * @param filepath The location of the image file.
	 * @param x The x-coordinate of where the upper-left corner of the image should be drawn.
	 * @param y The y-coordinate of where the upper-left corner of the image should be drawn.
	 */
	public static void draw(Graphics g, String filepath, int x, int y) {
		try {
			BufferedImage img;
			if (cache.containsKey(filepath))
				img = cache.get(filepath);
			else {
				img = ImageIO.read(new File(FILE_PATH+filepath));
				cache.put(filepath, img);
			}
			g.drawImage(img, x, y, null);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
}
