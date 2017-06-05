package edu.utexas.cs.nn.tasks.interactive.remixbreeder;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.interactive.picbreeder.PicbreederTask;

public class PictureRemixTask<T extends Network> extends PicbreederTask {
	
	public static final int CPPN_NUM_INPUTS	= 7;
	
	public String inputImage;

	public PictureRemixTask() throws IllegalAccessException {
		super();
		inputImage = Parameters.parameters.stringParameter("matchImageFile");
		
		//do I need to use ImageIO and read in the image to access the height and width?
		
		
	}
	
	@Override
	public String[] sensorLabels() {
		return new String[]{"X-coordinate", "Y-coordinate", "distance from center", "Picture H", "Picture S", "Picture V", "bias"};
	}
	
	@Override
	protected String getWindowTitle() {
		return "PictureRemix";
	}
	
	@Override
	public int numCPPNInputs() {
		return CPPN_NUM_INPUTS;
	}

	

}
