package edu.utexas.cs.nn.networks.hyperneat;

import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;

public interface SubstrateCoordinateMapping {
	public ILocated2D transformCoordinates(Tuple2D toScale, int width, int height);
}
