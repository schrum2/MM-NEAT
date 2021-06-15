package autoencoder.python;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;

import javax.imageio.ImageIO;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.gan.Comm;
import edu.southwestern.util.PythonUtil;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;

/**
 * Launches Python script for a previously trained autoencoder.
 * 
 * @author wickera
 *
 */
public class AutoEncoderProcess extends Comm {

	public static final String PYTHON_BASE_PATH = "." + File.separator + "src" + File.separator + "main" + File.separator + "python" + File.separator + "AutoEncoder" + File.separator;
	// Program for converting a latent vector to a level via a GAN
	public static final String AUTOENCODER_PATH = PYTHON_BASE_PATH + "autoencoderInputGenerator.py";

	public static AutoEncoderProcess currentProcess = null;

	private String savedAutoencoder;
	private AUTOENCODER_MODE mode;
	// When using an AutoEncoder with MAP-Elites, need to assign "novelty" before autoencoder
	// is trained, so have a special case for calculating loss
	public static boolean neverInitialized = true;

	public static final int SIDE_LENGTH = 28;

	public static enum AUTOENCODER_MODE {
		LOSS, IMAGE
	}

	/**
	 * Initializes the path name, mode and Python script.
	 * 
	 * @param pthName Name of pth file to be saved
	 * @param mode 
	 */
	public AutoEncoderProcess(String pthName, AUTOENCODER_MODE mode) {
		PythonUtil.setPythonProgram();
		savedAutoencoder = pthName;
		this.mode = mode;
	}

	/**
	 * Initializes the current process if not
	 * already initialized.
	 * 
	 * @return the current process
	 */
	public static AutoEncoderProcess getAutoEncoderProcess() {
		if(currentProcess == null) {
			PythonUtil.setPythonProgram();
			currentProcess = new AutoEncoderProcess(Parameters.parameters.stringParameter("mostRecentAutoEncoder"), AUTOENCODER_MODE.LOSS);
			currentProcess.start();
			// consume all start-up messages that are not data responses
			String response = "";
			while(!response.equals("READY")) {
				response = currentProcess.commRecv();
			}
		}
		return currentProcess;
	}

	/**
	 * Destroy an autoencoder process so a new one can be started
	 */
	public static void terminateAutoEncoderProcess() {
		if(currentProcess != null) {
			currentProcess.process.destroy();
			currentProcess = null;
		}
	}

	/**
	 * 
	 * 
	 * @param nameNewPthFile
	 * @param mode
	 * @return 
	 */
	public static AutoEncoderProcess resetAutoEncoder(String nameNewPthFile, AUTOENCODER_MODE mode) {
		terminateAutoEncoderProcess();
		AutoEncoderProcess newProcess = new AutoEncoderProcess(nameNewPthFile, mode);
		return newProcess;
	}


	public static double getReconstructionLoss(BufferedImage image) {
		try {
			AutoEncoderProcess p = getAutoEncoderProcess();
			double[] imageInput =  GraphicsUtil.flatFeatureArrayFromBufferedImage(image);
			String output = null;
			synchronized(p) {
				for(int i = 0; i < imageInput.length; i++) {
					p.commSend(imageInput[i] + "");
				}
				output = p.commRecv();
			}
			double result = Double.parseDouble(output);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to send to autoencoder");
			System.exit(1);
			return Double.NaN;
		}
	}

