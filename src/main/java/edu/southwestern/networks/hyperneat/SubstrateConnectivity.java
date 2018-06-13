package edu.southwestern.networks.hyperneat;

/**
 * This class replaces the old way of defining substrate connectivity by Triple<String, String, Boolean>
 * @author Devon Fulcher
 */
public class SubstrateConnectivity {
	public static final int CTYPE_FULL = 0;
	public static final int CTYPE_CONVOLUTION = 1;
	public final String SOURCE_SUBSTRATE_NAME;
	public final String TARGET_SUBSTRATE_NAME;
	public final int connectivityType;
	
	/**
	 * defines the source, target, and connection type of a link between substrates 
	 * @param sourceSubstrateName substrate name of source
	 * @param targetSubstrateName substrate name of target
	 * @param connectivityType how these two substrates are connected(i.e. full, convolutional,...)
	 */
	public SubstrateConnectivity (String sourceSubstrateName, String targetSubstrateName, int connectivityType) {
		this.SOURCE_SUBSTRATE_NAME = sourceSubstrateName;
		this.TARGET_SUBSTRATE_NAME = targetSubstrateName;
		this.connectivityType = connectivityType;
	}
	
	/**
	 * @param substrateConnectivity
	 * @return the subtrate connectivity as a string
	 */
	public String toString (SubstrateConnectivity substrateConnectivity) {
		String result = "";
		result.concat("substrate connectivity: from " + SOURCE_SUBSTRATE_NAME + " to " + 
				TARGET_SUBSTRATE_NAME + " with type " + connectivityType);
		return result;
	}
}
