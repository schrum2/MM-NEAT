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
	public int connectivityType;
	
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
		return new SubstrateConnectivity(this.SOURCE_SUBSTRATE_NAME, this.TARGET_SUBSTRATE_NAME, this.connectivityType);
	}
	
	public boolean equals(Object other) {
		if(other instanceof SubstrateConnectivity) {
			SubstrateConnectivity sc = (SubstrateConnectivity) other;
			return sc.connectivityType == this.connectivityType &&
				   sc.SOURCE_SUBSTRATE_NAME.equals(this.SOURCE_SUBSTRATE_NAME) &&
				   sc.TARGET_SUBSTRATE_NAME.equals(this.TARGET_SUBSTRATE_NAME);
		}
		return false;
	}
}
