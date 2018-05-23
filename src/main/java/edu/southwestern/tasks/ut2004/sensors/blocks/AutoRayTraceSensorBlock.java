package edu.southwestern.tasks.ut2004.sensors.blocks;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.RemoveRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import cz.cuni.amis.utils.flag.FlagListener;
import edu.southwestern.tasks.ut2004.Util;
import java.util.ArrayList;
import javax.vecmath.Vector3d;

/**
 *Allows the bot to figure out where it is being shot from
 * @author Jacob Schrum
 */
public class AutoRayTraceSensorBlock implements UT2004SensorBlock {

	public static int RAY_LENGTH = 5000;
	public static int NUMBER_LEVEL_RAY_SENSORS = 12;
	private ArrayList<String> autoRayIds;
	private ArrayList<AutoTraceRay> rays;

	/**
	 * creates the sensor block
	 */
	public AutoRayTraceSensorBlock() {
		autoRayIds = new ArrayList<String>(NUMBER_LEVEL_RAY_SENSORS + 2);
		autoRayIds.add("Crosshair");
		for (int i = 0; i < NUMBER_LEVEL_RAY_SENSORS; i++) {
			autoRayIds.add("Wall" + i);
		}
	}

	/**
	 * sets up the 
	 */
	public void prepareBlock(final UT2004BotModuleController bot) {
		bot.getAct().act(new RemoveRay("All"));
		this.rays = new ArrayList<AutoTraceRay>(NUMBER_LEVEL_RAY_SENSORS + 2);

		int id = 0;
		addRayToAutoTrace(bot, id++, new Vector3d(1, 0, 0), RAY_LENGTH, false, true);

		// Rays parallel to even ground
		double angle = (2.0 * Math.PI) / NUMBER_LEVEL_RAY_SENSORS;
		for (int i = 0; i < NUMBER_LEVEL_RAY_SENSORS; i++) {
			double x = Math.cos(angle * i);
			double y = Math.sin(angle * i);
			// Not tracing actors here, only walls
			addRayToAutoTrace(bot, id++, new Vector3d(x, y, 0), RAY_LENGTH, false, false);
		}

		// Not sure if this is still a bug in the latest version of Pogamut
		/**
		 * Dummy trace added because of ridiculous bug in Pogamut 3 that freezes
		 * things up if all autotraces are accessed. Therefore, this trace is
		 * made in order to never be accessed.
		 */
		// bot.getRaycasting().createRay("DUMMY", new Vector3d(0, 0, 0), 0,
		// false, false, false);
		bot.getRaycasting().getAllRaysInitialized().addListener(new FlagListener<Boolean>() {
			@Override
			public void flagChanged(Boolean changedValue) {
				// System.out.println("Rays updating");
				for (int i = 0; i < autoRayIds.size(); i++) {
					// System.out.println("\tGet " + autoRayIds.get(i));
					rays.set(i, bot.getRaycasting().getRay(autoRayIds.get(i)));
				}
			}
		});

		bot.getRaycasting().endRayInitSequence();
		bot.getAct().act(new Configuration().setDrawTraceLines(true).setAutoTrace(true));
	}

	public void addRayToAutoTrace(UT2004BotModuleController bot, int id, Vector3d v, int rayLength, boolean bFastTrace,
			boolean bTraceActors) {
		// Floor correction is always false
		boolean bFloorCorrection = false;
		rays.add(null); // Place holder for actual ray data
		// System.out.println("\tInit ray: " + autoRayIds.get(id));
		bot.getRaycasting().createRay(autoRayIds.get(id), v, rayLength, bFastTrace, bFloorCorrection, bTraceActors);
	}

	public int incorporateSensors(UT2004BotModuleController bot, int in, double[] inputs) {
		for (int i = 0; i < numberOfSensors(); i++) {
			AutoTraceRay trace = rays.get(i);
			// System.out.println(trace);
			Location botLocation = bot.getInfo().getLocation();
			Location hitLocation = trace.getHitLocation();
			double distance = (botLocation == null || hitLocation == null) ? 0 : (botLocation.getDistance(hitLocation));
			inputs[in++] = trace.isResult() ? Util.scale(distance, RAY_LENGTH) : 0;
		}
		return in;
	}

	public int incorporateLabels(int in, String[] labels) {
		for (int i = 0; i < numberOfSensors(); i++) {
			labels[in++] = "Ray Trace: " + autoRayIds.get(i);
		}
		return in;
	}

	/**
	 * Wall traces and the crosshair trace
	 *
	 * @return
	 */
	public int numberOfSensors() {
		return NUMBER_LEVEL_RAY_SENSORS + 1;
	}
}
