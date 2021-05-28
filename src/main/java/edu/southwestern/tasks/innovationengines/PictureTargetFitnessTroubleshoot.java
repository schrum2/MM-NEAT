package edu.southwestern.tasks.innovationengines;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;

/**
 * Creates an image that reflects the difference between each
 * pixel in the observed image and the target image.  The image
 * produced will essentially be a heat map which displays the 
 * redisual values of each pixel.
 * 
 * @author Anna Wicker
 *
 */
public class PictureTargetFitnessTroubleshoot {
	
	private static final int DISPLAY_SIDE_LENGTH = 800;
	public static final int SIDE_LENGTH = 64;
	public static final String IMAGE_MATCH_PATH = "data" + File.separator + "imagematch";

	public BufferedImage observed;
	public BufferedImage target;
	
		
	/**
	 * Creates the heatmap showing the degree of difference 
	 * between each pixel in the observed and target image.
	 * 
	 * @param observed
	 * @param target
	 */
	public static BufferedImage heatmapConstruction(BufferedImage observed, BufferedImage target) {
		
		assert observed.getWidth() == target.getWidth() && observed.getHeight() == target.getHeight(): "Image dimensions need to match";
		
		BufferedImage differenceRGB = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
		
		for(int x = 0; x < observed.getWidth(); x++) {
			for(int y = 0; y < observed.getHeight(); y++) {
				
				float observedBrightness = GraphicsUtil.getHSB(observed, x, y)[GraphicsUtil.BRIGHTNESS_INDEX];
				float targetBrightness = GraphicsUtil.getHSB(target, x, y)[GraphicsUtil.BRIGHTNESS_INDEX];
				
				double degreeOfDifference = PictureTargetTask.degreeOfDifference(observedBrightness, targetBrightness);
				
				
				
				System.out.println(x + "," + y +":" + degreeOfDifference);
				Color c = new Color((float) degreeOfDifference, 0, 0);
				differenceRGB.setRGB(x, y, c.getRGB());
			}
		}
		
		// Calculate and print Woolley fitness
		
		
		return differenceRGB;
	}
	
	
	public static void main(String[] args) throws NoSuchMethodException, IOException {
		
		BufferedImage skull = ImageIO.read(new File(IMAGE_MATCH_PATH + File.separator + "failedskull.jpg"));
		BufferedImage targetSkull = ImageIO.read(new File(IMAGE_MATCH_PATH + File.separator + "skull64.jpg"));
		
		
		//ImageIO.read(new File(IMAGE_MATCH_PATH + File.separator + filename));
		BufferedImage differenceRGB = heatmapConstruction(skull, targetSkull);
		Image img = differenceRGB.getScaledInstance(DISPLAY_SIDE_LENGTH, DISPLAY_SIDE_LENGTH, java.awt.Image.SCALE_DEFAULT);
		
	    BufferedImage bimage = convertToBufferedImage(img);
		
	    System.out.println("Woolley fitness = ");
		
//		BufferedImage image = GraphicsUtil.imageFromCPPN(network, SIZE, SIZE, ArrayUtil.doubleOnes(network.numInputs()), 0, SCALE, ROTATION);
//		DrawingPanel picture = GraphicsUtil.drawImage(image, "Image", SIZE, SIZE);
		
		DrawingPanel heatmap = GraphicsUtil.drawImage(bimage, "Difference in brightness at each pixel", DISPLAY_SIDE_LENGTH, DISPLAY_SIDE_LENGTH);

		// For test runs
//		MMNEAT.main(new String[]{"runNumber:1","randomSeed:0","base:targetimage","mu:400","maxGens:2000000",
//				"io:true","netio:true","mating:true","task:edu.southwestern.tasks.innovationengines.PictureTargetTask",
//				"log:TargetImage-QuadrantRMSESkull","saveTo:QuadrantRMSESkull","allowMultipleFunctions:true","ftype:0","netChangeActivationRate:0.3",
//				"cleanFrequency:400","recurrency:false","logTWEANNData:false","logMutationAndLineage:false",
//				"ea:edu.southwestern.evolution.mapelites.MAPElites",
//				"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
//				//"mapElitesBinLabels:edu.southwestern.tasks.innovationengines.CPPNComplexityBinMapping",
//				"mapElitesBinLabels:edu.southwestern.tasks.innovationengines.PictureFourQuadrantBrightnessBinLabels",
//				"fs:true",
//				"useWoolleyImageMatchFitness:false", "useRMSEImageMatchFitness:true", // Pick one
//				//"matchImageFile:TexasFlag.png",
//				//"matchImageFile:cat.jpg",
//				"matchImageFile:skull64.jpg",
//				"fitnessSaveThreshold:0.71",		// Higher threshold for RMSE 
//				"includeSigmoidFunction:true", 	// In Brian Woolley paper
//				"includeTanhFunction:false",
//				"includeIdFunction:true",		// In Brian Woolley paper
//				"includeFullApproxFunction:false",
//				"includeApproxFunction:false",
//				"includeGaussFunction:true", 	// In Brian Woolley paper
//				"includeSineFunction:true", 	// In Brian Woolley paper
//				"includeCosineFunction:true", 	// In Brian Woolley paper
//				"includeSawtoothFunction:false", 
//				"includeAbsValFunction:false", 
//				"includeHalfLinearPiecewiseFunction:false", 
//				"includeStretchedTanhFunction:false",
//				"includeReLUFunction:false",
//				"includeSoftplusFunction:false",
//				"includeLeakyReLUFunction:false",
//				"includeFullSawtoothFunction:false",
//				"includeTriangleWaveFunction:false", 
//				"includeSquareWaveFunction:false", "blackAndWhitePicbreeder:true"}); 
	}


	public static BufferedImage convertToBufferedImage(Image img) {
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();
		return bimage;
	}

}
