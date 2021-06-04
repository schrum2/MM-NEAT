package autoencoder.python;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.ProcessBuilder.Redirect;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.gan.Comm;
import edu.southwestern.util.PythonUtil;

/**
 * Launches the Python script, MyAutoencoder, which trains
 * an autoencoder.  Receives the output on a loop until 
 * Python code has finished executing.
 * 
 * @author Anna Wicker
 *
 */
public class TrainAutoEncoderProcess extends Comm {
	
	public static final String PYTHON_BASE_PATH = "." + File.separator + "src" + File.separator + "main" + File.separator + "python" + File.separator + "AutoEncoder" + File.separator;
	public static final String AUTOENCODER_PATH = PYTHON_BASE_PATH + "MyAutoencoder.py";
	
	public static String TRAINING_IMAGES_DIRECTORY = null;
	public static String PTH_FILE_NAME = null;
	

	/**
	 * Initializes the process buffers
	 */
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
	
	/**
	 * Launches the Python script and initializes the 
	 * process buffers.  Receives the output on a loop
	 * until the Python code terminates and "END" is returned.
	 */
	@Override
	public void start() {
		try {
			launchTrainingScript();
			initBuffers();
			printInfoMsg(this.threadName + " has started");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String response = this.commRecv();
		// Call commRecv in loop until END is returned
		while(!response.equals("END")){
			response = this.commRecv();
		}
		System.out.println("Training complete!");
	}
	
	/**
	 * Launches the Python script for MyAutoencoder.py,
	 * uses the training set "PicbreederTargetTrainingSet" and 
	 * saves the results to "test.pth".
	 */
	public void launchTrainingScript() {
		if(!(new File(PythonUtil.PYTHON_EXECUTABLE).exists())) {
			throw new RuntimeException("Before launching this program, you need to place the path to your "+
									   "Python executable in my_python_path.txt within the main MM-NEAT directory.");
		}

		// Run program with model architecture and weights specified as parameters
		ProcessBuilder builder = new ProcessBuilder(PythonUtil.PYTHON_EXECUTABLE, PYTHON_BASE_PATH + "MyAutoencoder.py", PYTHON_BASE_PATH + "PicbreederTargetTrainingSet", PYTHON_BASE_PATH + "test.pth");
		builder.redirectError(Redirect.INHERIT); // Standard error will print to console
		try {
			System.out.println(builder.command());
			this.process = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) {
		//TRAINING_IMAGES_DIRECTORY = PYTHON_BASE_PATH + "test.pth";
		
		Parameters.initializeParameterCollections(new String[] {"blackAndWhitePicbreeder:true"});
		PythonUtil.PYTHON_EXECUTABLE = "C:\\ProgramData\\Anaconda3\\python.exe";
		
		TrainAutoEncoderProcess p = new TrainAutoEncoderProcess();
		p.start();
		
	} 

}
