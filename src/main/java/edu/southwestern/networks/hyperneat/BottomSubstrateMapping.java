package edu.southwestern.networks.hyperneat;

import edu.southwestern.util.CartesianGeometricUtilities;
import edu.southwestern.util.util2D.ILocated2D;
import edu.southwestern.util.util2D.Tuple2D;

public class BottomSubstrateMapping implements SubstrateCoordinateMapping {

	@Override
	public ILocated2D transformCoordinates(Tuple2D toScale, int width, int height) {
		return CartesianGeometricUtilities.bottomCenterAndScale(toScale, width, height);
	}

}
