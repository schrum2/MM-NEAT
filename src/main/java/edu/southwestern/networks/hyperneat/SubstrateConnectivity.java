package edu.southwestern.networks.hyperneat;

import edu.southwestern.parameters.Parameters;

/**
 * This class replaces the old way of defining substrate connectivity by Triple<String, String, Boolean>
 * @author Devon Fulcher
 */
public class SubstrateConnectivity {
	public static final int CTYPE_FULL = 0;
	public static final int CTYPE_CONVOLUTION = 1;
	public String sourceSubstrateName;
	public String targetSubstrateName;
	public int receptiveFieldWidth;
	public int receptiveFieldHeight;
	public int connectivityType;
	
	/**
	 * defines the source, target, and connection type of a link between substrates.
	 * if the connectivity type is CTYPE_CONVOLUTION then the receptive field will be 3x3
	 * @param sourceSubstrateName substrate name of source
	 * @param targetSubstrateName substrate name of target
	 * @param connectivityType how these two substrates are connected(i.e. full, convolutional,...)
	 */
	public SubstrateConnectivity (String sourceSubstrateName, String targetSubstrateName, int connectivityType) {
		this(sourceSubstrateName, targetSubstrateName, connectivityType, 
				connectivityType == SubstrateConnectivity.CTYPE_CONVOLUTION ? Parameters.parameters.integerParameter("receptiveFieldWidth") : -1,
				connectivityType == SubstrateConnectivity.CTYPE_CONVOLUTION ? Parameters.parameters.integerParameter("receptiveFieldHeight") : -1);		
	}
	
	public SubstrateConnectivity (String sourceSubstrateName, String targetSubstrateName, int receptiveFieldWidth, int receptiveFieldHeight) {
		this(sourceSubstrateName, targetSubstrateName, receptiveFieldWidth > 0 && receptiveFieldHeight > 0 ? CTYPE_CONVOLUTION : CTYPE_FULL, receptiveFieldWidth, receptiveFieldHeight);
	}
	
	/**
	 * Constructor that specifies all values
	 * @param sourceSubstrateName
	 * @param targetSubstrateName
	 * @param connectivityType
	 * @param receptiveFieldWidth
	 * @param receptiveFieldHeight
	 */
	private SubstrateConnectivity (String sourceSubstrateName, String targetSubstrateName, int connectivityType, int receptiveFieldWidth, int receptiveFieldHeight) {
		assert sourceSubstrateName != null : "sourceSubstrateName must be specified";
		assert targetSubstrateName != null : "targetSubstrateName must be specified";
		assert !sourceSubstrateName.equals("null") : "sourceSubstrateName must be specified";
		assert !targetSubstrateName.equals("null") : "targetSubstrateName must be specified";
		assert receptiveFieldWidth != 0 : "Receptive field width cannot be 0";
		assert receptiveFieldHeight != 0 : "Receptive field height cannot be 0";
				
		this.sourceSubstrateName = sourceSubstrateName;
		this.targetSubstrateName = targetSubstrateName;
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
		result = result.concat("substrate connectivity: from " + sourceSubstrateName + " to " + 
							   targetSubstrateName + " with type " + connectivityType + " and field size " + receptiveFieldWidth + "x" + receptiveFieldHeight);
		return result;
	}
	
	/**
	 * @return a deep copy of the calling SubstrateConnectivity
	 */
	public SubstrateConnectivity copy() {
		return new SubstrateConnectivity(this.sourceSubstrateName, this.targetSubstrateName,
				this.connectivityType, this.receptiveFieldWidth, this.receptiveFieldHeight);
	}
	
	public boolean equals(Object other) {
		if(other instanceof SubstrateConnectivity) {
			SubstrateConnectivity sc = (SubstrateConnectivity) other;
			return sc.sourceSubstrateName.equals(this.sourceSubstrateName) &&
				   sc.targetSubstrateName.equals(this.targetSubstrateName) &&
				   sc.connectivityType == this.connectivityType &&
				   sc.receptiveFieldWidth == this.receptiveFieldWidth &&
				   sc.receptiveFieldHeight == this.receptiveFieldHeight;
		}
		return false;
	}
}
