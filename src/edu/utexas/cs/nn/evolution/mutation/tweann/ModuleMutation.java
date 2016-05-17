package edu.utexas.cs.nn.evolution.mutation.tweann;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import java.util.Arrays;

/**
 * an abstract class for mutating modules.
 * 
 * @author Jacob Schrum
 */
public abstract class ModuleMutation extends TWEANNMutation {

	/**
	 * Constructor for a moduleMutation	
	 * 
	 * @param rate rate of mutation occurrences
	 */
    public ModuleMutation(String rate) {
        super(rate);
    }

    /**
     * Mutates a TWEANN genotype
     * 
     * @param genotype the genotype to be mutated
     */
    public void mutate(Genotype<TWEANN> genotype) {
        if (CommonConstants.weakenBeforeModeMutation) {//weakens modules if needed
            ((TWEANNGenotype) genotype).weakenAllModules(Parameters.parameters.doubleParameter("weakenPortion"));
            if(infoTracking != null) {
                infoTracking.append("WEAKEN ");
            }
        }
        // Option to freeze existing network before adding new module
        if (CommonConstants.freezeBeforeModeMutation) {
            ((TWEANNGenotype) genotype).freezeNetwork();
            if(infoTracking != null) {
                infoTracking.append("FREEZE ");
            }
        }
        //this is the method that changes between mutation methods
        addModule((TWEANNGenotype) genotype);
        // This code only matters for hierarchical multitask networks.
        if(CommonConstants.hierarchicalMultitask) {
            int originalModules = ((TWEANNGenotype) genotype).moduleAssociations.length;
            int[] newmoduleAssociations = new int[originalModules + 1]; // One module added
            System.arraycopy(((TWEANNGenotype) genotype).moduleAssociations, 0, newmoduleAssociations, 0, originalModules); // Copy over old module associations
            int hierarchicalmodules = CommonConstants.multitaskModules;
            newmoduleAssociations[originalModules] = RandomNumbers.randomGenerator.nextInt(hierarchicalmodules); // Assign to random multitask module
            ((TWEANNGenotype) genotype).moduleAssociations = newmoduleAssociations;
            infoTracking.append("Assoc: ").append(Arrays.toString(newmoduleAssociations)).append(" ");
        }
    }

    abstract public void addModule(TWEANNGenotype genotype);
}
