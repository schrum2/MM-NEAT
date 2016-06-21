package edu.utexas.cs.nn.tasks.mario;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.environments.Environment;
import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;

public class NNMarioAgent<T extends Network> extends Organism<T> implements Agent {

	Network n;
	private String name = "NNMarioAgent";
	
	public NNMarioAgent(Genotype<T> genotype) {
		super(genotype);
		n = genotype.getPhenotype();
	}

	/**
	 * Resets the network (Phenotype) of the agent
	 */
	@Override
	public void reset() {
		n.flush();
	}

	@Override
	public boolean[] getAction(Environment observation) {
		byte[][] scene = observation.getLevelSceneObservation(/*1*/);
		int xStart = Parameters.parameters.integerParameter("marioInputStartX");
		int yStart = Parameters.parameters.integerParameter("marioInputStartY");
		int width = Parameters.parameters.integerParameter("marioInputWidth");
		int height = Parameters.parameters.integerParameter("marioInputHeight");
		int xEnd = width + xStart;
		int yEnd = height + yStart;
		int buffer = 0;		
		double[] inputs = new double[(width * height) + 1];
		//System.out.println("x start: " + xStart);
		//System.out.println("y start: " + yStart);
		//System.out.println("x end: " + xEnd);
		//System.out.println("y end: " + yEnd);
		
		for(int y = yStart; y < yEnd; y++){
			for(int x = xStart; x < xEnd; x++){
				inputs[buffer++] = probe(x, y, scene);
				//System.out.print("probe(" + x + ", " + y + "), ");
			}
		}
		inputs[buffer++] = 1;
		//System.out.println("Buffer: " + buffer + " and inputs size: " + inputs.length);

        double[] outputs = n.process(inputs);
        boolean[] action = new boolean[outputs.length];
        for (int i = 0; i < action.length; i++) {
            action[i] = outputs[i] > 0;
        }
        return action;
	}

	/**
	 * Getter for the Agent type
	 */
	@Override
	public AGENT_TYPE getType() {
		return AGENT_TYPE.AI;
	}

	/**
	 * Getter for the Agent name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Setter for Agent name
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Using the scene (byte[][]) determines if the (x, y) is 1 or 0
	 * @param x
	 * @param y
	 * @param scene
	 * @return
	 */
	private double probe (int x, int y, byte[][] scene) {
        int realX = x + 11; // unsure about these magic numbers -Gab
        int realY = y + 11;
        return (scene[realX][realY] != 0) ? 1 : 0;
    }

}
