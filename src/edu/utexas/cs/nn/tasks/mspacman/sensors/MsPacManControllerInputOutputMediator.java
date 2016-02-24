package edu.utexas.cs.nn.tasks.mspacman.sensors;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.data.NodeCollection;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.ClassCreation;

/**
 *
 * @author Jacob Schrum
 */
public abstract class MsPacManControllerInputOutputMediator {

    protected final int absence;
    public static NodeCollection escapeNodes = null;
    private final boolean evolveNetworkSelector;
    private final boolean externalPreferenceNeurons;

    public MsPacManControllerInputOutputMediator() {
        if (escapeNodes == null) {
            try {
                escapeNodes = (NodeCollection) ClassCreation.createObject("pacmanEscapeNodeCollection");
            } catch (NoSuchMethodException ex) {
                System.out.println("Cannot initialize escape nodes");
                System.exit(1);
            }
        }
        externalPreferenceNeurons = Parameters.parameters.booleanParameter("externalPreferenceNeurons");
        absence = Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0;
        evolveNetworkSelector = Parameters.parameters.booleanParameter("evolveNetworkSelector");
    }

    public void mediatorStateUpdate(GameFacade gs) {
        int current = gs.getPacmanCurrentNodeIndex();
        escapeNodes.updateNodes(gs, current);
    }

    public abstract double[] getInputs(GameFacade gs, final int currentDir);

    public void reset() {
        escapeNodes.reset();
    }

    public abstract String[] sensorLabels();

    public String[] outputLabels() {
        if (evolveNetworkSelector) {
            return new String[]{"Ghost Network", "Pill Network"};
        } else if (CommonConstants.relativePacmanDirections) {
            return new String[]{"Forward", "Turn Right", "Reverse", "Turn Left"};
        } else {
            return new String[]{"Up", "Right", "Down", "Left"};
        }
    }

    public int numOut() {
        // 2 is for turn/thrust
        return evolveNetworkSelector ? MMNEAT.modesToTrack : GameFacade.NUM_DIRS + (externalPreferenceNeurons ? 1 : 0);
    }

    public abstract int numIn();
}
