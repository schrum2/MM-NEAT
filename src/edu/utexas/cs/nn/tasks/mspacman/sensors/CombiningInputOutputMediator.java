package edu.utexas.cs.nn.tasks.mspacman.sensors;

import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.combining.GhostEatingNetworkBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.combining.PillEatingNetworkBlock;

/**
 *
 * @author Jacob Schrum
 */
public class CombiningInputOutputMediator extends BlockLoadedInputOutputMediator {

    public CombiningInputOutputMediator() {
        super();
        try {
            blocks.add(new GhostEatingNetworkBlock());
            blocks.add(new PillEatingNetworkBlock());
        } catch (NoSuchMethodException ex) {
            System.out.println("Sub mediator classes not loading properly");
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
