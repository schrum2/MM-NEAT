package edu.southwestern.tasks.mario.gan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * Jacob: Imported from MarioGAN, but then I deleted the Settings class and
 * moved some of that code here.
 * 
 * @author Jialin Liu
 */
public abstract class Comm extends Thread {
    public static final int THRESHOLD = 60000; // milliseconds

    protected boolean end;

    protected String threadName = "thread";
    protected BufferedReader reader;
    protected PrintStream writer;
    protected Process process;

    /**
     * Default constructor
     */
    public Comm() {
        super();
        this.end = false;
    }

	/**
	 * Buffers used for communicating with process via stdin and stdout
	 */
	public void initBuffers() {
		//Initialize input and output
		if (this.process != null) {
			this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			this.writer = new PrintStream(this.process.getOutputStream());
			System.out.println("Process buffers initialized");
		} else {
			printErrorMsg("initBuffers:Null process!");
		}
	}

    /**
     * Sends a message
     *
     * @param msg message to send.
     */
    public void commSend(String msg) throws IOException {
        //printInfoMsg("[" + this.threadName + "] Comm:commSend will send "+ msg + " to GAN");
        writer.println(msg);
        writer.flush();
    }

    /**
     * Receives a message.
     *
     * @return the response
     */
    public String commRecv() {
    	String msg = processCommRecv();
    	return msg;
    }
    public void terminate() {
    	process.destroy();
    }
    private String processCommRecv(){
    	String msg = null;
    	try {
    		msg = reader.readLine();
    		//System.out.println("processCommRecv:"+msg);
    		if (msg != null) {
    			return msg;
    		} else {
    			printErrorMsg("processCommRecv: Null message.");
    			return null;
    		}
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
		printErrorMsg("processCommRecv: exception.");
		return null;
    }

	public static final String ERROR_MSG = "[ERROR] ";
	public static final String INFO_MSG = "[INFO] ";

	public static void printInfoMsg(String msg) {
		System.out.println(INFO_MSG + msg);
	}

	public static void printErrorMsg(String msg) {
		System.out.println(ERROR_MSG + msg);
	}

}
