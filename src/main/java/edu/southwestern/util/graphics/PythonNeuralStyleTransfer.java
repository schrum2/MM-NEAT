package edu.southwestern.util.graphics;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.PythonUtil;

/**
 * This class interfaces with a Python program that uses
 * Tensorflow to execute the Neural Style Transfer algorithm.
 * 
 * @author Jacob Schrum
 *
 */
public class PythonNeuralStyleTransfer {

	public static class NeuralStyleTransferProcess extends Thread {
	    protected BufferedReader reader;
	    protected PrintStream writer;
	    protected Process process;

	    // For tracking messages to/from Python process
	    public static final int SUBSTRING_LENGTH = 20;
	    
	    public static final String PYTHON_PATH = "." + File.separator + "src" + File.separator + "main" + File.separator + "python" + File.separator + "NeuralStyleTransfer" + File.separator;
	    public static final String PYTHON_PROGRAM = "neural_style_json.py";
	    // You need to download this file yourself and store in the PYTHON_PATH: http://www.vlfeat.org/matconvnet/models/beta16/imagenet-vgg-verydeep-19.mat
	    public static final String VGG_NET_FILE = "imagenet-vgg-verydeep-19.mat"; 
	    
	    private String contentImage;

	    /**
	     * Create Python process that runs Neural Style Transfer algorithm
	     * by providing the content image to have a style transferred on to it
	     * (styles sent to program as json strings).
	     * @param content Full path and name of content image file
	     */
	    public NeuralStyleTransferProcess(String content) {
	    	contentImage = content;
	    }
	    	    
	    /**
	     * Launches the actual Python process that waits for input
	     */
	    protected void launchPythonProcess() {
	    	PythonUtil.setPythonProgram();
	    	ProcessBuilder builder = new ProcessBuilder(PythonUtil.PYTHON_EXECUTABLE, PYTHON_PATH+PYTHON_PROGRAM, 
	    												"--network", PYTHON_PATH+VGG_NET_FILE, 
	    												"--content", contentImage,
	    												"--iterations", ""+Parameters.parameters.integerParameter("neuralStyleIterations"),
	    												"--style-layer-weight-exp", ""+Parameters.parameters.doubleParameter("neuralStyleStyleWeight"));
	    	try {
	    		System.out.println("Run:" + builder.command());
	    		builder.redirectError(Redirect.INHERIT); // Standard error will print to console
	    		this.process = builder.start();
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    		System.out.println("Python process crashed");
	    		System.exit(1);
	    	}
	    }
	    
	    /**
	     * Start Python process and maintain Java thread for communicating with it
	     * via stdin and stdout.
	     */
	    public void start() {
	    	launchPythonProcess();
	    	this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            this.writer = new PrintStream(this.process.getOutputStream());
            System.out.println("Process buffers initialized");
	    }
	    
	    /**
	     * Send arbitrary text to the Python process stdin
	     * @param text String to send
	     */
	    public void send(String text) {
	    	assert process.isAlive() : "Cannot send data to dead process that exited with " + process.exitValue();
	    	// Arbitrary substring to track progress of process
	    	System.out.println("Sending:" + text.substring(0, Math.min(SUBSTRING_LENGTH, text.length())));
	        writer.println(text); // Send to stdin of process
	        writer.flush(); // flush to make sure text is received
	    }
	    
	    /**
	     * Block and wait for Python process to print a line of text to stdout,
	     * which is captured and returned here
	     * @return Line of text output by Python process
	     */
	    public String receive(){
	    	assert process.isAlive() : "Cannot receive data from dead process that exited with " + process.exitValue();
	    	String msg = null;
	    	try {
	    		msg = reader.readLine();
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    		System.out.println("Error reading from Python process");
	    		System.exit(1);
	    	}
	    	System.out.println("Receiving:" + msg.substring(0, Math.min(SUBSTRING_LENGTH, msg.length())));
	    	return msg;
	    }

	    /**
	     * Keep reading lines from stdout of process until target string is detected
	     * @param endString String to wait for
	     */
		public void waitForOutput(String endString) {
			String response = "";
			while(!response.equals(endString)) {
				response = receive();
			}
		}
		
		/**
		 * terminate the process
		 */
		public void terminate() {
			System.out.println("Terminate Python Process");
			if(process != null) process.destroyForcibly();
		}
	}
	
	// Assume there is only one
	private static NeuralStyleTransferProcess process = null;
	
	/**
	 * Decides the content image that will have its style transformed, and starts
	 * the Python process running in the background.
	 * @param contentImagePath Path to content image
	 */
	public static void initiateNeuralStyleTransferProcess(String contentImagePath) {
		process = new NeuralStyleTransferProcess(contentImagePath);
		process.start();
		process.waitForOutput("READY");
		System.out.println("Python process is ready");
	}
	
