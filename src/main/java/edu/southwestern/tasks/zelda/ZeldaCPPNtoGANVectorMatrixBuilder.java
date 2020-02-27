package edu.southwestern.tasks.zelda;

import edu.southwestern.networks.Network;
import edu.southwestern.util.CartesianGeometricUtilities;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.util2D.ILocated2D;
import edu.southwestern.util.util2D.Tuple2D;

public class ZeldaCPPNtoGANVectorMatrixBuilder implements ZeldaGANVectorMatrixBuilder {

	private Network cppn;
	private double[] inputMultipliers;

	public ZeldaCPPNtoGANVectorMatrixBuilder(Network cppn, double[] inputMultipliers) {
		this.cppn = cppn;
		this.inputMultipliers = inputMultipliers;
	}
	
	@Override
	public double[] latentVectorAndMiscDataForPosition(int width, int height, int x, int y) {
		ILocated2D scaled = CartesianGeometricUtilities.centerAndScale(new Tuple2D(x, y), width, height);
		double[] remixedInputs = { scaled.getX(), scaled.getY(), scaled.distance(new Tuple2D(0, 0)) * GraphicsUtil.SQRT2, GraphicsUtil.BIAS };
		// Might turn some inputs on/off
		for(int i = 0; i < remixedInputs.length; i++) {
			remixedInputs[i] *= inputMultipliers[i];
		}
		double[] vector = cppn.process(remixedInputs);
		return vector;
	}

}
