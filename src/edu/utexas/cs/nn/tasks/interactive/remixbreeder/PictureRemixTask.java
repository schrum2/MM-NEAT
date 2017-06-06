package edu.utexas.cs.nn.tasks.interactive.remixbreeder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.interactive.picbreeder.PicbreederTask;
import edu.utexas.cs.nn.util.graphics.GraphicsUtil;

public class PictureRemixTask<T extends Network> extends PicbreederTask<T> {
	
	public static final int CPPN_NUM_INPUTS	= 7;
	
	public String inputImage;
	public int imageHeight;
	public int imageWidth;
	
	private BufferedImage img = null;

	public PictureRemixTask() throws IllegalAccessException {
		this(Parameters.parameters.stringParameter("matchImageFile"));	
		
	}
	
	//do I need to use ImageIO and read in the image to access the height and width?
	
	public PictureRemixTask(String filename) throws IllegalAccessException{
		try {// throws and exception if filename is not valid
			img = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("Could not load image: " + filename);
			System.exit(1);
		}
		imageHeight = img.getHeight();
		imageWidth = img.getWidth();
	}
	
	@Override
	public String[] sensorLabels() {
		return new String[]{"X-coordinate", "Y-coordinate", "distance from center", "Picture H", "Picture S", "Picture V", "bias"};
	}
	
	@Override
	protected String getWindowTitle() {
		return "PictureRemix";
	}
	
	@Override
	protected BufferedImage getButtonImage(Network phenotype, int width, int height, double[] inputMultipliers) {
		 return GraphicsUtil.remixedImageFromCPPN(phenotype, img, inputMultipliers);
	}
	
	@Override
	public int numCPPNInputs() {
		return CPPN_NUM_INPUTS;
	}

	

}
