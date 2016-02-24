package edu.utexas.cs.nn.evolution.mutation.tweann;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import java.util.Arrays;

/**
 * @author Jacob Schrum
 */
public abstract class ModuleMutation extends TWEANNMutation {

    public ModuleMutation(String rate) {
        super(rate);
    }

    public void mutate(Genotype<TWEANN> genotype) {
        if (CommonConstants.weakenBeforeModeMutation) {
            // 0.5 is weakening proportion ... make param
            ((TWEANNGenotype) genotype).weakenAllModes(Parameters.parameters.doubleParameter("weakenPortion"));
            if(infoTracking != null) {
                infoTracking.append("WEAKEN ");
            }
        }
        // Option to freeze existing network before adding new mode
        if (CommonConstants.freezeBeforeModeMutation) {
            ((TWEANNGenotype) genotype).freezeNetwork();
            if(infoTracking != null) {
                infoTracking.append("FREEZE ");
            }
        }
        addMode((TWEANNGenotype) genotype);
        // This code only matters for hierarchical multitask networks.
        if(CommonConstants.hierarchicalMultitask) {
            int originalModules = ((TWEANNGenotype) genotype).modeAssociations.length;
            int[] newModeAssociations = new int[originalModules + 1]; // One module added
            System.arraycopy(((TWEANNGenotype) genotype).modeAssociations, 0, newModeAssociations, 0, originalModules); // Copy over old module associations
            int hierarchicalModes = CommonConstants.multitaskModes;
            newModeAssociations[originalModules] = RandomNumbers.randomGenerator.nextInt(hierarchicalModes); // Assign to random multitask mode
            ((TWEANNGenotype) genotype).modeAssociations = newModeAssociations;
            infoTracking.append("Assoc: ").append(Arrays.toString(newModeAssociations)).append(" ");
        }
    }

    abstract public void addMode(TWEANNGenotype genotype);
}
