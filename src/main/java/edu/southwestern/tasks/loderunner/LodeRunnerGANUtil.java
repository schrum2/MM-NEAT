package edu.southwestern.tasks.loderunner;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.gan.GANProcess;

/**
 * 
 * @author kdste
 *
 */
public class LodeRunnerGANUtil {

	public static final int LODE_RUNNER_TILE_NUMBER = 8; //number of tiles in LodeRunner 
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int size = 22; //latent vector size 
		GANProcess.type = GANProcess.GAN_TYPE.LODE_RUNNER;
		Parameters.initializeParameterCollections(new String[] {"GANInputSize:"+size,});
		
	}

}
