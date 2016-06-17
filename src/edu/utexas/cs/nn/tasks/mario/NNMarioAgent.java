package edu.utexas.cs.nn.tasks.mario;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.environments.Environment;
import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;

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
        double[] inputs = new double[]{probe(-1, -1, scene), probe(0, -1, scene), 
        							   probe(1, -1, scene), probe(-1, 0, scene), 
        							   probe(0, 0, scene), probe(1, 0, scene), 
        							   probe(-1, 1, scene), probe(0, 1, scene), 
        							   probe(1, 1, scene), 1}; // 10 inputs ? -Gab
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
