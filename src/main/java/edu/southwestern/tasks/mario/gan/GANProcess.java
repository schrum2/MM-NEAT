package edu.southwestern.tasks.mario.gan;

import java.io.*;
import java.lang.ProcessBuilder.Redirect;

import edu.southwestern.util.PythonUtil;

public class GANProcess extends Comm {
	
	public static final String PYTHON_BASE_PATH = "." + File.separator + "src" + File.separator + "main" + File.separator + "python" + File.separator + "MarioGAN" + File.separator;
	// Program for converting a latent vector to a level via a GAN
	public static final String WASSERSTEIN_PATH = PYTHON_BASE_PATH + "generator_ws.py";
	// Default GAN from MarioGAN GECCO 2018 trained on first level of the game
	public static final String WASSERSTEIN_GAN = PYTHON_BASE_PATH + "netG_epoch_5000.pth";
	// Default latent vector size for MarioGAN defined above
	public static final int GAN_DIM = 32;
	
	String GANPath = null;
	int GANDim = -1; 

	/**
	 * Loads the default Mario GAN on the first level of the game from the original Maio GAN publication (GECCO 2018)
	 */
	public GANProcess() {
		this(WASSERSTEIN_GAN, GAN_DIM);
	}

	/**
	 * This option allows for different GAN models than the default one.
	 * These models could be trained on different level sets, or may use
	 * different numbers of inputs for the latent variable.
	 * @param GANPath Path to GAN pth file
	 * @param GANDim Input size
	 */
	public GANProcess(String GANPath, int GANDim) {
		super();
		this.threadName = "GANThread";
		this.GANPath = GANPath;
		this.GANDim = GANDim;
	}

	/**
	 * Length of each latent input vector
	 * @return
	 */
	public int getLatentVectorSize() {
		return GANDim;
	}
	
	/**
	 * Launch GAN, this should be called only once
	 */
	public void launchGAN() {
		if(!(new File(PythonUtil.PYTHON_EXECUTABLE).exists())) {
			throw new RuntimeException("Before launching this program, you need to place the path to your "+
									   "Python executable in my_python_path.txt within the main MM-NEAT directory.");
		}

		// Run program with model architecture and weights specified as parameters
		ProcessBuilder builder = new ProcessBuilder(PythonUtil.PYTHON_EXECUTABLE, WASSERSTEIN_PATH, this.GANPath, ""+this.GANDim);
		builder.redirectError(Redirect.INHERIT); // Standard error will print to console
		try {
			System.out.println(builder.command());
			this.process = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Buffers used for communicating with process via stdin and stdout
	 */
	@Override
	public void initBuffers() {
		//Initialize input and output
		if (this.process != null) {
			this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			this.writer = new PrintStream(this.process.getOutputStream());
			System.out.println("Process buffers initialized");
		} else {
			printErrorMsg("GANProcess:initBuffers:Null process!");
		}
	}

	/**
	 * GAN process running in background, ready to accept latent vectors
	 */
	@Override
	public void start() {
		try {
			launchGAN();
			initBuffers();
			printInfoMsg(this.threadName + " has started");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}