package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.actions.*;
import edu.utexas.cs.nn.tasks.mspacman.facades.GhostControllerFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.ActionBlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.AtePowerPillBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.cluster.GhostClusterBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.GhostReversalBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.PowerPillAvoidanceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.HittingWallBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distancedifference.EscapeNodeDistanceDifferenceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.nearestfarthest.NearestFarthestEdibleGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.nearestfarthest.NearestPillBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.nearestfarthest.NearestPowerPillBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.paths.GhostsToFarthestEdibleBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.time.EdibleTimesBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.time.LairTimesBlock;
import pacman.controllers.examples.AggressiveGhosts;

/**
 *
 * @author Jacob Schrum
 */
public class DirectionalToActionInputOutputMediator extends ActionBlockLoadedInputOutputMediator {

    public DirectionalToActionInputOutputMediator() {
        super();
        // Base
        blocks.add(new BiasBlock());
        blocks.add(new HittingWallBlock());
        blocks.add(new GhostReversalBlock());
        // Simple
        blocks.add(new EscapeNodeDistanceDifferenceBlock(escapeNodes, false, false, Parameters.parameters.integerParameter("escapeNodeDepth"), true, true, true));
        blocks.add(new NearestPillBlock());
        blocks.add(new NearestPowerPillBlock());
        blocks.add(new NearestFarthestEdibleGhostBlock(true));
        // Intermediate
        blocks.add(new AtePowerPillBlock());
        blocks.add(new PowerPillAvoidanceBlock());
        blocks.add(new GhostsToFarthestEdibleBlock(true));
        blocks.add(new GhostsToFarthestEdibleBlock(false));
        // Advanced
        blocks.add(new GhostClusterBlock(true));
        blocks.add(new GhostClusterBlock(false));
        blocks.add(new LairTimesBlock(new boolean[]{false, false, false, true}));
        blocks.add(new EdibleTimesBlock(new boolean[]{false, false, false, true}));

        GhostControllerFacade ghostModel = new GhostControllerFacade(new AggressiveGhosts());

        // Actions
        actions.add(new FromNearestPowerPillAction());
        actions.add(new FromNearestThreatAction());
        actions.add(new ToFarthestSafeLocationAction(Parameters.parameters.integerParameter("escapeNodeDepth"), this.escapeNodes, ghostModel));
        actions.add(new ToNearestEdibleGhostAction());
        actions.add(new ToNearestPillAction());
        actions.add(new ToNearestPowerPillAction());
    }
}
