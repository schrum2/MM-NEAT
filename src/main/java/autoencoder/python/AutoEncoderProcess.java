package autoencoder.python;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.ProcessBuilder.Redirect;

import edu.southwestern.tasks.mario.gan.Comm;
import edu.southwestern.util.PythonUtil;

public class AutoEncoderProcess extends Comm {
	
	public static final String PYTHON_BASE_PATH = "." + File.separator + "src" + File.separator + "main" + File.separator + "python" + File.separator + "AutoEncoder" + File.separator;
	// Program for converting a latent vector to a level via a GAN
	public static final String AUTOENCODER_PATH = PYTHON_BASE_PATH + "autoencoderInputGenerator.py";
	
	public static final String SAVED_AUTOENCODER = PYTHON_BASE_PATH + "sim_autoencoder.pth";
		
	
	public AutoEncoderProcess() {
		//TODO: Anything needed here?
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


	/**
	 * Launch the autoencoder.
	 */
	public void launchAutoEncoder() {
		if(!(new File(PythonUtil.PYTHON_EXECUTABLE).exists())) {
			throw new RuntimeException("Before launching this program, you need to place the path to your "+
									   "Python executable in my_python_path.txt within the main MM-NEAT directory.");
		}

		// Run program with model architecture and weights specified as parameters
		ProcessBuilder builder = new ProcessBuilder(PythonUtil.PYTHON_EXECUTABLE, AUTOENCODER_PATH, SAVED_AUTOENCODER);
		builder.redirectError(Redirect.INHERIT); // Standard error will print to console
		try {
			System.out.println(builder.command());
			this.process = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main (String[] args) throws IOException {
		PythonUtil.PYTHON_EXECUTABLE = "C:\\ProgramData\\Anaconda3\\python.exe";
		
		AutoEncoderProcess p = new AutoEncoderProcess();
		p.start();
		
		String s = p.commRecv();
		System.out.println(s);
		
		for(int i = 0; i < 28 * 28; i++) {
			p.commSend(Math.random() + "");
		}
		String output = p.commRecv();
		System.out.println(output);
		System.out.println("done");
	}

}
