package edu.utexas.cs.nn.tasks.vizdoom;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import vizdoom.Button;
import vizdoom.GameState;
import vizdoom.GameVariable;

public class VizDoomBasicShootTask<T extends Network> extends VizDoomTask<T> {

	public VizDoomBasicShootTask() {
		super();
	}

    @Override
    public void taskSpecificInit() {
        game.loadConfig("vizdoom/examples/config/basic.cfg");
		game.setDoomScenarioPath("vizdoom/scenarios/basic.wad");
		game.setDoomMap("map01");
	}
        
        @Override
	public void setDoomActions() {
		// Adds buttons that will be allowed.
		game.addAvailableButton(Button.MOVE_LEFT);
		game.addAvailableButton(Button.MOVE_RIGHT);
		game.addAvailableButton(Button.ATTACK);

		// Define some actions. Each list entry corresponds to declared buttons:
		// MOVE_LEFT, MOVE_RIGHT, ATTACK
		// more combinations are naturally possible but only 3 are included for
		// transparency when watching.
		addAction(new int[] { 0, 0, 1 }, "Still and Shoot");
		addAction(new int[] { 1, 0, 0 }, "Left");
		addAction(new int[] { 0, 1, 0 }, "Right");
	}

	@Override
	public void setRewards() {
		game.setLivingReward(-1);
	}
	
	@Override
	public void setDoomStateVariables() {
		// Adds game variables that will be included in state.
		game.addAvailableGameVariable(GameVariable.AMMO2);
	}

	@Override
	public int numInputs() {
		int smudge = Parameters.parameters.integerParameter("doomInputPixelSmudge");
		int width = Parameters.parameters.integerParameter("doomInputWidth") / smudge;
		int height = Parameters.parameters.integerParameter("doomInputHeight") / smudge;
		
		if(Parameters.parameters.integerParameter("doomInputColorVal") == 3){
			return (width * height * 3);
		}
		return (width * height);
	}

	/**
	 * Inputs are from a single row in roughly the middle of the screen. The row
	 * should cross through the eye of the monster, creating a high contrast.
	 * Also, only the red color values are checked.
	 * 
	 * @param s
	 *            Game state
	 * @return scaled red color values from specified row
	 */
	@Override
	public double[] getInputs(GameState s) {
		double[] inputs = getInputs(s, Parameters.parameters.integerParameter("doomInputStartX"), 
				Parameters.parameters.integerParameter("doomInputStartY"), 
				Parameters.parameters.integerParameter("doomInputWidth"), 
				Parameters.parameters.integerParameter("doomInputHeight"), 
				Parameters.parameters.integerParameter("doomInputColorVal"));
		if(Parameters.parameters.integerParameter("doomInputPixelSmudge") > 1){
			return smudgeInputs(inputs, Parameters.parameters.integerParameter("doomInputWidth"), 
					Parameters.parameters.integerParameter("doomInputHeight"), 
					Parameters.parameters.integerParameter("doomInputColorVal"), 
					Parameters.parameters.integerParameter("doomInputPixelSmudge"));
		}else{
			return inputs;
		}
	}
        
	/**
	 * For this particular task, the labels should match the row inputs
	 * (coordinates)
	 * 
	 * @return
	 */
	@Override
	public String[] sensorLabels() {
		return getSensorLabels(Parameters.parameters.integerParameter("doomInputStartX"), 
				Parameters.parameters.integerParameter("doomInputStartY"), 
				(Parameters.parameters.integerParameter("doomInputWidth") / Parameters.parameters.integerParameter("doomInputPixelSmudge")), 
				(Parameters.parameters.integerParameter("doomInputHeight") / Parameters.parameters.integerParameter("doomInputPixelSmudge")), 
				Parameters.parameters.integerParameter("doomInputColorVal"));
	}

	/**
	 * Test run in the domain
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[] { "watch:true", "io:false", "netio:false",
				"task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomBasicShootTask", "trials:1", "printFitness:true", 
				"doomFullScreenInput:false", "doomInputWidth:20", "doomInputHeight:10", "doomInputStartX:50", 
				"doomInputStartY:50","doomInputColorVal:2", "doomInputPixelSmudge:4"});
		MMNEAT.loadClasses();
		VizDoomBasicShootTask<TWEANN> vd = new VizDoomBasicShootTask<TWEANN>();
		TWEANNGenotype individual = new TWEANNGenotype();
		System.out.println(vd.evaluate(individual));
		System.out.println(vd.evaluate(individual));
		vd.finalCleanup();
	}
}
