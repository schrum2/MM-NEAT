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

public class AutoEncoderProcess extends Comm {
	
	public static final String PYTHON_BASE_PATH = "." + File.separator + "src" + File.separator + "main" + File.separator + "python" + File.separator + "AutoEncoder" + File.separator;
	// Program for converting a latent vector to a level via a GAN
	public static final String AUTOENCODER_PATH = PYTHON_BASE_PATH + "autoencoderInputGenerator.py";
	
	public static final String SAVED_AUTOENCODER = PYTHON_BASE_PATH + "sim_autoencoder.pth";
		
	public static final int SIDE_LENGTH = 28;
	
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
		Parameters.initializeParameterCollections(new String[] {"blackAndWhitePicbreeder:true"});
		PythonUtil.PYTHON_EXECUTABLE = "C:\\ProgramData\\Anaconda3\\python.exe";
		
		AutoEncoderProcess p = new AutoEncoderProcess();
		p.start();
		
		//BufferedImage img = ImageIO.read(new File("parentDir" + File.separator + "PicbreederTargetTrainingSet" + File.separator + "0.71288Neurons[35]links[63]1788009.jpg"));
		//BufferedImage img = ImageIO.read(new File("parentDir" + File.separator + "PicbreederTargetTrainingSet" + File.separator + "0.52723Neurons[15]links[58]143861.jpg"));
		//BufferedImage img = ImageIO.read(new File("parentDir" + File.separator + "PicbreederTargetTrainingSet" + File.separator + "0.68266Neurons[14]links[62]1105464.jpg"));
		BufferedImage img = ImageIO.read(new File("data" + File.separator + "imagematch" + File.separator + "skull64.png"));
		Image scaled = img.getScaledInstance(28, 28, BufferedImage.SCALE_DEFAULT);
		img = GraphicsUtil.convertToBufferedImage(scaled);
		DrawingPanel picture = GraphicsUtil.drawImage(img, "Before", img.getWidth(), img.getHeight());
		
		double[] imageInput =  GraphicsUtil.flatFeatureArrayFromBufferedImage(img);
		
		String s = p.commRecv();
		System.out.println(s);
		System.out.println("Before:"+Arrays.toString(imageInput));
		
		for(int i = 0; i < imageInput.length; i++) {
			p.commSend(imageInput[i] + "");
		}
		
		String output = p.commRecv();
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
	}

}
