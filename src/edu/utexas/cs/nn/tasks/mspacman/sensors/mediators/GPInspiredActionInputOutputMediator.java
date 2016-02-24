package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators;

import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.cluster.GhostClusterBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.GhostReversalBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.AtePowerPillBlock;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.actions.*;
import edu.utexas.cs.nn.tasks.mspacman.facades.GhostControllerFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.ActionBlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.*;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.*;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting.PillsRemainingBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting.PowerPillsRemainingBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.*;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.time.EdibleTimesBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.time.LairTimesBlock;
import pacman.controllers.examples.AggressiveGhosts;

/**
 * Input sensors partially based on those from paper by Alhejali and Lucas
 *
 * @author Jacob Schrum
 */
public class GPInspiredActionInputOutputMediator extends ActionBlockLoadedInputOutputMediator {

    public GPInspiredActionInputOutputMediator() {
        super();

        GhostControllerFacade ghostModel = new GhostControllerFacade(new AggressiveGhosts());

        // Base
        blocks.add(new BiasBlock());
        //blocks.add(new HittingWallBlock());
        blocks.add(new GhostReversalBlock());
        // Simple
        //blocks.add(new EscapeNodeDistanceDifferenceBlock(escapeNodes, false, false, Parameters.parameters.integerParameter("escapeNodeDepth"), true, true, true));
        //blocks.add(new NearestPillBlock());
        //blocks.add(new NearestPowerPillBlock());
        //blocks.add(new NearestFarthestEdibleGhostBlock(true));
        // Intermediate
        blocks.add(new AtePowerPillBlock());
        //blocks.add(new PowerPillAvoidanceBlock());
        //blocks.add(new GhostsToFarthestEdibleBlock(true));
        //blocks.add(new GhostsToFarthestEdibleBlock(false));
        // Advanced
        blocks.add(new GhostClusterBlock(true));
        blocks.add(new GhostClusterBlock(false));
        //blocks.add(new LairTimesBlock(new boolean[]{false, false, false, true}));
        //blocks.add(new EdibleTimesBlock(new boolean[]{false, false, false, true}));

        // New
        blocks.add(new LairTimesBlock(new boolean[]{true, true, true, true}));
        blocks.add(new EdibleTimesBlock(new boolean[]{true, true, true, true}));
        blocks.add(new GhostEatingRewardBlock());
        blocks.add(new GhostsWithinDistanceBlock(new boolean[]{true, true, true, true}, false, 10));
        blocks.add(new PillsRemainingBlock(true, true));
        blocks.add(new PowerPillsRemainingBlock(true, true));
        blocks.add(new NearestJunctionDistanceBlock());
        blocks.add(new NearestPillPathSafeBlock(ghostModel));
        blocks.add(new NearestEdibleGhostPathSafeBlock(ghostModel));

        // GP Inspired
        blocks.add(new GhostsWithinDistanceBlock(new boolean[]{true, true, true, true}, true, 10)); // IsInDanger
        blocks.add(new NearestEdibleGhostDistanceBlock()); // DISEdibleGhost
        blocks.add(new NearestThreatGhostDistanceBlock()); // DISUnedibleGhost
        blocks.add(new NearestPillDistanceBlock()); // DISPill
        blocks.add(new NearestPowerPillDistanceBlock()); // DISEnergizer
        blocks.add(new SpecificGhostDistancesBlock(true, false)); // DISGhost, DIS2ndGhost, DIS3rdGhost
        blocks.add(new AnyEdibleGhostBlock()); // IsEdible
        blocks.add(new PowerPillsClearedBlock()); // IsEnergizersCleared
        blocks.add(new NearestPowerPillPathSafeBlock(ghostModel)); // IsToEnergizerSafe

        // Actions
        actions.add(new FromNearestPowerPillAction()); // FromEnergizer
        actions.add(new FromNearestThreatAction()); // FromGhost
        actions.add(new ToFarthestSafeLocationAction(Parameters.parameters.integerParameter("escapeNodeDepth"), this.escapeNodes, ghostModel)); // ToSafety
        actions.add(new ToNearestEdibleGhostAction()); // ToEdibleGhost
        actions.add(new ToNearestPillAction()); // ToPill
        actions.add(new ToNearestPowerPillAction()); // ToEnergizer
    }
}
