package edu.utexas.cs.nn.networks.hyperneat;

import edu.utexas.cs.nn.util.CartesianGeometricUtilities;
import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;

/**
 * maps substrate to 1D in Y-Direction with bottom coordinate mapping
 * @author Lauren Gillespie
 *
 */
public class Bottom1DSubstrateMapping implements SubstrateCoordinateMapping {
	@Override
	public ILocated2D transformCoordinates(Tuple2D toScale, int width, int height) {
		return CartesianGeometricUtilities.Bottom1DCenterAndScale(toScale, width, height);
	}

}
