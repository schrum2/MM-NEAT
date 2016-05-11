package edu.utexas.cs.nn.tasks.gridTorus;

/**
 * Imports needed parts to initialize the PredPreyAgent, as in organism, genotypes, and networks
 */
import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;

/**
 * 
 * @author Jacob Schrum, Gabby Gonzalez
 * The following class sets up the PredPreyAgent to utilize Organism and Network and initializes the brain and controller.
 */
public class NNTorusPredPreyAgent<T extends Network> extends Organism<T> {
	
	/**
	 * Initialize brain/controller
	 */
    public NNTorusPredPreyController controller; 

    /**
     * Uses the genotype to finish the set up of the brain/controller. It accesses the network to do this.
     * @param genotype
     */
    public NNTorusPredPreyAgent(Genotype<T> genotype) {
        super(genotype); 
        Network net = (Network) this.getGenotype().getPhenotype();
        controller = new NNTorusPredPreyController(net); 
    }

    /**
     * Getter function to access the brain/controller of the PredPreyAgent
     * @return controller
     */
    public NNTorusPredPreyController getController() {
        return controller; 
    }
}