	/**
	 * Return a json array representation of the image on a single line.
	 * This representation can be accepted by Python/Tensorflow/numpy.
	 * For each pixel of the image, the R,G,B values are printed as ints
	 * @param image An image to convert to json
	 * @return String with single line that is json representation of image
	 */
	public static String imageToJson(BufferedImage image) {
		StringBuilder sb = new StringBuilder();
		sb.append("["); // Opening brackets
		for(int j = 0; j < image.getHeight(); j++) {
			sb.append("[");
			for(int i = 0; i < image.getWidth(); i++) {
				int color = image.getRGB(i, j);
				Color c = new Color(color);
				sb.append("["+c.getRed()+".0, ");
				sb.append(c.getGreen()+".0, ");
				sb.append(c.getBlue()+".0]");
				if(i < image.getWidth() - 1) {
					sb.append(", "); // separate by comma, except for final bracket
				}
			}
			sb.append("]");
			if(j < image.getHeight() - 1) {
				sb.append(", "); // separate by comma, except for final bracket
			}
		}
		sb.append("]"); // Closing brackets
		return sb.toString();
	}
	
	/**
	 * This method specifically assumes that the json String represents an
	 * image as a list of lists of lists of doubles, but those doubles are
	 * rounded down to ints to be returned by this method.
	 * @param json json string of list of lists of lists
	 * @return Java List of Lists of Lists of Integers
	 */
    public static List<List<List<Integer>>> jsonToIntLists(String json) {
        JsonArray jarray1 = new Gson().fromJson(json, JsonArray.class);
    
    	List<List<List<Integer>>> myReturnList = new ArrayList<List<List<Integer>>>(jarray1.size());
    	
    	for(int i = 0; i < jarray1.size();i++) {
    		JsonArray jarrayi = ((JsonArray)jarray1.get(i));
    		List<List<Integer>> myFirstSubList = new ArrayList<List<Integer>>(jarrayi.size());
    		for(int j = 0; j < jarrayi.size();j++) {
    			JsonArray jarrayj = ((JsonArray)jarrayi.get(j));
    			List<Integer> mySecondSubList = new ArrayList<Integer>(jarrayj.size());
    			for(JsonElement je: jarrayj) {
    				// The json numbers will be doubles, but need to be cast to int
    				mySecondSubList.add((int) je.getAsDouble());
    			}
    			myFirstSubList.add(mySecondSubList);
    		}
    		myReturnList.add(myFirstSubList);
    	}	
    	return myReturnList;
    }
	
    /**
     * Intermediate representation of image as list of lists of lists is converted to image.
     * @param list Output of jsonToIntLists
     * @return Corresponding image
     */
	public static BufferedImage imageFromListRepresentation(List<List<List<Integer>>> list) {
		int imageHeight = list.size();
		int imageWidth = list.get(0).size();
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < imageHeight; y++) {
			for (int x = 0; x < imageWidth; x++) {// scans across whole image
				List<Integer> rgb = list.get(y).get(x);				
				//System.out.println(rgb);
				rgb.replaceAll(new UnaryOperator<Integer>() {
					// Clip values out of the appropriate color range
					@Override
					public Integer apply(Integer t) {
						// Allowable integer range for colors is [0,255]
						return new Integer(Math.max(0, Math.min(t, 255)));
					}
				});
				Color color = new Color(rgb.get(0),rgb.get(1),rgb.get(2));
				image.setRGB(x, y, color.getRGB());
			}
		}
		return image;
	}
    
	/**
	 * Send a style image to the neural style transfer Python process,
	 * and retrieve the combo image it produces by applying the given
	 * style to the previously provided content image.
	 * @param image Style image for neural style transfer
	 * @return content image with style of the provided image
	 */
	public static BufferedImage sendStyleImage(BufferedImage image) {
		assert process != null : "Python process for Neural Style Transfer not initialized!";
		String json = imageToJson(image);
		process.send(json);
		String output = process.receive();
		List<List<List<Integer>>> listRepresentation = jsonToIntLists(output);
		BufferedImage combo = imageFromListRepresentation(listRepresentation);
		return combo;
	}
	
	/**
	 * Terminate the Python process
	 */
	public static void terminatePythonProcess() {
		if(process != null) {
			process.terminate();
			process = null;
		}
	}
	
	// For testing and troubleshooting
	public static void main(String[] args) throws IOException {
		Parameters.initializeParameterCollections(new String[0]);
		
		String styleFile = "data/imagematch/supercreepypersonimage.jpg";
		BufferedImage styleImage = ImageIO.read(new File(styleFile));
		
		initiateNeuralStyleTransferProcess("."+File.separator+"data"+File.separator+"imagematch"+File.separator+"theScream.png");
		BufferedImage result = sendStyleImage(styleImage);
		DrawingPanel dp = GraphicsUtil.drawImage(result, "Combo", result.getWidth(), result.getHeight());
		MiscUtil.waitForReadStringAndEnterKeyPress();
		dp.dispose();
	}
}
