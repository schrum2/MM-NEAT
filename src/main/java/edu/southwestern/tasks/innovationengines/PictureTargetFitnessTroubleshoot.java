package edu.southwestern.tasks.innovationengines;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.southwestern.util.datastructures.Triple;
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
	 * Creates a pair of heatmaps showing the degree of difference 
	 * between each pixel in the observed and target image.
	 * 
	 * @param observed
	 * @param target
	 */
	public static Triple<BufferedImage,BufferedImage,BufferedImage> heatmapConstruction(BufferedImage observed, BufferedImage target) {
		
		assert observed.getWidth() == target.getWidth() && observed.getHeight() == target.getHeight(): "Image dimensions need to match";
		
		BufferedImage differenceWoolley = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
		BufferedImage differenceRMSE = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
		BufferedImage errorDiff = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
		
		double sumDegreeOfDifference = 0;
		double sumSquaredErrors = 0;
		
		for(int x = 0; x < observed.getWidth(); x++) {
			for(int y = 0; y < observed.getHeight(); y++) {
				
				float observedBrightness = GraphicsUtil.getHSB(observed, x, y)[GraphicsUtil.BRIGHTNESS_INDEX];
				float targetBrightness = GraphicsUtil.getHSB(target, x, y)[GraphicsUtil.BRIGHTNESS_INDEX];
				
				double degreeOfDifference = PictureTargetTask.degreeOfDifference(observedBrightness, targetBrightness);
				sumDegreeOfDifference += degreeOfDifference;
				
				double error = observedBrightness - targetBrightness;
				double squaredError = error * error;
				sumSquaredErrors += squaredError;
				
				System.out.println("(" + x + "," + y +"): error = "+ error +" Woolley = " + degreeOfDifference + ", RMSE = " + squaredError);
				Color c = new Color((float) degreeOfDifference, 0, 0);
				differenceWoolley.setRGB(x, y, c.getRGB());
				
				Color r = new Color((float) squaredError, 0, 0);
				differenceRMSE.setRGB(x, y, r.getRGB());
				
				float red = error > 0 ? (float) error  : 0;
				float blue = error > 0 ? 0 : (float)(-error);
				//System.out.println(red + " " + blue);
				Color e = new Color(red, 0, blue);
				errorDiff.setRGB(x, y, e.getRGB());
				
			}
		}
		
		// Calculate and print Woolley fitness
		double error = sumDegreeOfDifference / (observed.getWidth() * observed.getHeight());
	    System.out.println("Woolley fitness = " + (1 - (error * error)));
	    
	    // Calculate and print RMSE fitness
	    //Math.sqrt(sumSquaredErrors / xs.length)
	    double rootMeanSquareError = Math.sqrt(sumSquaredErrors / (observed.getWidth() * observed.getHeight()));
	    System.out.println("RMSE fitness = " + (1 - rootMeanSquareError));
		
		return new Triple<>(differenceWoolley, differenceRMSE,errorDiff);
	}
	
	
	public static void main(String[] args) throws NoSuchMethodException, IOException {
		
		BufferedImage skull = ImageIO.read(new File(IMAGE_MATCH_PATH + File.separator + "ScratchedSkull64.png"));
		//BufferedImage skull = ImageIO.read(new File(IMAGE_MATCH_PATH + File.separator + "ScratchedSkull.jpg"));
		//BufferedImage skull = ImageIO.read(new File(IMAGE_MATCH_PATH + File.separator + "failedskull.jpg"));
		// BufferedImage skull = ImageIO.read(new File("targetimage\\WoolleySkull7\\archive\\Neurons[8]links[7]\\0.70830Neurons[8]links[7]1721290.jpg"));
		//BufferedImage skull = ImageIO.read(new File("targetimage\\QuadrantRMSESkull1\\archive\\(4,4,4,4)\\0.71548(4,4,4,4)372085.jpg"));
		//BufferedImage skull = ImageIO.read(new File(IMAGE_MATCH_PATH + File.separator + "skull64.jpg"));
		//BufferedImage targetSkull = ImageIO.read(new File(IMAGE_MATCH_PATH + File.separator + "skull64.jpg"));
		BufferedImage targetSkull = ImageIO.read(new File(IMAGE_MATCH_PATH + File.separator + "skull64.png"));
		
		
		//ImageIO.read(new File(IMAGE_MATCH_PATH + File.separator + filename));
		Triple<BufferedImage,BufferedImage,BufferedImage> differenceRGB = heatmapConstruction(skull, targetSkull);
		BufferedImage woolley = differenceRGB.t1;
		BufferedImage rmse = differenceRGB.t2;
		BufferedImage diff = differenceRGB.t3;
		
		Image img = woolley.getScaledInstance(DISPLAY_SIDE_LENGTH, DISPLAY_SIDE_LENGTH, java.awt.Image.SCALE_DEFAULT);		
	    BufferedImage largeWoolley = GraphicsUtil.convertToBufferedImage(img);

		img = rmse.getScaledInstance(DISPLAY_SIDE_LENGTH, DISPLAY_SIDE_LENGTH, java.awt.Image.SCALE_DEFAULT);		
	    BufferedImage largeRMSE = GraphicsUtil.convertToBufferedImage(img);

		img = diff.getScaledInstance(DISPLAY_SIDE_LENGTH, DISPLAY_SIDE_LENGTH, java.awt.Image.SCALE_DEFAULT);		
	    BufferedImage largeDiff = GraphicsUtil.convertToBufferedImage(img);

		img = skull.getScaledInstance(DISPLAY_SIDE_LENGTH, DISPLAY_SIDE_LENGTH, java.awt.Image.SCALE_DEFAULT);		
	    BufferedImage largeSkull = GraphicsUtil.convertToBufferedImage(img);

		img = targetSkull.getScaledInstance(DISPLAY_SIDE_LENGTH, DISPLAY_SIDE_LENGTH, java.awt.Image.SCALE_DEFAULT);		
	    BufferedImage largeTargetSkull = GraphicsUtil.convertToBufferedImage(img);
	    
		
//		BufferedImage image = GraphicsUtil.imageFromCPPN(network, SIZE, SIZE, ArrayUtil.doubleOnes(network.numInputs()), 0, SCALE, ROTATION);
//		DrawingPanel picture = GraphicsUtil.drawImage(image, "Image", SIZE, SIZE);

		DrawingPanel original = GraphicsUtil.drawImage(largeSkull, "Original Image", DISPLAY_SIDE_LENGTH, DISPLAY_SIDE_LENGTH);
		DrawingPanel target = GraphicsUtil.drawImage(largeTargetSkull, "Target Image", DISPLAY_SIDE_LENGTH, DISPLAY_SIDE_LENGTH);

	    
		DrawingPanel heatmapWoolley = GraphicsUtil.drawImage(largeWoolley, "Difference in brightness at each pixel (Woolley)", DISPLAY_SIDE_LENGTH, DISPLAY_SIDE_LENGTH);
		DrawingPanel heatmapRMSE = GraphicsUtil.drawImage(largeRMSE, "Difference in brightness at each pixel (RMSE)", DISPLAY_SIDE_LENGTH, DISPLAY_SIDE_LENGTH);
		DrawingPanel heatmapDiff = GraphicsUtil.drawImage(largeDiff, "Difference in brightness at each pixel (RMSE)", DISPLAY_SIDE_LENGTH, DISPLAY_SIDE_LENGTH);

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


}
