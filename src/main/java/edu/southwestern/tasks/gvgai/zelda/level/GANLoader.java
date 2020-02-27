package edu.southwestern.tasks.gvgai.zelda.level;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.ZeldaGANUtil;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.random.RandomNumbers;

public class GANLoader implements LevelLoader{

	public GANLoader() {
		assert Parameters.parameters != null;
		Parameters.parameters.setString("zeldaGANModel", "ZeldaFixedDungeonsAlNoDoors_10000_10.pth");
		Parameters.parameters.setInteger("GANInputSize", 10);
		Parameters.parameters.setBoolean("zeldaGANUsesOriginalEncoding", false);
		GANProcess.type = GANProcess.GAN_TYPE.ZELDA;
	}
	
	@Override
	public List<List<List<Integer>>> getLevels() {
		double[] latentVector = RandomNumbers.randomArray(GANProcess.latentVectorLength());
		List<List<List<Integer>>> rs = ZeldaGANUtil.getRoomListRepresentationFromGAN(latentVector);
		List<List<Integer>> r = rs.get(RandomNumbers.randomGenerator.nextInt(rs.size()));
		List<List<List<Integer>>> ret = new ArrayList<>();
		ret.add(r);
		return ret;
	}

}
