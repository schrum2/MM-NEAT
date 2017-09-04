package edu.southwestern.tasks.gridTorus;

/**
 * Imports needed parts to initialize the PredPreyAgent, as in organism, genotypes, and networks
 */
import edu.southwestern.evolution.Organism;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;

/**
 * 
 * @author Jacob Schrum, Gabby Gonzalez, Alex Rollins The following class sets
 *         up the PredPreyAgent to utilize Organism and Network and initializes
 *         the brain and controller.
 */
public class NNTorusPredPreyAgent<T extends Network> extends Organism<T> {

	// These were added so that HyperNEAT teams can share the same network
	// rather than make copies of it.
	private static long networkGenotypeID = -1;
	private static Network storedNN = null;
	
	/**
	 * Initialize controller
	 */
	public NNTorusPredPreyController controller;

	/**
	 * Uses the genotype to finish the set up of the controller. It accesses the
	 * network to do this.
	 * 
	 * @param genotype
	 *            encodes agent phenotype controller
	 * @param isPredator
	 *            true if this agent is a predator, false if it is a prey
	 */
	public NNTorusPredPreyAgent(Genotype<T> genotype, boolean isPredator) {
		super(genotype);
		// Since HyperNEAT networks are large, and (for this domain) do not allow recurrent links,
		// it is both safe and efficient to simply share one network across all team members
		// rather than have multiple copies of the same network. This results in a minor speedup.
		if(CommonConstants.hyperNEAT && networkGenotypeID != getGenotype().getId()) {
			storedNN = (Network) getGenotype().getPhenotype();
			networkGenotypeID = getGenotype().getId();
		}
		Network net = CommonConstants.hyperNEAT ? storedNN : (Network) getGenotype().getPhenotype();
		controller = Parameters.parameters.booleanParameter("hyperNEAT")
				? new HyperNEATNNTorusPredPreyController(net, isPredator)
				: new NNTorusPredPreyController(net, isPredator);
	}

	/**
	 * Getter function to access the controller of the PredPreyAgent
	 * 
	 * @return controller
	 */
	public NNTorusPredPreyController getController() {
		return controller;
	}
}
