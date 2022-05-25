package edu.southwestern.evocraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.ProcessBuilder.Redirect;

import edu.southwestern.tasks.mario.gan.Comm;
import edu.southwestern.util.PythonUtil;

public class MinecraftServerClient extends Comm {

	private static MinecraftServerClient client = null;
	
	public static final String PYTHON_BASE_PATH = "." + File.separator + "src" + File.separator + "main" + File.separator + "python" + File.separator + "EvoCraft" + File.separator;
	// Python script to interact with a Minecraft server on the localhost
	public static final String CLIENT_PATH = PYTHON_BASE_PATH + "ServerSendReceive.py";
	
	public MinecraftServerClient() {
		super();
		// More?
	}
	
	@Override
	public void initBuffers() {
		//Initialize input and output
		if (this.process != null) {
			this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			this.writer = new PrintStream(this.process.getOutputStream());
			System.out.println("Process buffers initialized");
		} else {
			printErrorMsg("MinecraftServerUtil:initBuffers:Null process!");
		}
	}
	
	public MinecraftServerClient getMinecraftServerClient() {
		if(client == null) {
			PythonUtil.setPythonProgram();
			client = new MinecraftServerClient();
			client.start();
			// consume all start-up messages that are not data responses
			String response = "";
			while(!response.equals("READY")) {
				response = client.commRecv();
			}
		}
		return client;
	}
	
	@Override
	public void start() {
		try {
			launchClientScript();
			initBuffers();
			printInfoMsg(this.threadName + " has started");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void launchClientScript() {
		PythonUtil.checkPython();
		// Run script for communicating with Minecraft Server
		ProcessBuilder builder = new ProcessBuilder(PythonUtil.PYTHON_EXECUTABLE, CLIENT_PATH);
		builder.redirectError(Redirect.INHERIT); // Standard error will print to console
		try {
			System.out.println(builder.command());
			this.process = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void terminateClientScriptProcess() {
		if(client != null) {
			client.process.destroy();
			client.process = null;
		}
	}
}
