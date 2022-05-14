package edu.southwestern.evolution.mutation.tweann;

import java.util.List;

import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.megaman.MegaManCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.megaman.MegaManTrackSegmentType;
import edu.southwestern.tasks.megaman.gan.MegaManGANUtil;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManGANGenerator;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
/**
 * Converts CPPN to GAN to Direct to GAN.
 * 
 * Cannot specific type of phenotype since it changes
 *
 */

public class ConvertMegaManCPPN2GANtoDirect2GANMutation extends ConvertCPPN2GANtoDirect2GANMutation {
	/**
	 * Construct that defines the rate (0.1) and tells if it's out of bounds
	 */
	public ConvertMegaManCPPN2GANtoDirect2GANMutation() {
		super();
		double rate = Parameters.parameters.doubleParameter("indirectToDirectTransitionRate");
		assert 0 <= rate && rate <= 1 : "Mutation rate out of range: " + rate;
		this.rate = rate;
	}

	/**
	 * Takes in a CPPN and converts it to a whole latent vector, which is then returned
	 * @param cppn The CPPN of type Network
	 * @return longResult The array of doubles containing the whole latent vector
	 */
	@Override
	protected double[] getLongVectorResultFromCPPN(Network cppn) {
		//Creates empty array to hold the whole latent vector
		int segLeng = (Parameters.parameters.integerParameter("GANInputSize")+MegaManGANGenerator.numberOfAuxiliaryVariables())*Parameters.parameters.integerParameter("megaManGANLevelChunks");
		double[] longResult = new double[segLeng];
		
		//Makes new MegaManTrackSegmentType to be used
		MegaManTrackSegmentType segmentTypeTracker = new MegaManTrackSegmentType();
		
		//Calls longVectorOrCPPNtoMegaManLevelAndVector to get a Pair (level and whole latent vector)
		Pair<List<List<Integer>>, double[]> levelAndWholeLatentVector = MegaManGANUtil.longVectorOrCPPNtoMegaManLevelAndVector(
				MegaManGANUtil.getMegaManGANGenerator(),cppn,null,Parameters.parameters.integerParameter("megaManGANLevelChunks")
				,segmentTypeTracker,ArrayUtil.doubleOnes(MegaManCPPNtoGANLevelBreederTask.SENSOR_LABELS.length));

		//Assigns the second part of the pair (the WholeLatentVector) to longResult
		longResult = levelAndWholeLatentVector.t2;
		
		return longResult;
		
	}
	
}
//Old code 

//int chunks = Parameters.parameters.integerParameter("megaManGANLevelChunks");
//double[] inputMultipliers = ArrayUtil.doubleOnes(MegaManCPPNtoGANLevelBreederTask.SENSOR_LABELS.length);
//
//// TODO: This method unnecessarily repeats code from MegaManGANUtil.longVectorToMegaManLevel
////       We should refactor to avoid the repeated code
//HashSet<Point> previousPoints = new HashSet<>();
//Point currentPoint  = new Point(0,0);
//Point previousPoint = null;
//Point placementPoint = currentPoint;
//List<List<Integer>> level = new ArrayList<>();
//List<List<Integer>> segment = new ArrayList<>();
//for(int i = 0;i<chunks;i++) {
//	
//	// Taken from XPREF, YPREF, and BIASPREF, respectively
//	double[] oneSegmentData = cppn.process(new double[] {
//			inputMultipliers[0] * currentPoint.x/(1.0*chunks),
//			inputMultipliers[1] * currentPoint.y/(1.0*chunks),
//			inputMultipliers[2] * 1.0});
//	Pair<List<List<Integer>>, Point> segmentAndPoint = MegaManGANUtil.getMegaManGANGenerator().generateSegmentFromVariables(oneSegmentData, previousPoint, previousPoints, currentPoint);
//	
//	if(segmentAndPoint==null) {
//		break; //NEEDS TO BE FIXED!! ORB WILL NOT BE PLACED
//	}
//	
//	}
