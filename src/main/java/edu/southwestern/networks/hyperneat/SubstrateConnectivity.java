package edu.southwestern.networks.hyperneat;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;

/**
 * This class replaces the old way of defining substrate connectivity by Triple<String, String, Boolean>
 * @author Devon Fulcher
 */
public class SubstrateConnectivity {
	public static final int CTYPE_FULL = 0;
	public static final int CTYPE_CONVOLUTION = 1;
	public final String SOURCE_SUBSTRATE_NAME;
	public final String TARGET_SUBSTRATE_NAME;
	public final int receptiveFieldWidth;
	public final int receptiveFieldHeight;
	public int connectivityType;
	
	/**
	 * defines the source, target, and connection type of a link between substrates.
	 * if the connectivity type is CTYPE_CONVOLUTION then the receptive field will be 3x3
	 * @param sourceSubstrateName substrate name of source
	 * @param targetSubstrateName substrate name of target
	 * @param connectivityType how these two substrates are connected(i.e. full, convolutional,...)
	 */
	public SubstrateConnectivity (String sourceSubstrateName, String targetSubstrateName, int connectivityType) {
		this.SOURCE_SUBSTRATE_NAME = sourceSubstrateName;
		this.TARGET_SUBSTRATE_NAME = targetSubstrateName;
		this.connectivityType = connectivityType;
		if (connectivityType == SubstrateConnectivity.CTYPE_CONVOLUTION) {
			//default receptive height and width is 3
			this.receptiveFieldWidth = Parameters.parameters.integerParameter("receptiveFieldWidth");
			this.receptiveFieldHeight = Parameters.parameters.integerParameter("receptiveFieldHeight");
		} else {
			this.receptiveFieldWidth = -1;
			this.receptiveFieldHeight = -1;
		}
	}
	
	public SubstrateConnectivity (String sourceSubstrateName, String targetSubstrateName, int receptiveFieldWidth, int receptiveFieldHeight) {
		this.SOURCE_SUBSTRATE_NAME = sourceSubstrateName;
		this.TARGET_SUBSTRATE_NAME = targetSubstrateName;
		this.connectivityType = CTYPE_CONVOLUTION;
		this.receptiveFieldWidth = receptiveFieldWidth;
		this.receptiveFieldHeight = receptiveFieldHeight;
	}
	
	private SubstrateConnectivity (String sourceSubstrateName, String targetSubstrateName, 
			int connectivityType, int receptiveFieldWidth, int receptiveFieldHeight) {
		this.SOURCE_SUBSTRATE_NAME = sourceSubstrateName;
		this.TARGET_SUBSTRATE_NAME = targetSubstrateName;
		this.connectivityType = connectivityType;
		this.receptiveFieldWidth = receptiveFieldWidth;
		this.receptiveFieldHeight = receptiveFieldHeight;
	}
	
	/**
	 * @param substrateConnectivity
	 * @return the subtrate connectivity as a string
	 */
	public String toString() {
		String result = "";
		result = result.concat("substrate connectivity: from " + SOURCE_SUBSTRATE_NAME + " to " + 
							   TARGET_SUBSTRATE_NAME + " with type " + connectivityType);
		return result;
	}
	
	/**
	 * @return a deep copy of the calling SubstrateConnectivity
	 */
	public SubstrateConnectivity copy() {
		return new SubstrateConnectivity(this.SOURCE_SUBSTRATE_NAME, this.TARGET_SUBSTRATE_NAME,
				this.connectivityType, this.receptiveFieldWidth, this.receptiveFieldHeight);
	}
	
	public boolean equals(Object other) {
		if(other instanceof SubstrateConnectivity) {
			SubstrateConnectivity sc = (SubstrateConnectivity) other;
			return sc.SOURCE_SUBSTRATE_NAME.equals(this.SOURCE_SUBSTRATE_NAME) &&
				   sc.TARGET_SUBSTRATE_NAME.equals(this.TARGET_SUBSTRATE_NAME) &&
				   sc.connectivityType == this.connectivityType &&
				   sc.receptiveFieldWidth == this.receptiveFieldWidth &&
				   sc.receptiveFieldHeight == this.receptiveFieldHeight;
		}
		return false;
	}
}
