package edu.utexas.cs.nn.tasks.mspacman.sensors;

import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.HittingWallBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting.PillsRemainingBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting.PowerPillsRemainingBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.NearestEdibleGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.NearestPillDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.NearestPowerPillDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.NearestThreatGhostDistanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.SpecificGhostDistancesBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.lair.LairRelativeCoordinatesBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distancedifference.NearestEscapeNodeThreatDistanceDifferencesBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.time.EdibleTimesBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.time.LairTimesBlock;

/**
 *
 * @author Jacob Schrum
 */
public class AfterStateFeatureInputOutputMediator extends BlockLoadedInputOutputMediator {

    public AfterStateFeatureInputOutputMediator() {
        super();
        blocks.add(new BiasBlock());
        blocks.add(new HittingWallBlock());
        //blocks.add(new NearestEscapeNodeDistanceBlock(escapeNodes));
        blocks.add(new NearestEscapeNodeThreatDistanceDifferencesBlock(escapeNodes, 5));
        blocks.add(new SpecificGhostDistancesBlock(true, false));
        blocks.add(new SpecificGhostDistancesBlock(false, true));
        blocks.add(new NearestEdibleGhostDistanceBlock());
        blocks.add(new NearestThreatGhostDistanceBlock());
        blocks.add(new NearestPillDistanceBlock());
        blocks.add(new NearestPowerPillDistanceBlock());
        blocks.add(new PillsRemainingBlock(true, true));
        blocks.add(new PowerPillsRemainingBlock(true, true));
        blocks.add(new LairRelativeCoordinatesBlock());
        blocks.add(new LairTimesBlock(new boolean[]{false,false,false,true}));
        blocks.add(new EdibleTimesBlock(new boolean[]{false,false,false,true}));
    }

    @Override
    public String[] outputLabels() {
        return new String[]{"Preference"};
    }

    @Override
    public int numOut() {
        return 1;
    }
}
