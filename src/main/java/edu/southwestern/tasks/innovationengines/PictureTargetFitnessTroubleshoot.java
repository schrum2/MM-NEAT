package edu.southwestern.tasks.innovationengines;

import java.awt.image.BufferedImage;

import java.io.FileNotFoundException;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.stats.StatisticsUtilities;

/**
 * Creates an image that reflects the difference in 
 * 
 * @author Anna Wicker
 *
 */
public class PictureTargetFitnessTroubleshoot {
	
	public static final int SIDE_LENGTH = 64;

	public BufferedImage observed;
	public BufferedImage target;
	
	public BufferedImage differenceRGB;
	
	public PictureTargetFitnessTroubleshoot() {
		
	}
	
	/**
	 * 
	 * @param observed
	 * @param target
	 */
	public static BufferedImage heatMapConstruction(BufferedImage observed, BufferedImage target) {
		//assert candidateFeatures.length == targetFeatures.length: "Number of candidate and target features needs to match";
		assert observed.getWidth() == target.getWidth() && observed.getHeight() == target.getHeight(): "Image dimensions need to match";
		
		differenceRGB = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
		
		// Scaling error values for observed image
		
		
		// Scaling error values for target image
		
		for(int x = 0; x < observed.getWidth(); x++) {
			for(int y = 0; y < observed.getHeight(); y++) {
				int degreeOfDifference = observed.getRGB(x, y) - target.getRGB(x, y);
				differenceRGB.setRGB(x, y, degreeOfDifference);
			}
		}
	}
	
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// TODO Auto-generated method stub
		
//		BufferedImage image = GraphicsUtil.imageFromCPPN(network, SIZE, SIZE, ArrayUtil.doubleOnes(network.numInputs()), 0, SCALE, ROTATION);
//		DrawingPanel picture = GraphicsUtil.drawImage(image, "Image", SIZE, SIZE);
		
		DrawingPanel heatMap = GraphicsUtil.drawImage(differenceRGB, "Difference in brightness at each pixel", SIDE_LENGTH, SIDE_LENGTH);

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
