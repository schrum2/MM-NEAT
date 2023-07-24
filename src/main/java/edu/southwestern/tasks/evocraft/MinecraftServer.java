package edu.southwestern.tasks.evocraft;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;

public class MinecraftServer {
	// Give the server this much time to start before connecting the client
	public static final int SERVER_START_STOP_DELAY = 40000;
	public static final String SERVER_PATH = "data" + File.separator + "EvoCraft-py" + File.separator;
	public static final String SERVER_JAR = "spongevanilla-1.12.2-7.3.0.jar";
	
	private static Process server = null;
	
	/**
	 * Launch the modded EvoCraft Minecraft server distributed with MM-NEAT
	 */
	public static void launchServer() {
		ProcessBuilder pb = new ProcessBuilder("java","-jar",SERVER_JAR);
		pb.directory(new File(SERVER_PATH)); // Launch from the server directory itself (where auxiliary files are located)
		try {
			System.out.println(pb.command());
			server = pb.start();
			Thread.sleep(SERVER_START_STOP_DELAY); // Give server chance to launch
		} catch (IOException e) {
			System.out.println("Exception setting up the Minecraft process:");
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			System.out.println("Exception waiting for server to launch. Not enough time?");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Terminate a previously launched instance of the Minecraft EvoCrafy server
	 */
	public static void terminateServer() {
		if(server != null) {
			server.destroyForcibly();
			server = null;
			// Give server chance to die
			try {
				Thread.sleep(SERVER_START_STOP_DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Indicate whether an EvoCraft server is currently running.
	 * However, will not be able to detect crashes or program
	 * termination caused by external sources.
	 * 
	 * @return true if launchServer has been called and terminateServer
	 * 			has not been called yet.
	 */
	public static boolean serverIsRunning() {
		return server != null;
	}
	
	public static void main(String[] args) {
		launchServer();
		//Thread.sleep(30000); // Give server chance to launch
		MinecraftClient client = MinecraftClient.getMinecraftClient();
		List<Block> blocks = client.readCube(114, 9, 114, 120, 16, 120);
		System.out.println("Size: "+blocks.size());
		System.out.println(blocks);
		client.terminate();
		terminateServer();
	}
}