	/**
	 * Initializes the process buffers.
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
	 * process buffers.
	 */
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
	 * Launches the autoencoder using the training 
	 * set specified by AUTOENCODER_PATH and saves the
	 * results in a .pth file specified by SAVED_AUTOENCODER.
	 */
	public void launchAutoEncoder() {
		if(!(new File(PythonUtil.PYTHON_EXECUTABLE).exists())) {
			throw new RuntimeException("Before launching this program, you need to place the path to your "+
					"Python executable in my_python_path.txt within the main MM-NEAT directory.");
		}

		// Run program with model architecture and weights specified as parameters
		ProcessBuilder builder = new ProcessBuilder(PythonUtil.PYTHON_EXECUTABLE, AUTOENCODER_PATH, savedAutoencoder, mode == AUTOENCODER_MODE.IMAGE ? "image" : "loss");
		builder.redirectError(Redirect.INHERIT); // Standard error will print to console
		try {
			System.out.println(builder.command());
			this.process = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main (String[] args) throws IOException {
		Parameters.initializeParameterCollections(new String[] {"blackAndWhitePicbreeder:true"});
		PythonUtil.PYTHON_EXECUTABLE = "C:\\ProgramData\\Anaconda3\\python.exe";

		AUTOENCODER_MODE mode = AUTOENCODER_MODE.LOSS;
		//AutoEncoderProcess p = new AutoEncoderProcess("parentDir\\test.pth", mode);
		//AutoEncoderProcess p = new AutoEncoderProcess("targetimage\\skull6\\snapshots\\iteration30000.pth", mode);
		AutoEncoderProcess p = new AutoEncoderProcess("targetimage\\skullAutoEncoder20\\snapshots\\iteration4500000.pth", mode);
		p.start();

		//BufferedImage img = ImageIO.read(new File("parentDir" + File.separator + "PicbreederTargetTrainingSet" + File.separator + "0.71288Neurons[35]links[63]1788009.jpg"));
		//BufferedImage img = ImageIO.read(new File("parentDir" + File.separator + "PicbreederTargetTrainingSet" + File.separator + "0.52723Neurons[15]links[58]143861.jpg"));
		//BufferedImage img = ImageIO.read(new File("parentDir" + File.separator + "PicbreederTargetTrainingSet" + File.separator + "0.68266Neurons[14]links[62]1105464.jpg"));
		//BufferedImage img = ImageIO.read(new File("targetimage" + File.separator + "skull6" + File.separator + "snapshots" + File.separator + "iteration30000" + File.separator + "0.71623785656924-Neurons[30]links[37].jpg"));
		//BufferedImage img = ImageIO.read(new File("targetimage\\skullAutoEncoder20\\snapshots\\iteration4500000\\0.809899371158382-Neurons[45]loss[0.9,1.0].jpg"));
		BufferedImage img = ImageIO.read(new File("targetimage\\skullAutoEncoder20\\snapshots\\iteration4500000\\0.7640251320574905-Neurons[9]loss[0.9,1.0].jpg"));
		//BufferedImage img = ImageIO.read(new File("data" + File.separator + "imagematch" + File.separator + "skull64.png"));
		Image scaled = img.getScaledInstance(28, 28, BufferedImage.SCALE_DEFAULT);
		img = GraphicsUtil.convertToBufferedImage(scaled);
		@SuppressWarnings("unused")
		DrawingPanel picture = GraphicsUtil.drawImage(img, "Before", img.getWidth(), img.getHeight());

		double[] imageInput =  GraphicsUtil.flatFeatureArrayFromBufferedImage(img);

		String s = p.commRecv();
		System.out.println(s);
		System.out.println("Before:"+Arrays.toString(imageInput));

		for(int i = 0; i < imageInput.length; i++) {
			p.commSend(imageInput[i] + "");
		}

		String output = p.commRecv();

		if(mode == AUTOENCODER_MODE.IMAGE) {		
			System.out.println("After:"+output);

			// Remove [ and ]
			output = output.substring(1,output.length()-1);
			String[] arrOutput = output.split(",");
			double[] numOut = new double[arrOutput.length];
			for(int i = 0; i < numOut.length; i++) {
				numOut[i] = Double.parseDouble(arrOutput[i]);
			}

			BufferedImage pythonOutput = new BufferedImage(SIDE_LENGTH, SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);
			int i = 0;
			for(int y = 0; y < SIDE_LENGTH; y++) {
				for(int x = 0; x < SIDE_LENGTH; x++) {
					float gray = (float) numOut[i++];
					gray = Math.max(gray, 0); // Why is negative possible!?
					Color c = new Color(gray,gray,gray);
					pythonOutput.setRGB(x, y, c.getRGB());	
				}
			}

			DrawingPanel outputImage = GraphicsUtil.drawImage(pythonOutput, "After", img.getWidth(), img.getHeight());
			outputImage.getFrame().setLocation(100, 0);

			System.out.println("done");
		} else {
			System.out.println("Loss is:"+output);
		}
	}

}
