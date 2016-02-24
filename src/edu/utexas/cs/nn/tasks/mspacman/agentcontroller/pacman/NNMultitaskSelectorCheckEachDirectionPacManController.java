package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.lineage.Offspring;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.graphics.Plot;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class NNMultitaskSelectorCheckEachDirectionPacManController extends NNDirectionalPacManController {

    // Needed so each direction can have its own recurrent state
    private Network[] directionalPolicyNetworks;
    private Network[] directionalPreferenceNetworks;
    private static DrawingPanel[] policyPanels = null;
    private static DrawingPanel[] preferencePanels = null;
    private final VariableDirectionBlock safe;

    public NNMultitaskSelectorCheckEachDirectionPacManController(Genotype<? extends Network> policy, Genotype<? extends Network> preference, VariableDirectionBlock safe) {
        super(policy.getPhenotype());
        this.safe = safe;
        directionalPolicyNetworks = new Network[GameFacade.NUM_DIRS];
        directionalPreferenceNetworks = new Network[GameFacade.NUM_DIRS];
        for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
            directionalPolicyNetworks[i] = policy.getPhenotype(); // Each is a different copy
            directionalPreferenceNetworks[i] = preference.getPhenotype(); // Each is a different copy
        }
        if (CommonConstants.monitorInputs) {
            TWEANN.inputPanel.dispose();
            // Dispose of existing panels
            policyPanels = new DrawingPanel[GameFacade.NUM_DIRS];
            preferencePanels = new DrawingPanel[GameFacade.NUM_DIRS];
            refreshPanels(policyPanels, (TWEANNGenotype) policy, "Policy");
            refreshPanels(preferencePanels, (TWEANNGenotype) preference, "Preference");
        }
    }

    public static void refreshPanels(DrawingPanel[] panels, TWEANNGenotype policy, String label) {
        if (panels != null) {
            for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
                if(panels[i] != null) {
                    panels[i].dispose();
                }
            }
        }
        for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
            panels[i] = new DrawingPanel(Plot.BROWSE_DIM, (int) (Plot.BROWSE_DIM * 3.5), label + " Direction " + i);
            panels[i].setLocation(i * (Plot.BROWSE_DIM + 10), 0);
            Offspring.fillInputs(panels[i], policy);
        }
    }

    @Override
    public double[] getDirectionPreferences(GameFacade gf) {
        double[] preferences = new double[GameFacade.NUM_DIRS];
        Arrays.fill(preferences, -1);   // -1 is lowest possible value after activation function scaling
        final int current = gf.getPacmanCurrentNodeIndex();
        final int[] neighbors = gf.neighbors(current);
        for (int i = 0; i < neighbors.length; i++) {
            if (neighbors[i] != -1) {
                ((VariableDirectionBlockLoadedInputOutputMediator) this.inputMediator).setDirection(i);
                double[] inputs = this.inputMediator.getInputs(gf, gf.getPacmanLastMoveMade());
                // Get preferences
                if (preferencePanels != null) {
                    TWEANN.inputPanel = preferencePanels[i];
                }
                double[] preferenceOutputs = this.directionalPreferenceNetworks[i].process(inputs);
                // Determine mode
                int mode = StatisticsUtilities.argmax(preferenceOutputs);
                // Get policy
                if (policyPanels != null) {
                    TWEANN.inputPanel = policyPanels[i];
                }
                this.directionalPolicyNetworks[i].chooseMode(mode);
                double[] policyOutput = this.directionalPolicyNetworks[i].process(inputs);
                preferences[i] = policyOutput[0];
            } else {
                if (policyPanels != null) {
                    TWEANN.inputPanel = policyPanels[i];
                }
                if(CommonConstants.checkEachFlushWalls) {
                    this.directionalPolicyNetworks[i].flush(); // Nothing sensed from wall
                }
            }
        }
        // Should unsafe directions be excluded?
        if (safe != null) {
            boolean anySafe = false;
            boolean[] safeDirections = new boolean[preferences.length];
            for (int i = 0; i < preferences.length; i++) {
                if (neighbors[i] != -1) {
                    safe.setDirection(i);
                    safeDirections[i] = safe.getValue(gf) > 0;
                    anySafe = anySafe || safeDirections[i];
                }
            }
            // If any direction is safe, then only consider the safe ones.
            // Otherwise, consider all and pick the best of the bad.
            if (anySafe) {
                //System.out.println("Safe " + gf.getPacmanCurrentNodeIndex());
                for (int i = 0; i < safeDirections.length; i++) {
                    if (!safeDirections[i]) {
                        preferences[i] = -1;
                    }
                }
            }
        }
        return preferences;
    }

    @Override
    public void reset() {
        super.reset();
        for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
            directionalPolicyNetworks[i].flush();
            directionalPreferenceNetworks[i].flush();
        }
    }
}
