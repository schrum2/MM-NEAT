/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 *
 * @author Jacob
 */
public class WallDistanceBlock extends MsPacManSensorBlock {

	@Override
	public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
		int pacman = gf.getPacmanCurrentNodeIndex();
		for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
			inputs[in++] = Math.min(GameFacade.MAX_DISTANCE, distanceToWallInDir(gf, pacman, i))
					/ (GameFacade.MAX_DISTANCE * 1.0);
		}
		return in;
	}

	public static int distanceToWallInDir(GameFacade gf, int start, int dir) {
		int distance = 0;
		while (gf.neighbors(start)[dir] != -1) {
			distance++;
			start = gf.neighbors(start)[dir];
		}
		return distance;
	}

	@Override
	public int incorporateLabels(String[] labels, int in) {
		labels[in++] = "Distance to Wall Up";
		labels[in++] = "Distance to Wall Right";
		labels[in++] = "Distance to Wall Down";
		labels[in++] = "Distance to Wall Left";
		return in;
	}

	@Override
	public int numberAdded() {
		return GameFacade.NUM_DIRS;
	}

}
