package autoencoder.python;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

import edu.southwestern.tasks.mario.gan.Comm;

public class AutoEncoderProcess extends Comm {

	public AutoEncoderProcess() {
		
	}
	
	@Override
	public void initBuffers() {
		//Initialize input and output
		if (this.process != null) {
			this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			this.writer = new PrintStream(this.process.getOutputStream());
			System.out.println("Process buffers initialized");
		} else {
			printErrorMsg("AutoEncoderProcess:initBuffers:Null process!");
		}
	}

	
	@Override
	public void start() {
		try {
			launchAutoEncoder();
			initBuffers();
			printInfoMsg(this.threadName + " has started");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void launchAutoEncoder() {
		// TODO Auto-generated method stub
		
	}

}
