package edu.southwestern.tasks.evocraft.fitness;

/**
 * This is not a unit test, but it helps manage the unit tests in this package
 * that launch Minecraft servers. For reasons that are not completely clear,
 * this collection of unit tests can only succeed if a single server is launched
 * and then terminated. If different tests launch a server and then terminate it
 * when done, then whichever ones execute after the first will have errors, as
 * the server will be unreachable. So, instead, only the last test to need a
 * server is allowed to terminate it. 
 * 
 * @author Jacob Schrum
 *
 */
public class MinecraftServerTestTracker {
	// WaterLavaSecondaryCreationFitnessTest
	// ChangeCenterOfMassFitnessTest
	private static int numberOfUnitTestsThatMakeAServer = 2;
	
	public static synchronized void decrementServerTestCount() {
		numberOfUnitTestsThatMakeAServer--;
	}
	
	public static synchronized int checkServerTestCount() {
		return numberOfUnitTestsThatMakeAServer;
	}
}
