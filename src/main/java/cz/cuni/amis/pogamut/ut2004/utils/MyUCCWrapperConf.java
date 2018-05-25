
package cz.cuni.amis.pogamut.ut2004.utils;

/**
 * Pogamut's UCCWrapper did not allow for certain options that I require, so I
 * wrapped it in MyUCCWrapper. This small class is related to the associated
 * UCCWrapperConf class that is actually inside of the UCCWrapper class. Since
 * UCCWrapper uses UCCWrapperConf, MyUCCWrapper uses MyUCCWrapperConf.
 * 
 * @author Jacob Schrum
 */
public class MyUCCWrapperConf extends UCCWrapperConf {

	/**
	 * Auto-generated serial ID
	 */
	private static final long serialVersionUID = -1708099469535573325L;

	protected int playerPort = -1;

	/**
	 * Specify the port to connect to the server on. Port cannot be in use by
	 * another process.
	 * 
	 * @param gamePort
	 *            free port number.
	 */
	public void setPlayerPort(int gamePort) {
		this.playerPort = gamePort;
	}
	
	/**
	 * Look at all pertinent details of configuration
	 */
	public String toString() {
		return super.toString() + "[playerPort=" + playerPort + "]";
	}
}
