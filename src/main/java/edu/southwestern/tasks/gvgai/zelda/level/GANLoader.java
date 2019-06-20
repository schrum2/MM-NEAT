package edu.southwestern.tasks.gvgai.zelda.level;

import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.ZeldaGANUtil;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.random.RandomNumbers;

public class GANLoader implements LevelLoader{

	public GANLoader() {
		assert Parameters.parameters != null;
		Parameters.parameters.setString("zeldaGANModel", "ZeldaFixedDungeonsAll_5000_10.pth");
		Parameters.parameters.setInteger("GANInputSize", 10);
		Parameters.parameters.setBoolean("zeldaGANUsesOriginalEncoding", false);
		GANProcess.type = GANProcess.GAN_TYPE.ZELDA;
	}
	
	@Override
	public List<List<List<Integer>>> getLevels() {
		double[] latentVector = RandomNumbers.randomArray(GANProcess.latentVectorLength());
		return ZeldaGANUtil.getRoomListRepresentationFromGAN(latentVector);
	}

}
